package com.chtrembl.petstoreassistant.utility;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Attachment;

public class PetStoreAssistantUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreAssistantUtilities.class);

    public static String cleanDataFromAOAIResponseContent(String content) {
       //remove quotes, slashes and all chars after the last period
       return content.replaceAll("[\"]", "").replaceAll("\\\\", "").replaceAll("\\.[^.]*$", "");
    }

    public static AzurePetStoreSessionInfo getAzurePetStoreSessionInfo(String text) {
        AzurePetStoreSessionInfo azurePetStoreSessionInfo = null;
        //text comes throgh from DP as "text https://azurepetstore.com/soulmachines?sid=[sessionId]&csrf=[csrfToken]&arr=[arrAffinity]"
        Pattern pattern = Pattern.compile("sid=(.*)&csrf=(.*)&arr=(.*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String sessionID = matcher.group(1);
            String csrfToken = matcher.group(2);
            String arrAffinity = matcher.group(3);
            String newText = text.substring(0, text.indexOf("http")).trim();
            azurePetStoreSessionInfo = new AzurePetStoreSessionInfo(sessionID, csrfToken, arrAffinity, newText);
            LOGGER.info("Found session id:" + sessionID + ", csrf token:" + csrfToken + " and arr affinity:" + arrAffinity + " in text: " + text + " new text: " + newText);
        } else {
            LOGGER.info("No new session id, csrf token or arr affinity found in text: " + text);
        }
        
        return azurePetStoreSessionInfo;
    }

    public static CompletableFuture<Void> getImageCard(TurnContext turnContext, DPResponse dpResponse) {
        String jsonString = "{\"type\":\"image\",\"id\":\"image-product\",\"data\":{\"url\": \""+dpResponse.getProducts().get(0).getPhotoURL()+"\",\"alt\": \""+dpResponse.getProducts().get(0).getDescription()+"\",\"caption\": \""+dpResponse.getProducts().get(0).getDescription()+"\"}}";
        Attachment attachment = new Attachment();
        attachment.setContentType("application/json");

        attachment.setContent(new Gson().fromJson(jsonString, JsonObject.class));
        attachment.setName("public-image-product");

        return turnContext.sendActivity(
                MessageFactory.attachment(attachment, dpResponse.getDpResponseText() + " @showcards(image-product)"))
                .thenApply(sendResult -> null);
    }

    public static CompletableFuture<Void> getProductCarouselContentCard(TurnContext turnContext, DPResponse dpResponse) {
        String jsonString = "{\"type\":\"buttonCarousel\",\"id\":\"buttonCarousel\",\"data\":{\"buttonCards\":%s}}";
        jsonString = String.format(jsonString, new Gson().toJson(dpResponse.getProducts()));

        LOGGER.info("getProductCarouselContentCard() jsonString: " + jsonString);

        Attachment attachment = new Attachment();
        attachment.setContentType("application/json");

        attachment.setContent(new Gson().fromJson(jsonString, JsonObject.class));
        attachment.setName("public-buttonCarousel");

        return turnContext.sendActivity(
                MessageFactory.attachment(attachment, dpResponse.getDpResponseText() + " @showcards(buttonCarousel)"))
                .thenApply(sendResult -> null);
    }
}
