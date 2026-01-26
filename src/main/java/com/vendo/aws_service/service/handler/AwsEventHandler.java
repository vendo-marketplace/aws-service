package com.vendo.aws_service.service.handler;


import com.fasterxml.jackson.databind.JsonNode;
import com.vendo.aws_service.service.type.AwsEventType;


public interface AwsEventHandler {

    boolean canHandle(AwsEventType type);

    void handle(JsonNode payload);
}
