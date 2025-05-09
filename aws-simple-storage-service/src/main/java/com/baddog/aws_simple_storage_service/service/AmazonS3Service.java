package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service implements AmazonS3ServiceInterface {
  private final AmazonS3 amazonS3Client;

  @Override
  public ResponseEntity<?> getObjects(String bucket) {
    if (bucket == null || bucket.isBlank()) {
      log.warn("Bucket name must not be null or blank");
      return ResponseEntity.badRequest().build();
    }
    try {
      ObjectListing listing = amazonS3Client.listObjects(bucket);
      log.debug("Listed objects in bucket '{}': {} objects found", bucket,
              listing.getObjectSummaries().size());
      return ResponseEntity.ok(listing);
    } catch (AmazonServiceException e) {
      log.error("Failed to list objects in bucket '{}': {}", bucket, e.getMessage());
      String errorCode = e.getErrorCode();
      if ("NoSuchBucket".equals(errorCode)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Bucket '%s' does not exist", bucket));
      } else if ("AccessDenied".equals(errorCode)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @Override
  public ResponseEntity<StreamingResponseBody> getObject(String bucket, String key) {
    try {
      S3Object s3Object = amazonS3Client.getObject(bucket, key);
      StreamingResponseBody body = getStreamingResponseBody(s3Object);

      return ResponseEntity.ok()
              .header("Content-Type", "video/mp4")
              .header("Content-Length", String.valueOf(s3Object.getObjectMetadata().getContentLength()))
              .header("Content-Disposition",
                      "attachment; filename=\"" + key.substring(key.lastIndexOf('/') + 1) + "\"")
              .body(body);

    } catch (AmazonServiceException e) {
      log.error("Error getting object from S3: bucket={}, key={}, error={}", bucket, key, e.getMessage());
      String errorCode = e.getErrorCode();
      if ("NoSuchKey".equals(errorCode) || "NoSuchBucket".equals(errorCode)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      } else if ("AccessDenied".equals(errorCode)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<Void> putObject(String bucket, String key, MultipartFile file) {
    try (InputStream inputStream = file.getInputStream()) {
      amazonS3Client.putObject(bucket, key, inputStream, null);
      return ResponseEntity.ok().build();
    } catch (AmazonServiceException e) {
      log.error("Amazon S3 couldn't process the request: {}", e.getMessage(), e);
      String errorCode = e.getErrorCode();
      if ("NoSuchKey".equals(errorCode) || "NoSuchBucket".equals(errorCode)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      } else if ("AccessDenied".equals(errorCode)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (IOException e) {
      log.error("Failed to read file input stream: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  private StreamingResponseBody getStreamingResponseBody(S3Object s3Object) {
    S3ObjectInputStream inputStream = s3Object.getObjectContent();
    return outputStream -> {
      try (S3ObjectInputStream is = inputStream) {
        byte[] buffer = new byte[16 * 1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }
      } finally {
        s3Object.close();
      }
    };
  }
}
