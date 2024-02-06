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
    private boolean contentCard = false;
    private boolean rateLimitExceeded = false;
    
    public DPResponse() {
        super();
    }
    
    public Classification getClassification() {
        return this.classification;
    }
    
    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public String getDpResponseText() {
        return this.dpResponseText;
    }
    public void setDpResponseText(String dpResponseText) {
        this.dpResponseText = dpResponseText;
    }

    public List<Product> getProducts() {
        return this.products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public boolean isUpdateCart() {
        return this.updateCart;
    }
    public void setUpdateCart(boolean updateCart) {
        this.updateCart = updateCart;
    }
    public boolean isCompleteCart() {
        return this.completeCart;
    }
    public void setCompleteCart(boolean completeCart) {
        this.completeCart = completeCart;
    }   
    public String getAoaiResponse() {
        return this.aoaiResponse;
    }
    public void setAoaiResponse(String aoaiResponse) {
        this.aoaiResponse = aoaiResponse;
    }
    public boolean isContentCard() {
        return this.contentCard;
    }
    public void setContentCard(boolean contentCard) {
        this.contentCard = contentCard;
    }
    public boolean isRateLimitExceeded() {
        return this.rateLimitExceeded;
    }
    public void setRateLimitExceeded(boolean rateLimitExceeded) {
        this.rateLimitExceeded = rateLimitExceeded;
    }
}
