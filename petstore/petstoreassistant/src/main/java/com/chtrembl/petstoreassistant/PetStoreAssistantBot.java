// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.chtrembl.petstoreassistant;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.UserState;
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
    private String RATE_LIMIT_EXCEEDED_MESSAGE = "I am sorry, you have exceeded your Azure Open AI rate limit, please try again shortly.";  
    private String SESSION_MISSING_ERROR_MESSAGE = "I am sorry, there is an error with audio translation, please try interacting via text or restarting your browser.";   
    private String ERROR_MESSAGE = "I am sorry, I am having trouble understanding you, please try interacting via text or restarting your browser.";

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
                // get the text without the session id, csrf token and arr affinity
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
            // get the text without the session id, csrf token and arr affinity
            text = azurePetStoreSessionInfo.getNewText();
        }

        // the client browser initialized
        if (text.equals("...")) {
            LOGGER.info("onMessageActivity new session established, " + azurePetStoreSessionInfo != null ? "session id: " + azurePetStoreSessionInfo.getId() + " id: " + azurePetStoreSessionInfo.getId() : "session id: null");
            return turnContext.sendActivity(
                    MessageFactory.text(WELCOME_MESSAGE)).thenApply(sendResult -> null);
        }

        CompletableFuture<Void> debug = getDebug(turnContext, text, azurePetStoreSessionInfo);
        if (debug != null) {
            return debug;
        }
        
        if(azurePetStoreSessionInfo == null)
        {
            return turnContext.sendActivity(
                MessageFactory.text(this.SESSION_MISSING_ERROR_MESSAGE))
                .thenApply(sendResult -> null);
        }

        DPResponse dpResponse = this.azureOpenAI.classification(text, azurePetStoreSessionInfo);

        if(dpResponse.isRateLimitExceeded())
        {
            return turnContext.sendActivity(
                MessageFactory.text(this.RATE_LIMIT_EXCEEDED_MESSAGE))
                .thenApply(sendResult -> null);
        }
        
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
                }
                break;
            case VIEW_SHOPPING_CART:
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azurePetStore.viewCart(azurePetStoreSessionInfo);
                }
                break;
            case PLACE_ORDER:
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azurePetStore.completeCart(azurePetStoreSessionInfo);
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
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azureOpenAI.search(text, dpResponse.getClassification());
                }
                break;
            case SOMETHING_ELSE:
                if (azurePetStoreSessionInfo != null) {
                    if (!text.isEmpty()) {
                        dpResponse = this.azureOpenAI.completion(text, dpResponse.getClassification(), azurePetStoreSessionInfo);
                    } else {
                        dpResponse.setDpResponseText("chatgpt called without a search query... text: " + text);
                    }
                }
                break;
        }

        if(dpResponse.isRateLimitExceeded())
        {
            return turnContext.sendActivity(
                MessageFactory.text(this.RATE_LIMIT_EXCEEDED_MESSAGE))
                .thenApply(sendResult -> null);
        }
        
        if ((dpResponse.getDpResponseText() == null)) {
            dpResponse.setDpResponseText(this.ERROR_MESSAGE);
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
                // get the text without the session id, csrf token and arr affinity
                text = azurePetStoreSessionInfo.getNewText();
            }

            // the client browser initialized
            if (text.equals("...")) {
                LOGGER.info("onMembersAdded new session established, " + azurePetStoreSessionInfo != null ? "session id: " + azurePetStoreSessionInfo.getId() + " id: " + azurePetStoreSessionInfo.getId() : "session id: null");
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

        // strip out session id, csrf token and arr affinity if one was passed in
        AzurePetStoreSessionInfo incomingAzurePetStoreSessionInfo = PetStoreAssistantUtilities
                .getAzurePetStoreSessionInfo(text);
        if (incomingAzurePetStoreSessionInfo != null) {
            text = incomingAzurePetStoreSessionInfo.getNewText();
            // turnContext.getActivity().getId() is unique per browser over the broken
            // recipient for some reason
            LOGGER.info("configureSession() incoming text contains new session info, caching session " + id + " for text: " + text);
            incomingAzurePetStoreSessionInfo.setId(id);
            this.cache.put(id, incomingAzurePetStoreSessionInfo);
            azurePetStoreSessionInfo = incomingAzurePetStoreSessionInfo;
        } else if (azurePetStoreSessionInfo != null) {
            LOGGER.info("configureSession() incoming text does not contain new session info, using existing session " + azurePetStoreSessionInfo.getId() + " for text: " + text);
            azurePetStoreSessionInfo.setNewText(text);
        }

        if (azurePetStoreSessionInfo != null && existingPrompts != null) {
            azurePetStoreSessionInfo.setPrompts(existingPrompts);
        }

        MDC.put("newText", azurePetStoreSessionInfo!= null ? azurePetStoreSessionInfo.getNewText(): null);
        MDC.put("unformattedID", turnContext.getActivity().getId().trim());
        MDC.put("id", azurePetStoreSessionInfo!= null ? azurePetStoreSessionInfo.getId() : null);
        MDC.put("sessionID", azurePetStoreSessionInfo!= null ? azurePetStoreSessionInfo.getSessionID() : null);
        MDC.put("csrfToken", azurePetStoreSessionInfo!= null ? azurePetStoreSessionInfo.getCsrfToken() : null);
        MDC.put("arrAffinity", azurePetStoreSessionInfo!= null ? azurePetStoreSessionInfo.getArrAffinity() : null);

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

        return null;
    }
}
