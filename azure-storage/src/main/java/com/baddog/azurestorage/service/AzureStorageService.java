package com.baddog.azurestorage.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AzureStorageService {
  private final BlobServiceClient blobServiceClient;

  @Value("${spring.cloud.azure.storage.blob.container-name}")
  private String containerName;

  public Mono<ResponseEntity<Flux<ByteBuffer>>> streamVideo(String id) {
    BlobContainerClient blobContainerClient =
        blobServiceClient.getBlobContainerClient(containerName);
    BlobClient blobClient = blobContainerClient.getBlobClient(id);

    return Mono.fromCallable(blobClient::getProperties)
        .flatMap(
            properties -> {
              Flux<ByteBuffer> videoStream = blobClient.downloadContent().toFluxByteBuffer();
              return Mono.just(
                  ResponseEntity.ok()
                      .contentType(MediaType.parseMediaType("video/mp4"))
                      .contentLength(properties.getBlobSize())
                      .body(videoStream.buffer(10 * 1024).flatMap(Flux::fromIterable)));
            });
  }

  public void uploadVideo(String id, String contentType, MultipartFile videoFile)
      throws IOException {
    BlobContainerClient blobContainerClient =
        blobServiceClient.getBlobContainerClient(containerName);
    BlobClient blobClient = blobContainerClient.getBlobClient(id);
    blobClient.upload(videoFile.getInputStream(), videoFile.getSize(), true);
    blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
  }
}
