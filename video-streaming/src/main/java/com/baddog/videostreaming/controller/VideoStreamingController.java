package com.baddog.videostreaming.controller;

import com.baddog.videostreaming.model.VideoDO;
import com.baddog.videostreaming.service.VideoStreamingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class VideoStreamingController {
    private final VideoStreamingService videoStreamingService;

    @GetMapping("/videos")
    public List<VideoDO> getAllVideos() {
        return videoStreamingService.getAllVideos();
    }

    // TODO: Convert to WebFlux
    @GetMapping("/video")
    public void streamVideo(
            @RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        videoStreamingService.streamVideo(id, request, response);

    }
}
