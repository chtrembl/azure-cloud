package com.chtrembl.petstoreassistant.service;

import java.nio.charset.Charset;

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
import com.chtrembl.petstoreassistant.utility.PetStoreAssistantUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class AzureOpenAI implements IAzureOpenAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(AzureOpenAI.class);

    @Value("classpath:json/aoairequests/classificationRequestBody.txt")
    private Resource classificationRequestBodyResource;
    String classificationRequestBodyString;
    String CLASSIFICATION_URI = "/openai/deployments/azurepetstore-shopping-assistant/chat/completions?api-version=2023-07-01-preview";

    @Value("classpath:json/aoairequests/azurepetstoredataCompletionRequestBody.txt")
    private Resource azurepetstoredataCompletionRequestBodyResource;
    String azurepetstoredataCompletionRequestBodyString;
    String COMPLETION_URI = "/openai/deployments/azurepetstore-shopping-assistant/extensions/chat/completions?api-version=2023-07-01-preview";

    @Value("classpath:json/aoairequests/chatgpt4completionRequestBody.txt")
    private Resource chatgpt4CompletionRequestBodyResource;
    String chatgpt4CompletionRequestBodyString;
    String CHATGPT4_COMPLETION_URI = "/openai/deployments/azurepetstore-shopping-assistant/chat/completions?api-version=2023-07-01-preview";

    private WebClient aoaiClient = WebClient.create("https://azurepetstore-aoai-gpt4.openai.azure.com");

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
                azurepetstoredataCompletionRequestBodyResource.getInputStream(), Charset.defaultCharset()).replaceAll("CS_KEY",this.csKey);

        this.chatgpt4CompletionRequestBodyString = StreamUtils
                .copyToString(chatgpt4CompletionRequestBodyResource.getInputStream(), Charset.defaultCharset());
    }

    public enum Classification {
        UPDATE_SHOPPING_CART("update shopping cart"),
        VIEW_SHOPPING_CART("view shopping cart"),
        PLACE_ORDER("place order"),
        SEARCH_FOR_PRODUCTS("search for products"),
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
            String aoaiResoinse = this.aoaiHTTPRequest(String.format(this.classificationRequestBodyString, text),
                    this.CLASSIFICATION_URI);

            String classification = new Gson().fromJson(aoaiResoinse, JsonElement.class).getAsJsonObject()
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
            String aoaiRequestBody = null;
            String uri = null;

            switch (classification) {
                case SEARCH_FOR_PRODUCTS:
                    aoaiRequestBody = this.azurepetstoredataCompletionRequestBodyString;
                    uri = this.COMPLETION_URI;
                    break;
                case SOMETHING_ELSE:
                    aoaiRequestBody = this.chatgpt4CompletionRequestBodyString;
                    uri = this.CHATGPT4_COMPLETION_URI;
                    break;
            }

            if (aoaiRequestBody != null) {
                String aoaiResponse = this.aoaiHTTPRequest(
                        String.format(aoaiRequestBody,
                                text),
                        uri);

                String content = null;

                switch (classification) {
                    case SEARCH_FOR_PRODUCTS:

                        JsonArray messages = new Gson().fromJson(aoaiResponse, JsonElement.class).getAsJsonObject()
                                .get("choices")
                                .getAsJsonArray().get(0).getAsJsonObject().get("messages").getAsJsonArray();

                        // get the last content message in the array of responses, this should contain
                        // the response along with potential product id's we may need to then lookup for
                        // content cards and such
                        content = PetStoreAssistantUtilities.cleanDataFromAOAIResponseContent(
                                messages.get(messages.size() - 1).getAsJsonObject().get("content").toString()
                                        .toLowerCase());
                        dpResponse = PetStoreAssistantUtilities.processAOAIProductsCompletion(content,
                                this.cosmosDB.getCachedProducts());
                        break;

                    case SOMETHING_ELSE:
                        JsonObject message = new Gson().fromJson(aoaiResponse, JsonElement.class).getAsJsonObject()
                                .get("choices")
                                .getAsJsonArray().get(0).getAsJsonObject().get("message").getAsJsonObject();

                        content = message.get("content").toString().toLowerCase();
                     
                        dpResponse.setDpResponseText(PetStoreAssistantUtilities.cleanDataFromAOAIResponseContent(content));
                        break;
                }
                dpResponse.setAoaiResponse(content);
                LOGGER.info("completion response for text {} was {}", text, content);
            }
        } catch (Exception e) {
            LOGGER.error("Error parsing completion response ", e);
        }
        return dpResponse;
    }

    private String aoaiHTTPRequest(String body, String uri) {
        String aoaiResponse = aoaiClient.post().uri(uri)
                .header("api-key", this.aoaiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class).block();
        return aoaiResponse;
    }
}
