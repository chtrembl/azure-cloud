package com.chtrembl.petstoreassistant.service;

import java.util.HashMap;

import com.chtrembl.petstoreassistant.model.Product;

public interface ICosmosDB {
    public HashMap<String, Product>  getProducts();
    public HashMap<String, Product>  getCachedProducts();
}
