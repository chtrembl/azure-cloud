package com.chtrembl.petstoreassistant.model;

import java.io.Serializable;

public class AzurePetStoreSessionInfo implements Serializable{
    private String sessionID = null;
    private String csrfToken = null;
    private String newText = null;

    public AzurePetStoreSessionInfo(String sessionID, String csrfToken, String newText) {
        super();
        this.sessionID = sessionID.trim();
        this.csrfToken = csrfToken.trim();
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
}
