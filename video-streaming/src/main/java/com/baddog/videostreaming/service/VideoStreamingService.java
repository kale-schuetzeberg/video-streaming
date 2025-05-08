package com.baddog.videostreaming.service;

import com.baddog.videostreaming.model.VideoDO;
import com.baddog.videostreaming.kafka.KafkaProducerService;
import com.baddog.videostreaming.model.VideoStorageDO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoStreamingService {
    private final VideoRepo videoRepo;
    private final VideoStorageDO videoStorageDO;

    public void streamVideo(String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var video = getVideo(id);

        String forwardUrl =
                String.format(
                        "http://%s:%s/get-object?bucket=%s&key=%s",
                        videoStorageDO.getHost(), videoStorageDO.getPort(), video.getBucket(), video.getKey());

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


    public List<VideoDO> getAllVideos() {
        return videoRepo.findAll();
    }

    private VideoDO getVideo(String videoId) {

        var video = videoRepo.findById(videoId).orElse(null);
        if (video != null) {
            final KafkaProducerService producer = new KafkaProducerService();
            producer.sendMessage("video-streaming", video.getKey());
            producer.close();
        }
        return video;
    }
}
