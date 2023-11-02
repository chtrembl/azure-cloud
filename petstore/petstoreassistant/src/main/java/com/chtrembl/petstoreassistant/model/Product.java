package com.chtrembl.petstoreassistant.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product implements Serializable{
    private String productId = null;
    
    @JsonProperty("category.name")
    private String category = null;
    private String name = null;
    private String description = null;
    private String photoURL = null;

    public Product() {
        super();
    }

    public Product(String productId, String category, String name, String description, String photoURL) {
        super();
        this.productId = productId;
        this.category = category;
        this.name = name;
        this.description = description;
        this.photoURL = photoURL;
    }

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
      public String getPhotoURL() {
        return photoURL;
    }
    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
