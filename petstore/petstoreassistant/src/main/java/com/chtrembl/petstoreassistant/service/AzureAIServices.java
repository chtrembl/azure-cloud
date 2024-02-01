package com.chtrembl.petstoreassistant.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.model.Product;
import com.chtrembl.petstoreassistant.utility.PetStoreAssistantUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class AzureAIServices implements IAzureAIServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(AzureAIServices.class);

    @Value("classpath:json/azurerequests/classificationRequestBody.txt")
    private Resource classificationRequestBodyResource;
    String classificationRequestBodyString;
    String CLASSIFICATION_URI = "/openai/deployments/azurepetstore-shopping-assistant/chat/completions?api-version=2023-12-01-preview";

    @Value("classpath:json/azurerequests/azurepetstoredataCompletionRequestBody.txt")
    private Resource azurepetstoredataCompletionRequestBodyResource;
    String azurepetstoredataCompletionRequestBodyString;
    String COMPLETION_URI = "/openai/deployments/azurepetstore-shopping-assistant/extensions/chat/completions?api-version=2023-12-01-preview";

    @Value("classpath:json/azurerequests/chatgpt4completionRequestBody.txt")
    private Resource chatgpt4CompletionRequestBodyResource;
    String chatgpt4CompletionRequestBodyString;
    String CHATGPT4_COMPLETION_URI = "/openai/deployments/azurepetstore-shopping-assistant/chat/completions?api-version=2023-12-01-preview";

    @Value("classpath:json/azurerequests/semanticSearchRequestBody.txt")
    private Resource semanticSearchRequestBodyBodyResource;
    String semanticSearchRequestBodyBodyString;

    private WebClient aoaiClient = WebClient.create("https://azurepetstore-aoai-gpt4.openai.azure.com");
    private WebClient csClient = WebClient.create(
            "https://azurepetstore-cs.search.windows.net/indexes/petproducts/docs/search?api-version=2023-10-01-Preview");

    @Value("${aoai.key}")
    private String aoaiKey;

    @Value("${cognitive.search.key}")
    private String csKey;

    @Autowired
    private ICosmosDB cosmosDB;

    @PostConstruct
    public void initialize() throws Exception {
        this.classificationRequestBodyString = StreamUtils
                .copyToString(classificationRequestBodyResource.getInputStream(), Charset.defaultCharset());

        this.azurepetstoredataCompletionRequestBodyString = StreamUtils.copyToString(
                azurepetstoredataCompletionRequestBodyResource.getInputStream(), Charset.defaultCharset())
                .replaceAll("CS_KEY", this.csKey);

        this.chatgpt4CompletionRequestBodyString = StreamUtils
                .copyToString(chatgpt4CompletionRequestBodyResource.getInputStream(), Charset.defaultCharset());

        this.semanticSearchRequestBodyBodyString = StreamUtils
                .copyToString(semanticSearchRequestBodyBodyResource.getInputStream(), Charset.defaultCharset());
    }

    public enum Classification {
        UPDATE_SHOPPING_CART("update shopping cart"),
        VIEW_SHOPPING_CART("view shopping cart"),
        PLACE_ORDER("place order"),
        SEARCH_FOR_DOG_TOYS("search for dog toys"),
        SEARCH_FOR_DOG_FOOD("search for dog food"),
        SEARCH_FOR_CAT_TOYS("search for cat toys"),
        SEARCH_FOR_CAT_FOOD("search for cat food"),
        SEARCH_FOR_FISH_TOYS("search for fish toys"),
        SEARCH_FOR_FISH_FOOD("search for fish food"),
        SEARCH_FOR_PRODUCTS("search for products"),
        MORE_PRODUCT_INFORMATION("more product information"),
        SOMETHING_ELSE("something else");

        public final String label;

        private Classification(String label) {
            this.label = label;
        }

        public static Classification valueOfLabel(String label) {
            for (Classification e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }
    }

    @Override
    public DPResponse classification(String text) {
        LOGGER.info("classification invoked, text: {}", text);

        DPResponse dpResponse = new DPResponse();

        try {
            String aoaiResponse = this.httpRequest(String.format(this.classificationRequestBodyString, text),
                    this.CLASSIFICATION_URI, this.aoaiKey, this.aoaiClient);

            String classification = new Gson().fromJson(aoaiResponse, JsonElement.class).getAsJsonObject()
                    .get("choices")
                    .getAsJsonArray().get(0).getAsJsonObject().get("message").getAsJsonObject().get("content")
                    .toString().toLowerCase();

            classification = PetStoreAssistantUtilities.cleanDataFromAOAIResponseContent(classification);

            dpResponse.setClassification(Classification.valueOfLabel(classification));

            LOGGER.info("classified {} as {}", text, classification);

        } catch (Exception e) {
            LOGGER.error("Error parsing classification response ", e);
        }
        return dpResponse;
    }

    @Override
    public DPResponse completion(String text, Classification classification) {
        LOGGER.info("completion invoked, text: {}", text);

        DPResponse dpResponse = new DPResponse();
        dpResponse.setClassification(classification);

        try {
            String aoaiRequestBody = this.chatgpt4CompletionRequestBodyString;
            String uri = this.CHATGPT4_COMPLETION_URI;

            String aoaiResponse = this.httpRequest(
                    String.format(aoaiRequestBody,
                            text),
                    uri, this.aoaiKey, this.aoaiClient);

            String content = null;

            JsonObject message = new Gson().fromJson(aoaiResponse, JsonElement.class).getAsJsonObject()
                    .get("choices")
                    .getAsJsonArray().get(0).getAsJsonObject().get("message").getAsJsonObject();

            content = message.get("content").toString().toLowerCase();

            dpResponse.setDpResponseText(PetStoreAssistantUtilities.cleanDataFromAOAIResponseContent(content));

            dpResponse.setAoaiResponse(content);
            LOGGER.info("completion response for text {} was {}", text, content);
        } catch (Exception e) {
            LOGGER.error("Error parsing completion response ", e);
        }
        return dpResponse;
    }

    @Override
    public DPResponse search(String text, Classification classification) {
        LOGGER.info("search invoked, text: {}", text);

        DPResponse dpResponse = new DPResponse();
        dpResponse.setClassification(classification);

        String dpResponseText = "We have a ";

        String filter = "";

        if(!classification.equals(Classification.SEARCH_FOR_PRODUCTS))
        {
            String category = "";
            switch (dpResponse.getClassification()) {
                case SEARCH_FOR_DOG_FOOD:
                    category = "Dog Food";
                    dpResponse.setContentCard(true);
                    break;
                case SEARCH_FOR_DOG_TOYS:
                    category = "Dog Toy";
                    dpResponse.setContentCard(true);
                    break;
                case SEARCH_FOR_CAT_FOOD:
                    category = "Cat Food";
                    dpResponse.setContentCard(true);
                    break;
                case SEARCH_FOR_CAT_TOYS:
                    category = "Cat Toy";
                    dpResponse.setContentCard(true);
                    break;
                case SEARCH_FOR_FISH_FOOD:
                    category = "Fish Food";
                    dpResponse.setContentCard(true);
                    break;
                case SEARCH_FOR_FISH_TOYS:
                    category = "Fish Toy";
                    dpResponse.setContentCard(true);
                    break;
            }

            if(!classification.equals(Classification.MORE_PRODUCT_INFORMATION))
            {
                filter =  "\"filter\": \"category/name eq '"+category+"'\",";
            }
        }

        String body = String.format(this.semanticSearchRequestBodyBodyString,
                        text, filter);

        LOGGER.info("search body: {}", body);

        String searchResponse = this.httpRequest(body,
                null, this.csKey, this.csClient);

        LOGGER.info("search response: {}", searchResponse);

        JsonArray productsJsonArray = new Gson().fromJson(searchResponse, JsonElement.class).getAsJsonObject()
                .get("value")
                .getAsJsonArray();

        List<Product> products = new ArrayList<Product>();
        int searchScoreThreshold = 0;
        for (JsonElement productJsonElement : productsJsonArray) {
            JsonObject productJsonObject = productJsonElement.getAsJsonObject();
            Product product = new Product();
            //grab the top ranking integers/search score
            String searchScoreString = productJsonObject.get("@search.score").getAsString();
            int searchScore = Integer.parseInt(searchScoreString.substring(0, searchScoreString.indexOf(".")));
            //if (searchScore >= searchScoreThreshold) {
            searchScoreThreshold = searchScore;
            product.setSearchScore(searchScore);
            product.setProductId(productJsonObject.get("productId").getAsString());
            product.setName(productJsonObject.get("name").getAsString());
            product.setDescription(productJsonObject.get("description").getAsString());
            product.setPhotoURL(productJsonObject.get("photoURL").getAsString());
            products.add(product);
            //}
        }

        LOGGER.info("Found " + products.size() + " products from semantic search on " + text);
                   
        if (products.size() > 0) {
            int i = 0;
            for (Product product : products) {
                if (i == 0) {
                    dpResponseText += " " + product.getName();
                    i++;
                } else if (i++ != products.size() - 1) {
                    dpResponseText += ", " + product.getName();
                } else {
                    dpResponseText += " and " + product.getName();
                }
            }

            dpResponse.setDpResponseText(dpResponseText);
            dpResponse.setProducts(products);
        }
        
        // this should become a content card with a carousel of product(s) for now just display description if there is 1 product and override the stuff above
        if(products.size() == 1 && (classification.equals(Classification.MORE_PRODUCT_INFORMATION) || dpResponse.isContentCard()))
        {
             dpResponse.setContentCard(true);
             dpResponse.setDpResponseText("Check out this product, the " + products.get(0).getName());
        }
        else if (products.size() > 0 && dpResponse.isContentCard())
        {
            dpResponseText = "Check out these products";
            dpResponse.setDpResponseText(dpResponseText);
        }
        else
        {
            // else display the raw AOAI response from our cog search index
            dpResponseText = text;
        }

        return dpResponse;
    }

    private String httpRequest(String body, String uri, String apiKey, WebClient webClient) {
        String response = webClient.post().uri(uri)
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class).block();
        return response;
    }
}
