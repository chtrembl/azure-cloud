package com.chtrembl.petstoreassistant.model;

import java.util.List;

import com.chtrembl.petstoreassistant.service.AzureAIServices.Classification;

public class DPResponse {
    private Classification classification = null;
    private String dpResponseText = null;  
    private List<Product> products = null;
    private boolean updateCart = false;
    private boolean completeCart = false;
    private String aoaiResponse = null;

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
    public String getAoaiResponse() {
        return aoaiResponse;
    }
    public void setAoaiResponse(String aoaiResponse) {
        this.aoaiResponse = aoaiResponse;
    }
}
