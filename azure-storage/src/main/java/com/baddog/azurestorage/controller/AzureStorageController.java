package com.baddog.azurestorage.controller;

import com.baddog.azurestorage.service.AzureStorageService;
import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AzureStorageController {
  private final AzureStorageService azureStorageService;

  @GetMapping("/video")
  public Mono<ResponseEntity<Flux<ByteBuffer>>> streamVideoBlob(@RequestParam("id") String id) {
    return azureStorageService.streamVideo(id);
  }

  @PostMapping("/upload")
  public void uploadVideoBlob(
      @RequestHeader("id") String id,
      @RequestHeader("content-type") String contentType,
      @RequestBody MultipartFile videoFile) {
    try {
      azureStorageService.uploadVideo(id, contentType, videoFile);
    } catch (IOException e) {
      log.error("Error unloading video: " + e.getMessage());
    }
  }
}
