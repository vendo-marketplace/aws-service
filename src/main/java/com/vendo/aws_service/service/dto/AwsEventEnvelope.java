package com.vendo.aws_service.service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.vendo.aws_service.service.type.AwsEventType;

public record AwsEventEnvelope(
        AwsEventType type,
        JsonNode payload
) {
}
