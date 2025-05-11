package com.baddog.awssimplestorageservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

class AmazonS3ServiceTest {

  private AmazonS3 amazonS3Client;
  private AmazonS3Service amazonS3Service;
  private MultipartFile multipartFile;

  @BeforeEach
  void setUp() {
    amazonS3Client = mock(AmazonS3.class);
    amazonS3Service = new AmazonS3Service(amazonS3Client);
  }

  // getObjects
  @Test
  void givenValidBucketName_whenGettingBucketObjects_thenReturnBucketObjects() {
    String bucket = "valid-bucket";
    ObjectListing objectListing = mock(ObjectListing.class);

    when(amazonS3Client.listObjects(bucket)).thenReturn(objectListing);
    when(objectListing.getObjectSummaries()).thenReturn(java.util.Collections.emptyList());

    ResponseEntity<?> result = amazonS3Service.getObjects(bucket);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isSameAs(objectListing);
    verify(amazonS3Client, times(1)).listObjects(bucket);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {" "})
  void givenInvalidBucketName_whenGettingBucketObjects_thenReturn400BadRequest(String bucketName) {
    ResponseEntity<?> result = amazonS3Service.getObjects(bucketName);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(result.getBody()).isNull();

    verifyNoInteractions(amazonS3Client);
  }

  @Test
  void
      givenNonExistentBucketName_whenGettingBucketObjects_thenReturn404NotFoundAndCorrectMessage() {
    String bucket = "non-existent-bucket";
    AmazonServiceException ase = new AmazonServiceException("Bucket does not exist");
    ase.setErrorCode("NoSuchBucket"); // simulate this specific S3 error

    when(amazonS3Client.listObjects(bucket)).thenThrow(ase);

    ResponseEntity<?> response = amazonS3Service.getObjects(bucket);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(amazonS3Client, times(1)).listObjects(bucket);
  }

  @Test
  void
      givenAmazonServiceExceptionWithOtherErrorCode_whenGettingBucketObjects_thenReturn500InternalServerError() {
    String bucket = "any-bucket";
    AmazonServiceException ase = new AmazonServiceException("Other error");
    ase.setErrorCode("InternalError");

    when(amazonS3Client.listObjects(bucket)).thenThrow(ase);

    ResponseEntity<?> response = amazonS3Service.getObjects(bucket);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNull();
    verify(amazonS3Client, times(1)).listObjects(bucket);
  }

  // getObject
  @Test
  void givenValidBucketAndKey_whenGettingObject_thenReturnStreamingResponseWithCorrectHeaders()
      throws Exception {
    String bucket = "valid-bucket";
    String key = "videos/video.mp4";
    long fileLength = 123456L;

    S3Object s3Object = mock(S3Object.class);
    S3ObjectInputStream s3ObjectInputStream =
        new S3ObjectInputStream(new ByteArrayInputStream("dummy data".getBytes()), null);
    ObjectMetadata metadata = mock(ObjectMetadata.class);

    when(amazonS3Client.getObject(bucket, key)).thenReturn(s3Object);
    when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
    when(s3Object.getObjectMetadata()).thenReturn(metadata);
    when(metadata.getContentLength()).thenReturn(fileLength);

    ResponseEntity<StreamingResponseBody> response = amazonS3Service.getObject(bucket, key);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getFirst("Content-Type")).isEqualTo("video/mp4");
    assertThat(response.getHeaders().getFirst("Content-Length"))
        .isEqualTo(String.valueOf(fileLength));
    assertThat(response.getHeaders().getFirst("Content-Disposition"))
        .isEqualTo("attachment; filename=\"video.mp4\"");
    assertNotNull(response.getBody());

    ByteArrayOutputStream streamed = new ByteArrayOutputStream();
    response.getBody().writeTo(streamed);
    assertThat(streamed.toByteArray()).isNotEmpty();

    verify(amazonS3Client, times(1)).getObject(bucket, key);
    verify(s3Object, times(1)).close();
  }

  @Test
  void givenNonExistentBucketName_whenGettingObject_thenReturnNotFound() {
    String bucket = "non-existent-bucket";
    String key = "videos/video.mp4";
    AmazonServiceException ase = new AmazonServiceException("Bucket does not exist");
    ase.setErrorCode("NoSuchBucket");

    when(amazonS3Client.getObject(bucket, key)).thenThrow(ase);

    ResponseEntity<StreamingResponseBody> response = amazonS3Service.getObject(bucket, key);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertNull(response.getBody());
    verify(amazonS3Client, times(1)).getObject(bucket, key);
  }

  @Test
  void givenAmazonServiceExceptionWithAccessDenied_whenGettingObject_thenReturn403Forbidden() {
    String bucket = "my-bucket";
    String key = "secret-file.mp4";
    AmazonServiceException ase = new AmazonServiceException("Access Denied");
    ase.setErrorCode("AccessDenied");

    when(amazonS3Client.getObject(bucket, key)).thenThrow(ase);

    ResponseEntity<StreamingResponseBody> response = amazonS3Service.getObject(bucket, key);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertNull(response.getBody());
    verify(amazonS3Client, times(1)).getObject(bucket, key);
  }

  @Test
  void
      givenAmazonServiceExceptionWithInternalError_whenGettingObject_thenReturn500InternalServerError() {
    String bucket = "any-bucket";
    String key = "video.mp4";
    AmazonServiceException ase = new AmazonServiceException("Internal Error");
    ase.setErrorCode("InternalError");

    when(amazonS3Client.getObject(bucket, key)).thenThrow(ase);

    ResponseEntity<StreamingResponseBody> response = amazonS3Service.getObject(bucket, key);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertNull(response.getBody());
    verify(amazonS3Client, times(1)).getObject(bucket, key);
  }

  // putObject
  @Test
  void givenValidBucketKeyAndFile_whenPuttingObject_thenReturn200OK() throws Exception {
    String bucket = "my-bucket";
    String key = "file.mp4";
    byte[] fileContent = "test file".getBytes();

    MultipartFile file = mock(MultipartFile.class);
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

    ResponseEntity<Void> response = amazonS3Service.putObject(bucket, key, file);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(file, times(1)).getInputStream();
    verify(amazonS3Client, times(1)).putObject(bucket, key, file.getInputStream(), null);
  }

  @Test
  void givenAmazonServiceException_whenPuttingObject_thenReturnBadGateway() throws Exception {
    String bucket = "my-bucket";
    String key = "fail.mp4";
    MultipartFile file = mock(MultipartFile.class);
    InputStream inputStream = new ByteArrayInputStream("irrelevant".getBytes());
    when(file.getInputStream()).thenReturn(inputStream);

    AmazonServiceException exception = new AmazonServiceException("Service error");
    doThrow(exception).when(amazonS3Client).putObject(bucket, key, inputStream, null);

    ResponseEntity<Void> response = amazonS3Service.putObject(bucket, key, file);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    verify(file, times(1)).getInputStream();
    verify(amazonS3Client, times(1)).putObject(bucket, key, inputStream, null);
  }

  @Test
  void givenIOException_whenPutObject_thenReturnBadRequest() throws Exception {
    String bucket = "my-bucket";
    String key = "iofail.mp4";
    MultipartFile file = mock(MultipartFile.class);

    when(file.getInputStream()).thenThrow(new IOException("Cannot read file"));

    ResponseEntity<Void> response = amazonS3Service.putObject(bucket, key, file);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    verify(file, times(1)).getInputStream();
    verifyNoInteractions(amazonS3Client);
  }

  // getStreamingResponseBody
  @Test
  void givenS3Object_whenGetStreamingResponseBody_thenStreamsAllBytesAndClosesS3Object()
      throws Exception {
    // Given
    S3Object s3Object = mock(S3Object.class);
    byte[] expectedBytes = "streamed content".getBytes();
    S3ObjectInputStream s3InputStream =
        new S3ObjectInputStream(new ByteArrayInputStream(expectedBytes), null);

    when(s3Object.getObjectContent()).thenReturn(s3InputStream);

    // Reflection to access the private method
    var method =
        AmazonS3Service.class.getDeclaredMethod("getStreamingResponseBody", S3Object.class);
    method.setAccessible(true);
    StreamingResponseBody body = (StreamingResponseBody) method.invoke(amazonS3Service, s3Object);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // When
    body.writeTo(outputStream);

    // Then
    assertThat(outputStream.toByteArray()).isEqualTo(expectedBytes);
    verify(s3Object, times(1)).close();
  }
}
