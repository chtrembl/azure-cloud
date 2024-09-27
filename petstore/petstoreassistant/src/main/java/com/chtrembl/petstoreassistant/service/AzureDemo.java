package com.chtrembl.petstoreassistant.service;

import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.utility.PetStoreAssistantUtilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Service
public class AzureDemo implements IAzureDemo {
        private static final Logger LOGGER = LoggerFactory.getLogger(AzureDemo.class);

        @Value("classpath:json/azurerequests/azureResourcesDemoBody.txt")
        private Resource azureResourcesDemoBodyResource;
        String azureResourcesDemoBodyBodyString;

        @Value("${subscriptionId}")
        private String subscriptionId;
    
        private WebClient azureClient = null;
        private WebClient adoClient = null;

        @PostConstruct
        public void initialize() throws Exception {
                this.azureResourcesDemoBodyBodyString = StreamUtils
                .copyToString(azureResourcesDemoBodyResource.getInputStream(), Charset.defaultCharset());

                this.azureClient = WebClient.create("https://management.azure.com/providers/Microsoft.ResourceGraph/resources?api-version=2021-03-01");
                this.adoClient = WebClient.create("https://dev.azure.com/chtrembl/PetStore/_apis/pipelines/49/runs?api-version=6.0-preview.1&Content-Type=application/json");
        }

        @Override       
        public DPResponse getAzureResources(String at1, AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
                LOGGER.info("getAzureResources invoked, text: {}", azurePetStoreSessionInfo.getNewText());

                DPResponse dpResponse = new DPResponse();

                try {
                        int days = 10;
                        if (azurePetStoreSessionInfo.getNewText().matches(".*\\d.*")) {
                                days = Integer.parseInt(azurePetStoreSessionInfo.getNewText().replaceAll("[^0-9]", ""));
                        }

                        String azureResponse = this.azureClient.post()
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + at1)
                                .bodyValue((String.format(this.azureResourcesDemoBodyBodyString, subscriptionId, days)))
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
        
                        JsonObject jsonObject = JsonParser.parseString(azureResponse).getAsJsonObject();

                        // Get the data array
                        JsonArray dataArray = jsonObject.getAsJsonArray("data");

                        // StringBuilder to append resourceType values
                        StringBuilder resourceTypes = new StringBuilder();

                        // Iterate over the data array
                        for (int i = 0; i < dataArray.size(); i++) {
                                JsonObject dataObject = dataArray.get(i).getAsJsonObject();
                                String resourceType = dataObject.get("resourceType").getAsString();
                                String changeType = dataObject.get("changeType").getAsString();
                                resourceTypes.append(changeType+" "+resourceType).append("\n");
                        }

                        String content = "We Found: " + dataArray.size()+" over the last " + days+" days\n" +resourceTypes.toString();

                        dpResponse.setDpResponseText(PetStoreAssistantUtilities.cleanDataFromAOAIResponseContent(content));

                        dpResponse.setAoaiResponse(content);
                        LOGGER.info("getAzureResources response for text {} was {}", azurePetStoreSessionInfo.getNewText(), content);
                }
                catch (WebClientException webClientException) {
                        LOGGER.error("Error parsing getAzureResources response ", webClientException);
                        if(webClientException.getMessage().contains("429"))
                        {
                                dpResponse.setRateLimitExceeded(true);
                        }
                        
                        dpResponse.setDpResponseText("I'm sorry, I wasn't able to get the Azure resources, check at.");
                }
                catch (Exception e) {
                        LOGGER.error("Error parsing getAzureResources response azure " + azurePetStoreSessionInfo != null ? "session id: " + azurePetStoreSessionInfo.getId() + " id: " + azurePetStoreSessionInfo.getId() : "session id: null", e);
                }
                return dpResponse;
        }

        @Override
        public DPResponse executeDevopsPipeline(AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
                DPResponse dpResponse = new DPResponse();
                dpResponse.setDpResponseText("I'm sorry, I wasn't able to execute the DevOps pipeline.");
                return dpResponse;
        }
}
