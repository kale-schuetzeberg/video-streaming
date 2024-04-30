package com.baddog.azurestorage.controller;

import com.baddog.azurestorage.service.AzureStorageService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AzureStorageController {
  private final AzureStorageService azureStorageService;

  @GetMapping("/video")
  public void streamVideoBlob(@RequestParam("id") String id, HttpServletResponse response) {
    try {
      azureStorageService.streamVideo(id, response);
    } catch (IOException e) {
      log.error("Error downloading video: " + e.getMessage());
    }
  }

  @PostMapping("/upload")
  public void uploadVideoBlob(@RequestHeader("id") String id, @RequestHeader("content-type") String contentType, @RequestBody MultipartFile videoFile) {
      try {
        azureStorageService.uploadVideo(id, contentType, videoFile);
      } catch (IOException e) {
        log.error("Error unloading video: " + e.getMessage());
      }
  }
}
