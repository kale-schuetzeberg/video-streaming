package com.baddog.azurestorage.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AzureStorageService {
  private final BlobServiceClient blobServiceClient;

  @Value("${spring.cloud.azure.storage.blob.container-name}")
  private String containerName;

  public void streamVideo(String id, HttpServletResponse response) throws IOException {
    BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
    BlobClient blobClient = blobContainerClient.getBlobClient(id);

    // TODO: move video stream to video-streaming service and pass message of file w/ RabbitMQ
    try (InputStream videoStream = blobClient.downloadContent().toStream();
        OutputStream out = response.getOutputStream()) {
      response.setContentType("video/mp4");
      response.setHeader("Content-Disposition", "inline; filename=" + id);

      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = videoStream.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      out.flush();
    }
  }

  public void uploadVideo(String id, String contentType, MultipartFile videoFile) throws IOException {
    BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
    BlobClient blobClient = blobContainerClient.getBlobClient(id);
    blobClient.upload(videoFile.getInputStream(), videoFile.getSize(), true);
    blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
  }
}
