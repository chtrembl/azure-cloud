package com.chtrembl.petstoreassistant.service;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.service.AzureAIServices.Classification;

public interface IAzureAIServices {
    public DPResponse classification(String text, AzurePetStoreSessionInfo azurePetStoreSessionInfo);
    public DPResponse completion(String text, Classification classification, AzurePetStoreSessionInfo azurePetStoreSessionInfo);
    public DPResponse search(String text,Classification classification);
}
