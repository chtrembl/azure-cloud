package com.chtrembl.petstoreassistant.model;

import java.io.Serializable;

public class AzurePetStoreSessionInfo implements Serializable{
    private String sessionID = null;
    private String csrfToken = null;
    private String newText = null;
    private String id = null;

    public AzurePetStoreSessionInfo(String sessionID, String csrfToken, String newText) {
        super();
        this.sessionID = sessionID.trim().toUpperCase(); //JSESSION needs to be UPPER CASE
        this.csrfToken = csrfToken.trim(); //CSRF needs to be exact case
        this.newText = newText.trim();
    }
    public String getSessionID() {
        return sessionID;
    }
    public String getCsrfToken() {
        return csrfToken;
    }
    public String getNewText() {
        return newText;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
