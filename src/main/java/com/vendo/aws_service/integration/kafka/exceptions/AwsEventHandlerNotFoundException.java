package com.vendo.aws_service.integration.kafka.exceptions;

public class AwsEventHandlerNotFoundException extends RuntimeException {
    public AwsEventHandlerNotFoundException(String message) {
        super(message);
    }
}