package com.chtrembl.petstoreassistant.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.model.Product;

public class PetStoreAssistantUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreAssistantUtilities.class);

    public static DPResponse processAOAIProductsCompletion(String text, HashMap<String, Product> products) {
        DPResponse dpResponse = new DPResponse();

        String dpResponseText = "We have a ";

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
                if(!productIDs.contains(number))
                {
                    productIDs.add(number);
                }
            }
        }
        LOGGER.info("Found " + productIDs.size() + " productId's in text: " + text);
                   
        if (productIDs.size() > 0) {
            int i = 0;
            for (String productID : productIDs) {
                if (i == 0) {
                    dpResponseText += " " + products.get(productID).getName();
                    i++;
                } else if (i++ != productIDs.size() - 1) {
                    dpResponseText += ", " + products.get(productID).getName();
                } else {
                    dpResponseText += " and " + products.get(productID).getName();
                }
            }

            dpResponse.setDpResponseText(dpResponseText);
            dpResponse.setResponseProductIDs(productIDs);
        }
        
        // this should become a content card with a carousel of product(s) for now just display description if there is 1 product and override the stuff above
        if(productIDs.size() == 1)
        {
             dpResponseText = "Here is a little information on the " + products.get(productIDs.get(0)).getName() + " " + products.get(productIDs.get(0)).getDescription();
             dpResponse.setDpResponseText(dpResponseText);
        }
        else
        {
            // else display the raw AOAI response from our cog search index
            dpResponseText = text;
        }

        return dpResponse;
    }

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
