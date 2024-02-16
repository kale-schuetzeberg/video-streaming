package com.baddog.azurestorage;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AzureStorageController {
  private final AzureStorageService azureStorageService;

  @GetMapping("/video")
  public void streamVideoBlob(@RequestParam("path") String path, HttpServletResponse response) {
    try {
      azureStorageService.streamVideo(path, response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
