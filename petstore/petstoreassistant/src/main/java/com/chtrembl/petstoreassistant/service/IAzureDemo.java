package com.chtrembl.petstoreassistant.service;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;

public interface IAzureDemo {
    public DPResponse getAzureResources(AzurePetStoreSessionInfo azurePetStoreSessionInfo);
    public DPResponse executeDevopsPipeline(AzurePetStoreSessionInfo azurePetStoreSessionInfo);
}
