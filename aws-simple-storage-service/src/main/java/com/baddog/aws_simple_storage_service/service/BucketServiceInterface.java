package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.InputStream;

public interface BucketServiceInterface {

  ObjectListing getBucketObjects(String bucket);

  ResponseEntity<StreamingResponseBody> getObject(String bucket, String key);

  ResponseEntity<?> putObject(String bucket, String key, MultipartFile file);
}
