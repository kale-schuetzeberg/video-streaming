package com.baddog.awssimplestorageservice.service;

import com.amazonaws.services.s3.model.ObjectListing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AmazonS3ServiceInterface {

  ResponseEntity<ObjectListing> getObjects(String bucket);

  ResponseEntity<StreamingResponseBody> getObject(String bucket, String key);

  ResponseEntity<Void> putObject(String bucket, String key, MultipartFile file);
}
