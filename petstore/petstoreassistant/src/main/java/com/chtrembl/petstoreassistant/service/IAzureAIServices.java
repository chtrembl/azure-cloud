package com.chtrembl.petstoreassistant.service;

import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.service.AzureAIServices.Classification;

public interface IAzureAIServices {
    public DPResponse classification(String text, String sessionID);
    public DPResponse completion(String text, Classification classification, String sessionID);
    public DPResponse search(String text,Classification classification);
}
