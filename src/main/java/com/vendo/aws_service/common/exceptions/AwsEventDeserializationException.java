package com.vendo.aws_service.common.exceptions;

public class AwsEventDeserializationException extends RuntimeException {
    public AwsEventDeserializationException(String message) {
        super(message);
    }
}
