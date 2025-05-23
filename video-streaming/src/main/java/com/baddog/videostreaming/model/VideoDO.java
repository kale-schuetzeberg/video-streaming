package com.baddog.videostreaming.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videos")
@Data
public class VideoDO {
  @Id private String id;
  private String bucket;
  private String key;
}
