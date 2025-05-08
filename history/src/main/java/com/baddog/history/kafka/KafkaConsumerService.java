package com.baddog.history.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService implements ApplicationRunner {
  private final KafkaConsumer<String, String> consumer;

  public KafkaConsumerService() {
    Properties kafkaProperties = new Properties();
    kafkaProperties.put("bootstrap.servers", "broker:9092");
    kafkaProperties.put("group.id", "VideoConsumer");
    kafkaProperties.put(
        "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    kafkaProperties.put(
        "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    this.consumer = new KafkaConsumer<>(kafkaProperties);
  }

  @Override
  public void run(ApplicationArguments args) {
    new Thread(this::pollConsumer, "kafka-video-consumer-thread").start();
  }

  private void pollConsumer() {
    Duration timeout = Duration.ofSeconds(100);

      consumer.subscribe(List.of("video"));

      while (true) {
      ConsumerRecords<String, String> records = consumer.poll(timeout);

      for (ConsumerRecord<String, String> record : records) {
        System.out.printf(
            "topic = %s, video = %s, partition = %d, offset = %d\n",
            record.topic(), record.value(), record.partition(), record.offset());
      }
    }
  }
}
