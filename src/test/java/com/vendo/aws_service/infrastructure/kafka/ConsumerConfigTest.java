package com.vendo.aws_service.infrastructure.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsumerConfigTest {

    private static final String TOPIC = "FILE_TEST_DELETED_TOPIC";

    @Test
    void consumerFactory_shouldDeserializePlainStringValue_whenFileKeyIsSentAsString() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        ReflectionTestUtils.setField(consumerConfig, "KAFKA_BOOTSTRAP_SERVERS", List.of("test"));
        String fileKey = "categories/491aab8d-d71a-442e-a235-589ca95d2c76.jpg";

        ConsumerFactory<String, String> consumerFactory = consumerConfig.consumerFactory();
        Deserializer<String> valueDeserializer = consumerFactory.getValueDeserializer();

        assertThat(valueDeserializer).isNotNull();
        assertThat(valueDeserializer.deserialize(TOPIC, fileKey.getBytes(StandardCharsets.UTF_8))).isEqualTo(fileKey);
    }
}
