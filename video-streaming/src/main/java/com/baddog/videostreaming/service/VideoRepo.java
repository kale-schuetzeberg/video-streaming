package com.baddog.videostreaming.service;

import com.baddog.videostreaming.model.VideoDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface VideoRepo extends MongoRepository<VideoDO, String> {
  @Query("{id: '?0'}")
  VideoDO getVideoById(String id);
}
