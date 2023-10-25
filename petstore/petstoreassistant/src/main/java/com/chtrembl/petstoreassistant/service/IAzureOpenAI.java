package com.chtrembl.petstoreassistant.service;

import com.chtrembl.petstoreassistant.model.DPResponse;
import com.chtrembl.petstoreassistant.service.AzureOpenAI.Classification;

public interface IAzureOpenAI {
    public DPResponse classification(String text);
    public DPResponse completion(String text, Classification classification);
}
