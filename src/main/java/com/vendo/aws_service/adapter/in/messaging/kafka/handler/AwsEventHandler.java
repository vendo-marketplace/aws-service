package com.vendo.aws_service.adapter.in.messaging.kafka.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.vendo.aws_service.domain.type.AwsEventType;

public interface AwsEventHandler {
    boolean canHandle(AwsEventType type);

    void handle(JsonNode payload);
}
