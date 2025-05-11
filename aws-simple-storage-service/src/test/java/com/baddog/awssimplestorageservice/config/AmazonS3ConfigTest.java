package com.baddog.awssimplestorageservice.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(classes = AmazonS3Config.class)
public class AmazonS3ConfigTest {

  @DynamicPropertySource
  static void AmazonS3Props(DynamicPropertyRegistry registry) {
    registry.add("aws.region", () -> "us-east-1");
  }

  @Autowired AmazonS3 amazonS3;

  @Test
  void contextLoads() {
    assertThat(amazonS3).isNotNull();
  }
}
