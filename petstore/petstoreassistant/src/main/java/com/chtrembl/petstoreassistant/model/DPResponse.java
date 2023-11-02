package com.chtrembl.petstoreassistant.model;

import java.util.List;

import com.chtrembl.petstoreassistant.service.AzureOpenAI.Classification;

public class DPResponse {
    Classification classification = null;
    private String dpResponseText = "I am not sure how to handle that.";  
    List<Product> products = null;
    List<String> responseProductIDs = null;
    boolean updateCart = false;
    boolean completeCart = false;
    
    public DPResponse() {
        super();
    }
    
    public Classification getClassification() {
        return classification;
    }
    
    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public String getDpResponseText() {
        return dpResponseText;
    }
    public void setDpResponseText(String dpResponseText) {
        this.dpResponseText = dpResponseText;
    }

    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<String> getResponseProductIDs() {
        return responseProductIDs;
    }
    public void setResponseProductIDs(List<String> responseProductIDs) {
        this.responseProductIDs = responseProductIDs;
    }
    public boolean isUpdateCart() {
        return updateCart;
    }
    public void setUpdateCart(boolean updateCart) {
        this.updateCart = updateCart;
    }
    public boolean isCompleteCart() {
        return completeCart;
    }
    public void setCompleteCart(boolean completeCart) {
        this.completeCart = completeCart;
    }   
}
