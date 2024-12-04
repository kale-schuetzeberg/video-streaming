package com.baddog.databasefixture.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videos")
@Data
public class VideoDO {
  private String bucket;
  private String key;
}
