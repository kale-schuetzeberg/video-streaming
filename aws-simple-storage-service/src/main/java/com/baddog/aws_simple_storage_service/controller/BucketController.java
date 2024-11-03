package com.baddog.aws_simple_storage_service.controller;

import com.amazonaws.services.s3.model.ObjectListing;
import com.baddog.aws_simple_storage_service.service.BucketService;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class BucketController {
  private final BucketService bucketService;

  @GetMapping("/get-bucket-objects")
  public ObjectListing getBucketList(@RequestParam(name = "bucket") String bucket) {
    return bucketService.getBucketObjects(bucket);
  }

  @GetMapping("/get-object")
  public Flux<ByteBuffer> getObject(
      @RequestParam(name = "bucket") String bucket, @RequestParam(name = "key") String key) {
    return bucketService.getObject(bucket, key);
  }
}
