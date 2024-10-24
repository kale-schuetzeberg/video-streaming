package com.baddog.aws_simple_storage_service.service;

import com.amazonaws.services.s3.model.Bucket;
import java.util.List;

public interface BucketServiceInterface {
  List<Bucket> getBucketList();
}
