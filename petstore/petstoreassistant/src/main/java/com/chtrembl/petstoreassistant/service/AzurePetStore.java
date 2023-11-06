package com.chtrembl.petstoreassistant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
public class AzurePetStore implements IAzurePetStore {
        private static final Logger LOGGER = LoggerFactory.getLogger(AzurePetStore.class);

        @Autowired
        private ICosmosDB cosmosDB;

        // investigate why Web Client wasnt working
        private OkHttpClient client = new OkHttpClient().newBuilder().build();

        private String UPDATE_CART_URL = "https://azurepetstore.com/api/updatecart";

        @Override
        public DPResponse updateCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo, String productId) {
                DPResponse dpResponse = new DPResponse();

                try {

                        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                        RequestBody body = RequestBody.create(mediaType,
                                        "_csrf=" + azurePetStoreSessionInfo.getCsrfToken() + "&productId=" + productId);
                        Request request = new Request.Builder()
                                        .url(this.UPDATE_CART_URL)
                                        .method("POST", body)
                                        .addHeader("Cookie", "JSESSIONID=" + azurePetStoreSessionInfo.getSessionID())
                                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                        .build();

                        client.newCall(request).execute();

                        LOGGER.info("Updated cart with product id: " + productId + " for session id: "
                                        + azurePetStoreSessionInfo.getSessionID() + " csrf: "
                                        + azurePetStoreSessionInfo.getCsrfToken());

                        dpResponse.setDpResponseText("I just added the "
                                        + this.cosmosDB.getCachedProducts().get(productId).getName()
                                        + " to your cart.");

                        dpResponse.setUpdateCart(true);
                } catch (Exception e) {
                        LOGGER.error("Error updating cart with product id: " + productId + " for session id: "
                                        + azurePetStoreSessionInfo.getSessionID() + " " + e.getMessage());
                        dpResponse.setDpResponseText("I'm sorry, I wasn't able to add the "
                                        + this.cosmosDB.getCachedProducts().get(productId).getName()
                                        + " to your cart. "+azurePetStoreSessionInfo.getSessionID()+"|"+azurePetStoreSessionInfo.getCsrfToken());
                }

                return dpResponse;
        }

        @Override
        public DPResponse completeCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'completeCart'");
        }

}
