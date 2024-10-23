package com.baddog.videostreaming;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VideoStreamingController {
  private final VideoRepo videoRepo;
  private final VideoSender enqueueDequeService;

  @Value("${video-storage.host}")
  private String videoStorageHost;

  @Value("${video-storage.port}")
  private String videoStoragePort;

  @GetMapping("/videos")
  public List<VideoDO> getAllVideos() {
    return videoRepo.findAll();
  }

  // TODO: Convert to WebFlux
  @GetMapping("/video")
  public void streamVideo(
      @RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    VideoDO video = videoRepo.getVideoById(id);
    publishMessage(video);

    String forwardUrl =
        String.format(
            "http://%s:%s/get-object?bucket=%s&key=%s", videoStorageHost, videoStoragePort, video.getBucket(), video.getKey());

    HttpURLConnection connection = (HttpURLConnection) new URL(forwardUrl).openConnection();
    connection.setRequestMethod("GET");

    // Copy headers from the incoming request to the outgoing request
    request
        .getHeaderNames()
        .asIterator()
        .forEachRemaining(
            headerName -> connection.setRequestProperty(headerName, request.getHeader(headerName)));

    // Connect and get response code
    connection.connect();
    int responseCode = connection.getResponseCode();

    // Copy response headers from the forwarded response to the current response
    connection
        .getHeaderFields()
        .forEach(
            (headerName, headerValues) -> {
              if (headerName != null) {
                headerValues.forEach(headerValue -> response.addHeader(headerName, headerValue));
              }
            });

    response.setStatus(responseCode);

    // Copy response body
    try (var inputStream = connection.getInputStream();
        var outputStream = response.getOutputStream()) {
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
    }
  }

  public void publishMessage(VideoDO video) {
    enqueueDequeService.publishMessage(video);
  }
}
