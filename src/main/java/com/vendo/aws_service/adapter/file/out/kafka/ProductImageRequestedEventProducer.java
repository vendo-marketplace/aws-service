package com.vendo.aws_service.adapter.file.out.kafka;

import com.vendo.event_lib.product.ProductImageRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ProductImageRequestedEventProducer {

    @Value("${kafka.events.product.image-requested-event.topic}")
    private String productCreatedEventTopic;

    private final KafkaTemplate<String, ProductImageRequestedEvent> kafkaTemplate;

    public void send(ProductImageRequestedEvent event) {
        log.info("Sent event for product image requested: {}.", event);
        kafkaTemplate.send(productCreatedEventTopic, event);
    }

}
