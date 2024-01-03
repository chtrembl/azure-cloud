package com.chtrembl.petstoreassistant.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;

public class PetStoreAssistantUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreAssistantUtilities.class);

    public static String cleanDataFromAOAIResponseContent(String content) {
       //remove quotes, slashes and all chars after the last period
       return content.replaceAll("[\"']", "").replaceAll("\\\\", "").replaceAll("\\.[^.]*$", "");
    }

    public static AzurePetStoreSessionInfo getAzurePetStoreSessionInfo(String text) {
        AzurePetStoreSessionInfo azurePetStoreSessionInfo = null;

        Pattern pattern = Pattern.compile("sid=(.*)&csrf=(.*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String sessionID = matcher.group(1);
            String csrfToken = matcher.group(2);
            String newText = text.substring(0, text.indexOf("http")).trim();
            azurePetStoreSessionInfo = new AzurePetStoreSessionInfo(sessionID, csrfToken, newText);
            LOGGER.info("Found session id:" + sessionID + " and csrf token:" + csrfToken + " in text: " + text + " new text: " + newText);
        } else {
            LOGGER.info("No new session id or csrf token found in text: " + text);
        }
        
        return azurePetStoreSessionInfo;
    }
}
