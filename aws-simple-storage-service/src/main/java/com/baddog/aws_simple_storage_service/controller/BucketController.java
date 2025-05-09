package com.baddog.aws_simple_storage_service.controller;

import com.amazonaws.services.s3.model.ObjectListing;
import com.baddog.aws_simple_storage_service.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BucketController {
  private final BucketService bucketService;

  @GetMapping("/videos")
  public ObjectListing getBucketList(@RequestParam(name = "bucket") String bucket) {
    return bucketService.getBucketObjects(bucket);
  }

  @GetMapping("/video")
  public ResponseEntity<StreamingResponseBody> getObject(
      @RequestParam(name = "bucket") String bucket, @RequestParam(name = "key") String key) {
    return bucketService.getObject(bucket, key);
  }

  @PostMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<?> uploadVideo(@RequestParam(name = "bucket") String bucket, @RequestParam(name = "key") String key, @RequestParam(name = "file") MultipartFile file) {
    return bucketService.putObject(bucket, key, file);
  }
}
