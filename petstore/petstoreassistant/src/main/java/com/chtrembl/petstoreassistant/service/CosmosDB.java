package com.chtrembl.petstoreassistant.service;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.chtrembl.petstoreassistant.model.Product;

@Service
public class CosmosDB implements ICosmosDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(CosmosDB.class);

    private static final String ENDPOINT = "https://azurepetstoreweb.documents.azure.com:443/";
   
    @Value("${cosmos.key}")
    private String cosmosKey;
   
    private static final String DATABASE_ID = "E-Commerce";
   
    private static final String CONTAINER_ID = "ProductsV2";

    private CosmosClient client = null;

    private HashMap<String, Product>  products;

    @PostConstruct
    public void initialize() throws Exception {
        // generate a cosmos client to query the database
        this.client = new CosmosClientBuilder()
                .endpoint(ENDPOINT)
                .key(this.cosmosKey)
                .buildClient();
        this.products = this.getProducts();
    }

    @Override
    public HashMap<String, Product>  getProducts() {
        HashMap<String, Product>  products = new HashMap<String, Product> ();

        CosmosContainer container = client.getDatabase(DATABASE_ID).getContainer(CONTAINER_ID);

        String query = "SELECT * FROM ProductsV2";
       
        CosmosPagedIterable<Product> productPagedIterable  = container.queryItems(query, null, Product.class);
        for (Product product : productPagedIterable) {
            products.put(product.getProductId(), product);
        }

        LOGGER.info("Retrieved " + products.size() + " products from CosmosDB");
        
        return products;
    }

    public HashMap<String, Product>  getCachedProducts() {
        return this.products;
    }
}
