// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.chtrembl.petstoreassistant;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.model.Prompt;
import com.chtrembl.petstoreassistant.service.AzureAIServices.Classification;
import com.chtrembl.petstoreassistant.service.IAzureAIServices;
import com.chtrembl.petstoreassistant.service.IAzurePetStore;
import com.chtrembl.petstoreassistant.service.ICosmosDB;
import com.chtrembl.petstoreassistant.utility.PetStoreAssistantUtilities;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.UserState;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.ChannelAccount;

/**
 * This class implements the functionality of the Bot.
 *
 * <p>
 * This is where application specific logic for interacting with the users would
 * be added. For this
 * sample, the {@link #onMessageActivity(TurnContext)} echos the text back to
 * the user. The {@link
 * #onMembersAdded(List, TurnContext)} will send a greeting to new conversation
 * participants.
 * </p>
 */
@Component
@Primary
public class PetStoreAssistantBot extends ActivityHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreAssistantBot.class);

    @Autowired
    private IAzureAIServices azureOpenAI;

    @Autowired
    private IAzurePetStore azurePetStore;

    @Autowired
    private ICosmosDB cosmosDB;

    private String WELCOME_MESSAGE = "Hello and welcome to the Azure Pet Store, you can ask me questions about our products, your shopping cart and your order, you can also ask me for information about pet animals. How can I help you?";

    private UserState userState;

    Cache<String, AzurePetStoreSessionInfo> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public PetStoreAssistantBot(UserState withUserState) {
        this.userState = withUserState;
    }

    // onTurn processing isn't working with DP, not being used to manage state...
    @Override
    public CompletableFuture<Void> onTurn(TurnContext turnContext) {
        try
        {
            String text = turnContext.getActivity().getText().toLowerCase().trim();
            LOGGER.info("onTurn incoming text: " + turnContext.getActivity().getText());
        
            if (isErroneousRequest(text)) {
                return null;
            }

            AzurePetStoreSessionInfo azurePetStoreSessionInfo = configureSession(turnContext, text);

            if (azurePetStoreSessionInfo != null && azurePetStoreSessionInfo.getNewText() != null) {
                // get the text without the session id and csrf token
                text = azurePetStoreSessionInfo.getNewText();
            }

            // the client browser initialized
            if (text.equals("...")) {
                return turnContext.sendActivity(
                        MessageFactory.text(WELCOME_MESSAGE)).thenApply(sendResult -> null);
            }
        }
        catch (Exception e)
        {
            LOGGER.info("onTurn incoming activity does not exist");
        }
      
        return super.onTurn(turnContext)
            .thenCompose(saveResult -> userState.saveChanges(turnContext));
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        String text = turnContext.getActivity().getText().toLowerCase().trim();

        LOGGER.info("onMessageActivity incoming text: " + text);

        if (isErroneousRequest(text)) {
            return null;
        }

        AzurePetStoreSessionInfo azurePetStoreSessionInfo = configureSession(turnContext, text);

        if (azurePetStoreSessionInfo != null && azurePetStoreSessionInfo.getNewText() != null) {
            // get the text without the session id and csrf token
            text = azurePetStoreSessionInfo.getNewText();
        }

        // the client browser initialized
        if (text.equals("...")) {
            return turnContext.sendActivity(
                    MessageFactory.text(WELCOME_MESSAGE)).thenApply(sendResult -> null);
        }

        CompletableFuture<Void> debug = getDebug(turnContext, text, azurePetStoreSessionInfo);
        if (debug != null) {
            return debug;
        }

        DPResponse dpResponse = this.azureOpenAI.classification(text);

        if (dpResponse.getClassification() == null) {
            dpResponse.setClassification(Classification.SEARCH_FOR_PRODUCTS);
            dpResponse = this.azureOpenAI.search(text, dpResponse.getClassification());
        }

        switch (dpResponse.getClassification()) {
            case UPDATE_SHOPPING_CART:
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azureOpenAI.search(text, Classification.SEARCH_FOR_PRODUCTS);
                    if (dpResponse.getProducts() != null) {
                        dpResponse = this.azurePetStore.updateCart(azurePetStoreSessionInfo,
                                dpResponse.getProducts().get(0).getProductId());
                    }
                } else {
                    dpResponse.setDpResponseText("update shopping cart request without session... text: " + text);
                }
                break;
            case VIEW_SHOPPING_CART:
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azurePetStore.viewCart(azurePetStoreSessionInfo);
                } else {
                    dpResponse.setDpResponseText("view shopping cart request without session... text: " + text);
                }
                break;
            case PLACE_ORDER:
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azurePetStore.completeCart(azurePetStoreSessionInfo);
                } else {
                    dpResponse.setDpResponseText("place order request without session... text: " + text);
                }
                break;
            case SEARCH_FOR_DOG_FOOD:
            case SEARCH_FOR_DOG_TOYS:
            case SEARCH_FOR_CAT_FOOD:
            case SEARCH_FOR_CAT_TOYS:
            case SEARCH_FOR_FISH_FOOD:
            case SEARCH_FOR_FISH_TOYS:
            case MORE_PRODUCT_INFORMATION:
            case SEARCH_FOR_PRODUCTS:
                if (azurePetStoreSessionInfo == null) {
                    dpResponse.setDpResponseText("search for products request without session... text: " + text);
                } else {
                    dpResponse = this.azureOpenAI.search(text, dpResponse.getClassification());
                }
                break;
            case SOMETHING_ELSE:
                if (azurePetStoreSessionInfo == null) {
                    dpResponse.setDpResponseText("chatgpt request without session... text: " + text);
                } else {
                    if (!text.isEmpty()) {
                        dpResponse = this.azureOpenAI.completion(text, dpResponse.getClassification());
                    } else {
                        dpResponse.setDpResponseText("chatgpt called without a search query... text: " + text);
                    }
                }
                break;
        }

        if ((dpResponse.getDpResponseText() == null)) {
            String responseText = "I am not sure how to handle that.";

            if ((azurePetStoreSessionInfo == null)) {
                responseText += " It may be because I did not have your session information.";
            }
            dpResponse.setDpResponseText(responseText);
        }

        if (azurePetStoreSessionInfo != null) {
            azurePetStoreSessionInfo
                    .addPrompt(new Prompt(dpResponse.getClassification(), text, dpResponse.getDpResponseText()));

            LOGGER.info("onMessageActivity() caching session " + azurePetStoreSessionInfo.getId() + " for text: " + text);

            this.cache.put(azurePetStoreSessionInfo.getId(), azurePetStoreSessionInfo);
        
            this.cosmosDB.storePrompt(this.cache.getIfPresent(azurePetStoreSessionInfo.getId()));
        }

        if (dpResponse.isContentCard()) {
            return PetStoreAssistantUtilities.getProductCarouselContentCard(turnContext, dpResponse);
        }

        LOGGER.info("classification on text: " + text + " is: " + dpResponse.getClassification());

        return turnContext.sendActivity(
                MessageFactory.text(dpResponse.getDpResponseText())).thenApply(sendResult -> null);
    }

    // this method only gets invoked once, regardless of browser/user, state isnt
    // working right for some reason (DP related, not in issue with emulator)
    @Override
    protected CompletableFuture<Void> onMembersAdded(
            List<ChannelAccount> membersAdded,
            TurnContext turnContext) {

        // return membersAdded.stream()
        // .filter(
        // member -> !StringUtils
        // .equals(member.getId(), turnContext.getActivity().getRecipient().getId()))
        // .map(channel -> turnContext
        // .sendActivity(
        // MessageFactory.text(this.WELCOME_MESSAGE + id)))
        // .collect(CompletableFutures.toFutureList()).thenApply(resourceResponses ->
        // null);

        try
        {
            String text = turnContext.getActivity().getText().toLowerCase().trim();
            LOGGER.info("onMembersAdded incoming text: " + turnContext.getActivity().getText());
        
            if (isErroneousRequest(text)) {
                return null;
            }

            AzurePetStoreSessionInfo azurePetStoreSessionInfo = configureSession(turnContext, text);

            if (azurePetStoreSessionInfo != null && azurePetStoreSessionInfo.getNewText() != null) {
                // get the text without the session id and csrf token
                text = azurePetStoreSessionInfo.getNewText();
            }

            // the client browser initialized
            if (text.equals("...")) {
                return turnContext.sendActivity(
                        MessageFactory.text(WELCOME_MESSAGE)).thenApply(sendResult -> null);
            }
        }
        catch (Exception e)
        {
            LOGGER.info("onMembersAdded incoming activity does not exist");
        }

        return turnContext.sendActivity(
                MessageFactory.text("")).thenApply(sendResult -> null);
    }

    private boolean isErroneousRequest(String text) {
        // some times for unknown reasons, activity occurs with text: page_metadata,
        // let's ignore these
        if (text.contains("page_metadata") || text.isEmpty()) {
            return true;
        }
        return false;
    }

    private AzurePetStoreSessionInfo configureSession(TurnContext turnContext, String text) {
        // bot turn state and recipient not working with SoulMachines/DP (works in
        // emulator) however this id appears to be unique per browser tab when running
        // with SoulMachines/DP
        // format is XKQtkRt4hDBdwzwP2bwhs-us|0000014, so we will hack off the dynamic
        // ending piece, If you want to test locally this id will be different every
        // single time so you will want to change to
        // turnContext.getActivity().getrecipient().getId() when running locally
        String id = turnContext.getActivity().getId().trim();
        if (id.contains("-")) {
            id = id.substring(0, id.indexOf("-"));
        }

        AzurePetStoreSessionInfo azurePetStoreSessionInfo = this.cache.getIfPresent(id);

        List<Prompt> existingPrompts = null;
        if (azurePetStoreSessionInfo != null && azurePetStoreSessionInfo.getPrompts() != null) {
            existingPrompts = azurePetStoreSessionInfo.getPrompts();
        }

        // strip out session id and csrf token if one was passed in
        AzurePetStoreSessionInfo incomingAzurePetStoreSessionInfo = PetStoreAssistantUtilities
                .getAzurePetStoreSessionInfo(text);
        if (incomingAzurePetStoreSessionInfo != null) {
            text = incomingAzurePetStoreSessionInfo.getNewText();
            // turnContext.getActivity().getId() is unique per browser over the broken
            // recipient for some reason
            LOGGER.info("configureSession() incoming text contains new session info, caching session " + id + " for text: " + text);
            this.cache.put(id, incomingAzurePetStoreSessionInfo);
            azurePetStoreSessionInfo = incomingAzurePetStoreSessionInfo;
            azurePetStoreSessionInfo.setId(id);
        } else if (azurePetStoreSessionInfo != null) {
            LOGGER.info("configureSession() incoming text does not contain new session info, using existing session " + azurePetStoreSessionInfo.getId() + " for text: " + text);
            azurePetStoreSessionInfo.setNewText(text);
        }

        if (azurePetStoreSessionInfo != null && existingPrompts != null) {
            azurePetStoreSessionInfo.setPrompts(existingPrompts);
        }

        return azurePetStoreSessionInfo;
    }

    private CompletableFuture<Void> getDebug(TurnContext turnContext, String text,
            AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
        if (text.equals("debug")) {
            if (azurePetStoreSessionInfo != null && azurePetStoreSessionInfo.getNewText() != null) {
                return turnContext.sendActivity(
                        MessageFactory.text(
                                "id:" + azurePetStoreSessionInfo.getId() + ", cache size: " + cache.estimatedSize()))
                        .thenApply(sendResult -> null);
            } else {
                return turnContext.sendActivity(
                        MessageFactory.text("azurePetStoreSessionInfo was null, cache size: " + cache.estimatedSize()))
                        .thenApply(sendResult -> null);
            }
        }
        if (text.equals("button card")) {
            if (azurePetStoreSessionInfo != null && azurePetStoreSessionInfo.getNewText() != null) {
                text = azurePetStoreSessionInfo.getNewText();
            }
            String jsonString = "{\"type\":\"buttonWithImage\",\"id\":\"buttonWithImage\",\"data\":{\"title\":\"Soul Machines\",\"imageUrl\":\"https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png\",\"description\":\"Soul Machines is the leader in astonishing AGI\",\"imageAltText\":\"some text\",\"buttonText\":\"push me\"}}";

            Attachment attachment = new Attachment();
            attachment.setContentType("application/json");

            attachment.setContent(new Gson().fromJson(jsonString, JsonObject.class));
            attachment.setName("public-buttonWithImage");

            return turnContext.sendActivity(
                    MessageFactory.attachment(attachment,
                            "I have something nice to show @showcards(buttonWithImage) you."))
                    .thenApply(sendResult -> null);
        }

        if (text.equals("image card")) {
            String jsonString = "{\"type\":\"image\",\"id\":\"image-ball\",\"data\":{\"url\": \"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/ball.jpg?raw=true\",\"alt\": \"This is a pretty ball\",\"caption\": \"ball blah blah blah\"}}";
            Attachment attachment = new Attachment();
            attachment.setContentType("application/json");

            attachment.setContent(new Gson().fromJson(jsonString, JsonObject.class));
            attachment.setName("public-image-ball");

            return turnContext.sendActivity(
                    MessageFactory.attachment(attachment, "I have something nice to show @showcards(image-ball) you."))
                    .thenApply(sendResult -> null);
        }

        if(text.equals("button carousel"))
        {
            String jsonString = "{\"type\":\"buttonCarousel\",\"id\":\"buttonCarousel\",\"data\":{\"buttonCards\":[{\"title\":\"Soul Machines\",\"imageUrl\":\"https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png\",\"description\":\"1 Soul Machines is the leader in astonishing AGI\",\"imageAltText\":\"some text\",\"buttonText\":\"push me\",\"productId\":\"1\"},{\"title\":\"Soul Machines\",\"imageUrl\":\"https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png\",\"description\":\"2 Soul Machines is the leader in astonishing AGI\",\"imageAltText\":\"some text\",\"buttonText\":\"push me\",\"productId\":\"2\"},{\"title\":\"Soul Machines\",\"imageUrl\":\"https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png\",\"description\":\"3 Soul Machines is the leader in astonishing AGI\",\"imageAltText\":\"some text\",\"buttonText\":\"push me\",\"productId\":\"3\"},]}}";
            Attachment attachment = new Attachment();
            attachment.setContentType("application/json");

            attachment.setContent(new Gson().fromJson(jsonString, JsonObject.class));
            attachment.setName("public-buttonCarousel");

            return turnContext.sendActivity(
                    MessageFactory.attachment(attachment, "I have something nice to show @showcards(buttonCarousel) you."))
                    .thenApply(sendResult -> null);
        }


        return null;
    }
}
