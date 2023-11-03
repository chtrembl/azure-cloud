package com.chtrembl.petstoreassistant.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class AzurePetStore implements IAzurePetStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(AzurePetStore.class);

    @Autowired
    private ICosmosDB cosmosDB;

    @PostConstruct
    public void initialize() {
    }

    @Override
    public DPResponse updateCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo, String productId) {
        DPResponse dpResponse = new DPResponse();

        try {
            OkHttpClient client  = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "_csrf="+azurePetStoreSessionInfo.getCsrfToken()+"&productId="+productId);
            Request request = new Request.Builder()
                    .url("https://azurepetstore.com/updatecart")
                    .method("POST", body)
                    .addHeader("Cookie", "JSESSIONID="+azurePetStoreSessionInfo.getSessionID())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();
            
            request.headers().toMultimap().forEach((k, v) -> {
                LOGGER.info("Request header: " + k + " " + v);
            });

            LOGGER.info(response.body().string());

            LOGGER.info("Updated cart with product id: " + productId + " for session id: "
                    + azurePetStoreSessionInfo.getSessionID() + " csrf: " + azurePetStoreSessionInfo.getCsrfToken());

            dpResponse.setDpResponseText("I just added the "
                    + this.cosmosDB.getCachedProducts().get(productId).getName() + " to your cart.");

            dpResponse.setUpdateCart(true);
        } catch (Exception e) {
            LOGGER.error("Error updating cart with product id: " + productId + " for session id: "
                    + azurePetStoreSessionInfo.getSessionID() + " " + e.getMessage());
        }

        return dpResponse;
    }

    @Override
    public DPResponse completeCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'completeCart'");
    }

}
