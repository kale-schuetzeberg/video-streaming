package com.baddog.azurestorage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AzureStorageService {
  private final BlobServiceClient blobServiceClient;

  @Value("${spring.cloud.azure.storage.blob.container-name}")
  private String containerName;

  public void streamVideo(String path, HttpServletResponse response) throws IOException {
    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
    BlobClient blobClient = containerClient.getBlobClient(path);

    try (InputStream videoStream = blobClient.downloadContent().toStream();
        OutputStream out = response.getOutputStream()) {
      response.setContentType("video/mp4");
      response.setHeader("Content-Disposition", "inline; filename=" + path);

      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = videoStream.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      out.flush();
    }
  }
}
