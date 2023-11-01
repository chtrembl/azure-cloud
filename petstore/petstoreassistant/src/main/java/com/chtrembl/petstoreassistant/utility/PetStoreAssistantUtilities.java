package com.chtrembl.petstoreassistant.utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.model.ProductsCache;

public class PetStoreAssistantUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreAssistantUtilities.class);

    public static DPResponse processAOAIProductsCompletion(String text, ProductsCache productsCache) {
        DPResponse dpResponse = new DPResponse();

        String dpResponseText = "We have,";

        // remove cog search references
        text = text.replaceAll("\\[(doc\\d+)\\]", "");
        
        // tokenize on the commas
        String[] substrings = text.split(",");

        ArrayList<String> productIDs = new ArrayList<String>();

        for (String substring : substrings) {
            substring = substring.trim();
            // grab numbers only
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(substring);
            if (matcher.find()) {
                String number = matcher.group();
                productIDs.add(number);
            } else {
                LOGGER.info("No product id found in substring: " + substring);
            }
        }
                   
        if (productIDs.size() > 0) {
            int i = 0;
            for (String productID : productIDs) {
                if (i == 0) {
                    dpResponseText += " " + productsCache.getProducts().get(productID).getName();
                    i++;
                } else if (i++ != productIDs.size() - 1) {
                    dpResponseText += ", " + productsCache.getProducts().get(productID).getName();
                } else {
                    dpResponseText += " and " + productsCache.getProducts().get(productID).getName();
                }
            }

            dpResponse.setDpResponseText(dpResponseText);
            dpResponse.setResponseProductIDs(productIDs);
        }
        
        return dpResponse;
    }

    public static String cleanDataFromAOAIResponseContent(String content) {
       //remove quotes, slashes and all chars after the last period
       return content.replaceAll("[\"']", "").replaceAll("\\\\", "").replaceAll("\\.[^.]*$", "");
    }

    public static AzurePetStoreSessionInfo getAzurePetStoreSessionInfo(String text) {
        AzurePetStoreSessionInfo azurePetStoreSessionInfo = null;

        Pattern pattern = Pattern.compile("sid:(.*)csrf:(.*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String sessionID = matcher.group(1);
            String csrfToken = matcher.group(2);
            String newText = text.substring(0, text.indexOf("sid:")).trim();
            azurePetStoreSessionInfo = new AzurePetStoreSessionInfo(sessionID, csrfToken, newText);
            LOGGER.info("Found session id:" + sessionID + " and csrf token:" + csrfToken + " in text: " + text + " new text: " + newText);
        } else {
            LOGGER.info("No session id or csrf token found in text: " + text);
        }
        
        return azurePetStoreSessionInfo;
    }
}
