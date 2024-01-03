package com.chtrembl.petstoreassistant.service;

import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.service.AzureAIServices.Classification;

public interface IAzureAIServices {
    public DPResponse classification(String text);
    public DPResponse completion(String text, Classification classification);
    public DPResponse search(String text,Classification classification);
}
