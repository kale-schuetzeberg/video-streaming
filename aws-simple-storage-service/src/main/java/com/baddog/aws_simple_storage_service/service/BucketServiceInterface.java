package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.services.s3.model.ObjectListing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface BucketServiceInterface {

  ObjectListing getBucketObjects(String bucket);

  ResponseEntity<StreamingResponseBody> getObject(String bucket, String key);
}
