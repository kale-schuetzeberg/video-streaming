package com.baddog.videostreaming.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "video-storage")
@Data
public class VideoStorageDO {
  private String host;
  private String port;
}
