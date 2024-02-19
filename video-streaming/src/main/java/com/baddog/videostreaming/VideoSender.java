package com.baddog.videostreaming;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoSender {
  private final AmqpTemplate amqpTemplate;

  @Value("${rabbitmq.queue-name}")
  String queueName;

  @Value("${rabbitmq.exchange}")
  String exchange;

  @Value("${rabbitmq.routing-key}")
  String routingKey;

  public void publishMessage(VideoDO video) {
    amqpTemplate.convertAndSend(exchange, routingKey, video);
  }
}
