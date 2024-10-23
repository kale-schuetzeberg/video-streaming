package com.baddog.aws_simple_storage_service.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class BucketConfigTest {

    @Autowired
    private BucketConfig bucketConfig;

    @Test
    void givenBucketConfig_whenGetAmazonS3ClientIsCalled_thenExpectAmazonS3Object() {
        var actual = bucketConfig.getAmazonS3Client();

        // assert AmazonS3 object is returned when getAmazonS3Client is called
        assertThat(actual instanceof AmazonS3).isTrue();
        // assert object has region US_EAST_1
        assertThat(actual.getRegion()).isEqualTo(Regions.US_EAST_1);
        // assert environment variables
        //      -> this can prevent running application somewhere w/out
        //         the environment variables

    }





}
