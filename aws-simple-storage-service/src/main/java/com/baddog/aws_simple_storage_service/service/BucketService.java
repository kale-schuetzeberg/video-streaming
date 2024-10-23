package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    // Retrieve the S3 object
    S3Object s3Object = amazonS3Client.getObject(bucket, key);
    S3ObjectInputStream inputStream = s3Object.getObjectContent();

    final StreamingResponseBody body =
        outputStream -> {
          int numberOfBytesToWrite = 0;
          byte[] data = new byte[1024];
          while ((numberOfBytesToWrite = inputStream.read(data, 0, data.length)) != -1) {
            outputStream.write(data, 0, numberOfBytesToWrite);
          }
          inputStream.close();
        };
    return ResponseEntity.ok().header("Content-Type", "audio/mp4").body(body);
  }
}
