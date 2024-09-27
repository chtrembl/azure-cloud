package com.chtrembl.petstoreassistant.service;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;

public interface IAzureDemo {
    public DPResponse getAzureResources(String at1, AzurePetStoreSessionInfo azurePetStoreSessionInfo);
    public DPResponse executeDevopsPipeline(String at2, AzurePetStoreSessionInfo azurePetStoreSessionInfo);
}
