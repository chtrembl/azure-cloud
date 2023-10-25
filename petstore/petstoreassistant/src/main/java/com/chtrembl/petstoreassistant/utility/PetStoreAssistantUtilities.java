package com.chtrembl.petstoreassistant.utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.model.ProductsCache;

public class PetStoreAssistantUtilities {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetStoreAssistantUtilities.class);

    public static String[] productIdKeyVariants = new String[] { " productid: ", " product id: ", " product id of ", " product id for "," productid ", " product id ", " productkey: ", " product key: ", " product key of ", " product key for ", " product key ", " is " };

    //TODO move this to a regex
    public static String cleanDataFromAOAIResponseContent(String content) {
        // whack new lines, quotes & slashes if they exist
        content = content.replace("\n", " "); // some ids would be concatenated if we don't do this
        content = content.replace("\\n", " "); // some ids would be concatenated if we don't do this
        content = content.replace("\\\n", " "); // some ids would be concatenated if we don't do this
        content = content.replace("\"", "");
        content = content.replace("\\", "");
        content = content.replace("\\\\", "");
        content = content.replace("'", "");
        content = content.replace("\'", "");
        content = content.replace("`", "");
        content = content.replace("\\`", "");

        // strip some parentheses and such
        content = content.replaceAll("\\(", "").replaceAll("\\)", "");
        content = content.replaceAll("\\{", "").replaceAll("\\}", "");

        // strip some **
        content = content.replaceAll("\\**", "");
        // remove the last period if it exists
        if (content.charAt(content.length() - 1) == '.') {
            content = content.substring(0, content.length() - 1);
        }

        return content.trim();
    }

    public static DPResponse processAOAIProductsCompletion(String text, ProductsCache productsCache) {
        DPResponse dpResponse = new DPResponse();

        String dpResponseText = null;

        String key = locateProductIDKey(text);

        if (key != null) {

            //now trim any whitespace so the regex can work
            key = key.trim();

            dpResponseText = "We have,";

            ArrayList<String> productIDs = new ArrayList<String>();

            /*
             * shout out to copilot for this regex to find integers after a key that can be
             * concatenated with other random stuff
             */
            //create a regex to find the first group of numbers after a defined key, note the group of numbers might have other characters concatenated that can be excluded,
            String keyRegEx = "(?<=" + key + ")\\D*(\\d+)";
            //This pattern uses a positive lookbehind (?<=key) to match the string "key" before the number. It then matches any non-digit characters \D* (zero or more) until it finds a group of digits \d+. The group of digits are captured in a group using parentheses.
            
            Matcher m = Pattern.compile(keyRegEx).matcher(text);
            int i = 0;
            while (m.find()) {
                //just to be safe ensure we only have numbers at this point
                String matchedText = m.group().replaceAll("[^0-9]+", "");
                productIDs.add(matchedText);
            }
            
            if (productIDs.size() > 0) {
                i = 0;
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
        }

        return dpResponse;
    }

    // aoai is inconsistent and non deterministic, even tho we ask for "productKey"
    // there are times it will return "product id" or "product key"
    // TODO clean this up with a reg expression or something
    public static String locateProductIDKey(String text) {
        String productIDKey = null;
        //iterate through the variants and see if we can find a match
        for (String variant : productIdKeyVariants) {
            if (text.contains(variant)) {
                productIDKey = variant;
                break;
            }
        }  
        return productIDKey;
    }
}
