package com.chtrembl.petstoreassistant.service;

import com.chtrembl.petstoreassistant.model.AzurePetStoreSessionInfo;
import com.chtrembl.petstoreassistant.model.DPResponse;

public interface IAzurePetStore {
    public DPResponse updateCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo, String productId);
    public DPResponse viewCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo);       
    public DPResponse completeCart(AzurePetStoreSessionInfo azurePetStoreSessionInfo);  
}
