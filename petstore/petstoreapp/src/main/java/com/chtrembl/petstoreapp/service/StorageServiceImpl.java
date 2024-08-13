package com.chtrembl.petstoreapp.service;


import java.io.BufferedInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.specialized.BlockBlobClient;

@Component
public class StorageServiceImpl implements StorageService {
   	private static Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    private final BlobServiceClient blobServiceClient;
    private final BlobContainerClient containerClient;

    public StorageServiceImpl(@Value("${audio.blob.connection-string:}") String blobConnectionString, @Value("${audio.blob.container-name:}") String blobContainerName) {
        this.blobServiceClient = new BlobServiceClientBuilder().connectionString(blobConnectionString).buildClient();
        this.containerClient = blobServiceClient.getBlobContainerClient(blobContainerName);
    }

    public String uploadFile(MultipartFile file)
     {
        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
                // Get a reference to a blob
                BlockBlobClient blobClient = containerClient.getBlobClient(file.getOriginalFilename()).getBlockBlobClient();

                // Upload the file
                blobClient.upload(inputStream, file.getSize(), true);

                logger.info("Uploaded file to Azure Blob Storage: " + file.getOriginalFilename());

                // Set the content type
                BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
                blobClient.setHttpHeaders(headers);

                // Return the URL of the uploaded file
                return blobClient.getBlobUrl();
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload file to Azure Blob Storage", e);
            }
     }
}