package com.chtrembl.petstoreassistant.service;

import java.util.HashMap;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.Product;

@Service
public class CosmosDB implements ICosmosDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(CosmosDB.class);

    private static final String ENDPOINT = "https://azurepetstoreweb.documents.azure.com:443/";
   
    @Value("${cosmos.key}")
    private String cosmosKey;
   
    private static final String DATABASE_ID = "E-Commerce";
   
    private static final String PRODUCTS_CONTAINER_ID = "ProductsV2";
    private static final String PROMPTS_CONTAINER_ID = "Prompts";

    private CosmosClient client = null;

    private HashMap<String, Product>  products;

    @PostConstruct
    public void initialize() throws Exception {
        // generate a cosmos client to query the database
        this.client = new CosmosClientBuilder()
                .endpoint(ENDPOINT)
                .key(this.cosmosKey)
                .buildClient();
        this.cacheProducts();
    }

   private void  cacheProducts() {
       this.products = new HashMap<String, Product> ();

        CosmosContainer container = client.getDatabase(DATABASE_ID).getContainer(this.PRODUCTS_CONTAINER_ID);

        String query = "SELECT * FROM ProductsV2";
       
        CosmosPagedIterable<Product> productPagedIterable  = container.queryItems(query, null, Product.class);
        for (Product product : productPagedIterable) {
            products.put(product.getProductId(), product);
        }

        LOGGER.info("Cached " + products.size() + " products from CosmosDB");
    }

    public HashMap<String, Product> getProducts() {
        return this.products;
    }

    @Async
    public Future<String> storePrompt(AzurePetStoreSessionInfo azurePetStoreSessionInfo) {
        CosmosContainer container = client.getDatabase(DATABASE_ID).getContainer(this.PROMPTS_CONTAINER_ID);

        try
        {
            container.upsertItem(azurePetStoreSessionInfo);
            LOGGER.info("Upsert prompt record in CosmosDB id: " + azurePetStoreSessionInfo.getId());

        }
        catch (Exception e)
        {
            LOGGER.error("Error upserting prompt in CosmosDB " + e.getMessage());
        }

         return new AsyncResult<>("prompt stored");
    }
}
