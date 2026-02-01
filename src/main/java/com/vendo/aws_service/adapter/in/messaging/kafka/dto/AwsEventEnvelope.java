package com.vendo.aws_service.adapter.in.messaging.kafka.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.vendo.aws_service.domain.type.AwsEventType;

public record AwsEventEnvelope(
        AwsEventType type,
        JsonNode payload
) {
}