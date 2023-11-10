package com.chtrembl.petstoreassistant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class AzurePetStore implements IAzurePetStore {
        private static final Logger LOGGER = LoggerFactory.getLogger(AzurePetStore.class);

        @Autowired
        private ICosmosDB cosmosDB;

        // investigate why POST with FORM URL ENCODING wasnt working with Azure Pet Store, the Content-Type is getting dropped in all client libraries
        // GET is the hack for POC purposes
        private OkHttpClient client = new OkHttpClient().newBuilder().build();

        private String UPDATE_CART_URL = "https://azurepetstore.com/api/updatecart";
        private String VIEW_CART_URL = "https://azurepetstore.com/api/viewcart";
        private String COMPLETE_CART_URL = "https://azurepetstore.com/api/completecart";

        @Override
        public DPResponse updateCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo, String productId) {
                DPResponse dpResponse = new DPResponse();

                try {
                        Request request = new Request.Builder()
                                        .url(this.UPDATE_CART_URL + "?csrf=" + azurePetStoreSessionInfo.getCsrfToken()
                                                        + "&productId=" + productId)
                                        .method("GET", null)
                                        .addHeader("Cookie", "JSESSIONID=" + azurePetStoreSessionInfo.getSessionID())
                                        .addHeader("Content-Type", "text/html")
                                        .build();

                        this.client.newCall(request).execute();

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
                                        + " to your cart. " + azurePetStoreSessionInfo.getSessionID() + "|"
                                        + azurePetStoreSessionInfo.getCsrfToken());
                }

                return dpResponse;
        }

        @Override
        public DPResponse viewCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
                DPResponse dpResponse = new DPResponse();

                try {
                        Request request = new Request.Builder()
                                        .url(this.VIEW_CART_URL)
                                        .method("GET", null)
                                        .addHeader("Cookie", "JSESSIONID=" + azurePetStoreSessionInfo.getSessionID())
                                        .addHeader("Content-Type", "text/html")
                                        .build();

                        Response response = this.client.newCall(request).execute();

                        LOGGER.info("Retrieved cart items for session id: "
                                        + azurePetStoreSessionInfo.getSessionID() + " csrf: "
                                        + azurePetStoreSessionInfo.getCsrfToken());

                        dpResponse.setDpResponseText(response.body().string());

                        dpResponse.setUpdateCart(true);
                } catch (Exception e) {
                        LOGGER.error("Error retrieving cart items for session id: "
                                        + azurePetStoreSessionInfo.getSessionID() + " " + e.getMessage());
                        dpResponse.setDpResponseText("I'm sorry, I wasn't able to retrieve your shopping cart. "
                                        + azurePetStoreSessionInfo.getSessionID() + "|"
                                        + azurePetStoreSessionInfo.getCsrfToken());
                }

                return dpResponse;
        }

        @Override
        public DPResponse completeCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
                DPResponse dpResponse = new DPResponse();

                try {
                        Request request = new Request.Builder()
                                        .url(this.COMPLETE_CART_URL + "?csrf=" + azurePetStoreSessionInfo.getCsrfToken())
                                        .method("GET", null)
                                        .addHeader("Cookie", "JSESSIONID=" + azurePetStoreSessionInfo.getSessionID())
                                        .addHeader("Content-Type", "text/html")
                                        .build();

                        Response response = this.client.newCall(request).execute();

                        LOGGER.info("Completed cart for session id: "
                                        + azurePetStoreSessionInfo.getSessionID() + " csrf: "
                                        + azurePetStoreSessionInfo.getCsrfToken());

                        dpResponse.setDpResponseText(response.body().string());

                        dpResponse.setCompleteCart(true);
                } catch (Exception e) {
                        LOGGER.error("Error completing cart for session id: "
                                        + azurePetStoreSessionInfo.getSessionID() + " " + e.getMessage());
                        dpResponse.setDpResponseText("I'm sorry, I wasn't able to place your order. "
                                        + azurePetStoreSessionInfo.getSessionID() + "|"
                                        + azurePetStoreSessionInfo.getCsrfToken());
                }

                return dpResponse;
        }

}
