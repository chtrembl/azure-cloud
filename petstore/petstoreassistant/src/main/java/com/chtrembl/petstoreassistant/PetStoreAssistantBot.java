// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.chtrembl.petstoreassistant;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.service.AzureAIServices.Classification;
import com.chtrembl.petstoreassistant.service.IAzureAIServices;
import com.chtrembl.petstoreassistant.service.IAzurePetStore;
import com.chtrembl.petstoreassistant.utility.PetStoreAssistantUtilities;
import com.codepoetics.protonpack.collectors.CompletableFutures;
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

    private String WELCOME_MESSAGE = "Hello and welcome to the Azure Pet Store, you can ask me questions about our products, your shopping cart and your order, you can also ask me for information about pet animals. How can I help you?";

    private UserState userState;

    public PetStoreAssistantBot(UserState withUserState) {
        this.userState = withUserState;
    }

    // onTurn processing isn't working with DP, not being used...
    @Override
    public CompletableFuture<Void> onTurn(TurnContext turnContext) {
        return super.onTurn(turnContext)
                .thenCompose(saveResult -> userState.saveChanges(turnContext));
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        String text = turnContext.getActivity().getText().toLowerCase();

        // strip out session id and csrf token if one was passed from soul machines
        // sendTextMessage() function
        AzurePetStoreSessionInfo azurePetStoreSessionInfo = PetStoreAssistantUtilities
                .getAzurePetStoreSessionInfo(text);
        if (azurePetStoreSessionInfo != null) {
            text = azurePetStoreSessionInfo.getNewText();
        }

         //DEBUG ONLY
        if (text.contains("session"))
        {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
                  return turnContext.sendActivity(
                MessageFactory.text("session: "+request.getSession().getId())).thenApply(sendResult -> null);
            }
        }
        if (text.contains("card")) {
            if(azurePetStoreSessionInfo != null && azurePetStoreSessionInfo.getNewText() != null)
            { 
            text = azurePetStoreSessionInfo.getNewText();
            }
            String jsonString = "{\"type\":\"buttonWithImage\",\"id\":\"buttonWithImage\",\"data\":{\"title\":\"Soul Machines\",\"imageUrl\":\"https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png\",\"description\":\"Soul Machines is the leader in astonishing AGI\",\"imageAltText\":\"some text\",\"buttonText\":\"push me\"}}";

            Attachment attachment = new Attachment();
            attachment.setContentType("application/json");

            attachment.setContent(new Gson().fromJson(jsonString, JsonObject.class));
            attachment.setName("public-content-card");

            return turnContext.sendActivity(
                    MessageFactory.attachment(attachment, "I have something nice to show @showcards(content-card) you."))
                    .thenApply(sendResult -> null);
        }
        //END DEBUG

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
                }
                else
                {
                    dpResponse.setDpResponseText("update shopping cart request without session...");
                }
                break;
            case VIEW_SHOPPING_CART:
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azurePetStore.viewCart(azurePetStoreSessionInfo);
                }
                else
                {
                    dpResponse.setDpResponseText("view shopping cart request without session...");
                }
                break;
            case PLACE_ORDER:
                if (azurePetStoreSessionInfo != null) {
                    dpResponse = this.azurePetStore.completeCart(azurePetStoreSessionInfo);
                }
                else
                {
                    dpResponse.setDpResponseText("place order request without session...");
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
                    dpResponse.setDpResponseText("search for products request without session...");
                }
                else
                {
                    dpResponse = this.azureOpenAI.search(text, dpResponse.getClassification());
                }
                break;
            case SOMETHING_ELSE:
                if (azurePetStoreSessionInfo == null) {
                    dpResponse.setDpResponseText("chatgpt request without session...");
                }
                else
                {
                    if(!text.isEmpty())
                    {
                        dpResponse = this.azureOpenAI.completion(text, dpResponse.getClassification());
                    }
                    else
                    {
                        dpResponse.setDpResponseText("chatgpt called without a search query");
                    }
                }
                break;
        }

        if((dpResponse.getDpResponseText() == null))
        {
            String responseText = "I am not sure how to handle that.";

            if((azurePetStoreSessionInfo == null))
            {
                responseText += " It may be because I did not have your session information.";
            }
            dpResponse.setDpResponseText(responseText);
        }

        return turnContext.sendActivity(
                MessageFactory.text(dpResponse.getDpResponseText())).thenApply(sendResult -> null);
       }

    @Override
    protected CompletableFuture<Void> onMembersAdded(
            List<ChannelAccount> membersAdded,
            TurnContext turnContext) {

        return membersAdded.stream()
                .filter(
                        member -> !StringUtils
                                .equals(member.getId(), turnContext.getActivity().getRecipient().getId()))
                .map(channel -> turnContext
                        .sendActivity(
                                MessageFactory.text(this.WELCOME_MESSAGE)))
                .collect(CompletableFutures.toFutureList()).thenApply(resourceResponses -> null);
    }

   

   }
