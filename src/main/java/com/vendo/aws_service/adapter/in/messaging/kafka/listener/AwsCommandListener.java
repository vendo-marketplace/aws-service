package com.vendo.aws_service.adapter.in.messaging.kafka.listener;

import com.vendo.aws_service.adapter.in.messaging.kafka.dto.AwsEventEnvelope;
import com.vendo.aws_service.adapter.in.messaging.kafka.handler.AwsEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsCommandListener {

    private final List<AwsEventHandler> handlers;

    @KafkaListener(
            topics = "${kafka.events.aws-command-event.topic}",
            groupId = "${kafka.events.aws-command-event.groupId}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeEvent(AwsEventEnvelope event) {
        log.info("Kafka Dispatcher: Received event of type {}", event.type());

        handlers.stream()
                .filter(handler -> handler.canHandle(event.type()))
                .findFirst()
                .ifPresentOrElse(
                        handler -> handler.handle(event.payload()),
                        () -> log.warn("No handler found for event type: {}", event.type())
                );
    }
}