package com.chtrembl.petstoreassistant.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;

@Service
public class AzurePetStore implements IAzurePetStore{
    private static final Logger LOGGER = LoggerFactory.getLogger(AzurePetStore.class);

    @Autowired
    private ICosmosDB cosmosDB;

    private WebClient azurePetStoreClient;

    @PostConstruct
    public void initialize() {
        this.azurePetStoreClient = WebClient.builder().baseUrl("https://azurepetstore.com").build();
    }

    @Override
    public DPResponse updateCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo, String productId) {
        DPResponse dpResponse = new DPResponse();
        
        try
        {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("_csrf", azurePetStoreSessionInfo.getCsrfToken());
            formData.add("productId", productId);
           
            this.azurePetStoreClient.post().uri("updatecart").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .header("Cookie", "JSESSIONID=" + azurePetStoreSessionInfo.getSessionID())
                .header("Referer", "https://azurepetstore.com/updatecart")
                .header("Accept", "*/*")
                .exchange().block().bodyToMono(String.class).block();

            LOGGER.info("Updated cart with product id: " + productId + " for session id: "+ azurePetStoreSessionInfo.getSessionID() + " csrf: " + azurePetStoreSessionInfo.getCsrfToken());

            dpResponse.setDpResponseText("I just added the " + this.cosmosDB.getCachedProducts().get(productId).getName() + " to your cart.");

            dpResponse.setUpdateCart(true);
        }catch (Exception e){
             LOGGER.error("Error updating cart with product id: " + productId + " for session id: "+ azurePetStoreSessionInfo.getSessionID() + " " + e.getMessage());
        }

        return dpResponse; 
    }

    @Override
    public DPResponse completeCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'completeCart'");
    }
    
}
