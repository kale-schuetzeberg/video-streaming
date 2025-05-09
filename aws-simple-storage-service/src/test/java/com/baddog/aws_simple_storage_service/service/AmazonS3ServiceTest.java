package com.baddog.aws_simple_storage_service.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

class AmazonS3ServiceTest {

  private AmazonS3 amazonS3Client;
  private AmazonS3Service amazonS3Service;

  @BeforeEach
  void setUp() {
    amazonS3Client = mock(AmazonS3.class);
    amazonS3Service = new AmazonS3Service(amazonS3Client);
  }

  @Test
  void testGetBucketObjects_withValidBucket_returnsListing() {
    String bucket = "valid-bucket";
    ObjectListing objectListing = mock(ObjectListing.class);

    when(amazonS3Client.listObjects(bucket)).thenReturn(objectListing);
    when(objectListing.getObjectSummaries()).thenReturn(java.util.Collections.emptyList());

    ObjectListing result = amazonS3Service.getBucketObjects(bucket);

    assertSame(objectListing, result);
    verify(amazonS3Client, times(1)).listObjects(bucket);
  }

  @Test
  void testGetBucketObjects_withNullBucket_throwsException() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> amazonS3Service.getBucketObjects(null)
    );
    assertEquals("Bucket name must not be null or blank", exception.getMessage());
  }

  @Test
  void testGetBucketObjects_withBlankBucket_throwsException() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> amazonS3Service.getBucketObjects("  ")
    );
    assertEquals("Bucket name must not be null or blank", exception.getMessage());
  }

  @Test
  void testGetBucketObjects_withAmazonServiceException_throwsWrappedException() {
    String bucket = "test-bucket";
    AmazonServiceException ase = new AmazonServiceException("S3 error");
    when(amazonS3Client.listObjects(bucket)).thenThrow(ase);

    AmazonServiceException thrown = assertThrows(
            AmazonServiceException.class,
            () -> amazonS3Service.getBucketObjects(bucket)
    );
    assertNotSame(ase, thrown); // it's a wrapped exception, not the same reference
    assertEquals(ase, thrown.getCause());
    assertTrue(thrown.getMessage().startsWith("S3 error"));
    // or just contains the key information
    assertTrue(thrown.getMessage().contains("S3 error"));
  }



  @Test
  void testGetObject_success_withHeaders() throws Exception {
    String bucket = "bucket";
    String key = "video/video.mp4";
    byte[] fileContent = "mock video content".getBytes();

    // Mock S3Object and its stream
    S3Object s3Object = mock(S3Object.class);
    S3ObjectInputStream inputStream = new S3ObjectInputStream(new ByteArrayInputStream(fileContent), null);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("video/mp4");
    metadata.setContentLength(fileContent.length);

    when(amazonS3Client.getObject(bucket, key)).thenReturn(s3Object);
    when(s3Object.getObjectContent()).thenReturn(inputStream);
    when(s3Object.getObjectMetadata()).thenReturn(metadata);

    // Actually call the service
    ResponseEntity<StreamingResponseBody> response = amazonS3Service.getObject(bucket, key);

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());

    // Headers should match
    assertEquals("video/mp4", response.getHeaders().getFirst("Content-Type"));
    assertEquals(String.valueOf(fileContent.length), response.getHeaders().getFirst("Content-Length"));
    assertEquals("attachment; filename=\"video.mp4\"", response.getHeaders().getFirst("Content-Disposition"));

    // Body should stream S3 content
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    response.getBody().writeTo(outputStream);
    assertArrayEquals(fileContent, outputStream.toByteArray());

    // S3Object should be closed
    verify(s3Object).close();
  }

  @Test
  void testGetObject_amazonServiceException() {
    String bucket = "bucket";
    String key = "notfound";

    when(amazonS3Client.getObject(bucket, key)).thenThrow(new AmazonServiceException("No such key"));

    ResponseEntity<StreamingResponseBody> response = amazonS3Service.getObject(bucket, key);

    assertEquals(404, response.getStatusCodeValue());
    assertNull(response.getBody());
  }

  @Test
  void testGetObject_ioExceptionDuringStreaming() throws Exception {
    String bucket = "bucket";
    String key = "file.txt";

    S3Object s3Object = mock(S3Object.class);

    // S3ObjectInputStream that throws IOException
    S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
    when(inputStream.read(any(byte[].class))).thenThrow(new IOException("Broken stream"));

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("text/plain");
    metadata.setContentLength(100);

    when(amazonS3Client.getObject(bucket, key)).thenReturn(s3Object);
    when(s3Object.getObjectContent()).thenReturn(inputStream);
    when(s3Object.getObjectMetadata()).thenReturn(metadata);

    ResponseEntity<StreamingResponseBody> response = amazonS3Service.getObject(bucket, key);

    assertEquals(200, response.getStatusCodeValue());
    StreamingResponseBody body = response.getBody();
    assertNotNull(body);

    // Streaming should throw IOException and method should handle (could log)
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    assertThrows(IOException.class, () -> body.writeTo(out));

    // S3Object should be closed even if exception occurs
    verify(s3Object).close();
  }


  @Test
  void testPutObject_success() throws Exception {
    String bucket = "test-bucket";
    String key = "file.txt";
    MultipartFile multipartFile = mock(MultipartFile.class);
    byte[] fileBytes = "dummy content".getBytes();
    InputStream inputStream = new ByteArrayInputStream(fileBytes);

    when(multipartFile.getInputStream()).thenReturn(inputStream);

    ResponseEntity<Void> response = amazonS3Service.putObject(bucket, key, multipartFile);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(amazonS3Client, times(1)).putObject(eq(bucket), eq(key), any(InputStream.class), isNull());
  }

  @Test
  void testPutObject_amazonServiceException() throws Exception {
    String bucket = "test-bucket";
    String key = "file.txt";
    MultipartFile multipartFile = mock(MultipartFile.class);
    InputStream inputStream = new ByteArrayInputStream("dummy".getBytes());

    when(multipartFile.getInputStream()).thenReturn(inputStream);
    doThrow(new AmazonServiceException("AWS error")).when(amazonS3Client)
            .putObject(eq(bucket), eq(key), any(InputStream.class), isNull());

    ResponseEntity<Void> response = amazonS3Service.putObject(bucket, key, multipartFile);

    assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    verify(amazonS3Client, times(1)).putObject(eq(bucket), eq(key), any(InputStream.class), isNull());
  }

  @Test
  void testPutObject_ioException() throws Exception {
    String bucket = "test-bucket";
    String key = "file.txt";
    MultipartFile multipartFile = mock(MultipartFile.class);

    when(multipartFile.getInputStream()).thenThrow(new IOException("IO error"));

    ResponseEntity<Void> response = amazonS3Service.putObject(bucket, key, multipartFile);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    verify(amazonS3Client, never()).putObject(any(), any(), any(), any());
  }

}
