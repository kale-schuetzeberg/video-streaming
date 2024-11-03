package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.services.s3.model.ObjectListing;
import java.nio.ByteBuffer;
import reactor.core.publisher.Flux;

public interface BucketServiceInterface {

  ObjectListing getBucketObjects(String bucket);

  Flux<ByteBuffer> getObject(String bucket, String key);
}
