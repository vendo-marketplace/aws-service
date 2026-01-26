package com.vendo.aws_service.integration.kafka.consumer;

import com.vendo.aws_service.integration.kafka.exceptions.AwsEventHandlerNotFoundException;
import com.vendo.aws_service.service.dto.AwsEventEnvelope;
import com.vendo.aws_service.service.handler.AwsEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsEventConsumer {
    private final List<AwsEventHandler> handlers;

    @KafkaListener(
            topics = "${kafka.events.aws-command-event.topic}",
            groupId = "${kafka.events.aws-command-event.groupId}",
            containerFactory = "${kafka.events.aws-command-event.container-factory}",
            properties = {"auto.offset.reset: ${kafka.events.aws-command-event.properties.auto-offset-reset}"}
    )
    public void consumeEvent(AwsEventEnvelope event) {
        log.info("Kafka Dispatcher: Event type received {}", event.type());

        handlers.stream()
                .filter(handler -> handler.canHandle(event.type()))
                .findFirst()
                .ifPresentOrElse(
                        handler -> handler.handle(event.payload()),
                        () -> {
                            throw new AwsEventHandlerNotFoundException("Kafka Dispatcher: Handler not found " + event.type());
                        });
    }
}
