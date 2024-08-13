package com.chtrembl.petstoreapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
     public String uploadFile(MultipartFile file);
}
