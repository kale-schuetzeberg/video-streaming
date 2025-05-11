package com.baddog.awssimplestorageservice.controller;

import com.amazonaws.services.s3.model.ObjectListing;
import com.baddog.awssimplestorageservice.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
public class AmazonS3Controller {
  private final AmazonS3Service bucketService;

  @GetMapping("/videos")
  public ResponseEntity<ObjectListing> getVideos(@RequestParam(name = "bucket") String bucket) {
    return bucketService.getObjects(bucket);
  }

  @GetMapping("/video")
  public ResponseEntity<StreamingResponseBody> getVideo(
      @RequestParam(name = "bucket") String bucket, @RequestParam(name = "key") String key) {
    return bucketService.getObject(bucket, key);
  }

  @PostMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<Void> putVideo(
      @RequestParam(name = "bucket") String bucket,
      @RequestParam(name = "key") String key,
      @RequestParam(name = "file") MultipartFile file) {
    return bucketService.putObject(bucket, key, file);
  }
}
