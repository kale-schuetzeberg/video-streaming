package com.baddog.videostreaming;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
@Data
public class RabbitConnectionDO {
  private String queueName;
  private String exchange;
  private String routingKey;
}
