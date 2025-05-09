package com.baddog.videostreaming.kafka;

import java.util.Properties;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
  private final KafkaProducer<String, String> producer;
  private final String topic;

  public KafkaProducerService() {
    Properties kafkaProperties = new Properties();
    kafkaProperties.put("bootstrap.servers", "broker:9092");
    kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    kafkaProperties.put(
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    this.producer = new KafkaProducer<>(kafkaProperties);
    this.topic = "video";
  }

  public void sendMessage(String key, String value) {
    ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

    try {
      Future<RecordMetadata> future = producer.send(record);
      // Optionally, wait for the result:
      // RecordMetadata metadata = future.get();
    } catch (Exception e) {
      // Handle exceptions such as serialization or broker issues
      e.printStackTrace();
    }
  }

  public void close() {
    producer.close();
  }
}
