package com.baddog.awssimplestorageservice.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.ObjectListing;
import com.baddog.awssimplestorageservice.service.AmazonS3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@ExtendWith(MockitoExtension.class)
class AmazonS3ControllerTest {

  @Mock private AmazonS3Service bucketService;

  @InjectMocks private AmazonS3Controller bucketController;

  @Test
  void givenValidBucketName_whenGettingVideos_thenReturnVideos() {
    String bucket = "test-bucket";
    ObjectListing listing = mock(ObjectListing.class);
    ResponseEntity<ObjectListing> expected = ResponseEntity.ok(listing);

    when(bucketService.getObjects(bucket)).thenReturn(expected);

    ResponseEntity<?> actual = bucketController.getVideos(bucket);

    assertThat(actual).isSameAs(expected);
    verify(bucketService).getObjects(bucket);
  }

  @Test
  void givenValidBucketNameAndVideoName_whenGettingVideo_thenReturnVideo() {
    String bucket = "test-bucket";
    String key = "test-key";
    @SuppressWarnings("unchecked")
    ResponseEntity<StreamingResponseBody> expected = mock(ResponseEntity.class);

    when(bucketService.getObject(bucket, key)).thenReturn(expected);

    ResponseEntity<StreamingResponseBody> actual = bucketController.getVideo(bucket, key);

    assertSame(expected, actual);
    verify(bucketService).getObject(bucket, key);
  }

  @Test
  void givenValidBucketNameAndVideoNameAndVideoFile_whenPuttingVideo_thenReturnHttpStatusOk() {
    String bucket = "test-bucket";
    String key = "test-key";
    MultipartFile file = mock(MultipartFile.class);
    ResponseEntity<Void> expected = new ResponseEntity<>(HttpStatus.OK);

    when(bucketService.putObject(bucket, key, file)).thenReturn(expected);

    ResponseEntity<?> actual = bucketController.putVideo(bucket, key, file);

    assertSame(expected, actual);
    verify(bucketService).putObject(bucket, key, file);
  }
}
