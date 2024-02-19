package com.baddog.history;

import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class VideoReceiver {
  @Value("${rabbitmq.queue-name}")
  String queueName;

  List<String> viewed = new ArrayList<>();

  @RabbitListener(queues = "${rabbitmq.queue-name}")
  public void handleMessage(VideoDO video) {
    System.out.println("Received message from queue " + queueName + ": " + video.getVideoPath());
    viewed.add(video.getVideoPath());
    System.out.println(viewed.size());
  }
}
