package com.baddog.aws_simple_storage_service.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.baddog.aws_simple_storage_service.service.BucketService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bucket")
public class BucketController {
  private final BucketService bucketService;

  @GetMapping
  public List<Bucket> getBucketList() {
    List<Bucket> bucketList = bucketService.getBucketList();
    System.out.println("aws s3 bucket list: " + bucketList);
    return bucketList;
  }
}
