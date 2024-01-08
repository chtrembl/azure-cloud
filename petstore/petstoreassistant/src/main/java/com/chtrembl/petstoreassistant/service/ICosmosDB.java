package com.chtrembl.petstoreassistant.service;

import java.util.HashMap;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.Product;

public interface ICosmosDB {
    public HashMap<String, Product>  getProducts();
    public void storePrompt(AzurePetStoreSessionInfo azurePetStoreSessionInfo);
}
