package com.chtrembl.petstoreassistant.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AzurePetStoreSessionInfo implements Serializable{
    private String sessionID = null;
    private String csrfToken = null;
    private String arrAffinity = null;
    @JsonIgnore
    private String newText = null;
    private String unformattedId = null;
    private String id = null;

    private List<Prompt> prompts = null;
    
    public AzurePetStoreSessionInfo(String sessionID, String csrfToken, String arrAffinity, String newText) {
        super();
        this.sessionID = sessionID.trim().toUpperCase(); //JSESSION needs to be UPPER CASE
        this.csrfToken = csrfToken.trim(); //CSRF needs to be exact case
        this.arrAffinity = arrAffinity.trim(); //ARRAffinity for App Service Session Stickiness
        this.newText = newText.trim();
    }
    public String getSessionID() {
        return sessionID;
    }
    public String getCsrfToken() {
        return csrfToken;
    }
    public String getArrAffinity() {
        return arrAffinity;
    }
    public String getNewText() {
        return newText;
    }
    public void setNewText(String newText) {
        this.newText = newText;
    }
    public String getUnformattedId() {
        return unformattedId;
    }
    public void setUnformattedId(String unformattedId) {
        this.unformattedId = unformattedId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<Prompt> getPrompts() {
        return prompts;
    }
    public void setPrompts(List<Prompt> prompts) {
        this.prompts = prompts;
    }
    public void addPrompt(Prompt prompt) {
        if (this.prompts == null) {
            this.prompts = new java.util.ArrayList<Prompt>();
        }
        this.prompts.add(prompt);
    }

}
