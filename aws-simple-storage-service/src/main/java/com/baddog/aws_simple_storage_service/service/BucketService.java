package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class BucketService implements BucketServiceInterface {
  private final AmazonS3 amazonS3Client;

  @Override
  public ObjectListing getBucketObjects(String bucket) {
    return amazonS3Client.listObjects(bucket);
  }

  @Override
      public ResponseEntity<StreamingResponseBody> getObject(String bucket, String key) {
          S3Object s3Object = amazonS3Client.getObject(bucket, key);
          S3ObjectInputStream inputStream = s3Object.getObjectContent();

          StreamingResponseBody body = outputStream -> {
              try (S3ObjectInputStream is = inputStream) {
                  byte[] buffer = new byte[1024];
                  int bytesRead;
                  while ((bytesRead = is.read(buffer)) != -1) {
                      outputStream.write(buffer, 0, bytesRead);
                  }
              }
          };
          return new ResponseEntity<>(body, HttpStatus.OK);
      }


      @Override
  public ResponseEntity<?> putObject(String bucket, String key, MultipartFile file) {
      try (InputStream inputStream = file.getInputStream()) {
          amazonS3Client.putObject(bucket, key, inputStream, null);
      } catch (Exception e) {
          e.printStackTrace();
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>(HttpStatus.OK);
  }

}
