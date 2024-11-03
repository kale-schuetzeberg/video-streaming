package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class BucketService implements BucketServiceInterface {
  private final AmazonS3 amazonS3Client;

  @Override
  public ObjectListing getBucketObjects(String bucket) {
    return amazonS3Client.listObjects(bucket);
  }

  @Override
  public Flux<ByteBuffer> getObject(String bucket, String key) {
    // Retrieve the S3 object
    S3Object s3Object = amazonS3Client.getObject(bucket, key);
    S3ObjectInputStream inputStream = s3Object.getObjectContent();

    // Create a Flux<ByteBuffer> from the InputStream
    return Flux.create(
        sink -> {
          byte[] buffer = new byte[1024 * 10]; // 10 KB buffer
          int bytesRead;
          try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
              sink.next(ByteBuffer.wrap(buffer, 0, bytesRead));
            }
            sink.complete();
          } catch (Exception e) {
            sink.error(e);
          } finally {
            try {
              inputStream.close(); // Ensure the input stream is closed
            } catch (Exception e) {
              // Handle error on close if needed
              sink.error(e);
            }
          }
        });
  }
}
