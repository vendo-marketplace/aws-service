package com.vendo.aws_service.adapter.in.messaging.kafka.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.aws_service.adapter.common.exceptions.AwsEventDeserializationException;
import com.vendo.aws_service.adapter.in.messaging.kafka.handler.AwsEventHandler;
import com.vendo.aws_service.application.usecase.UploadFileUseCase;
import com.vendo.aws_service.domain.model.FileUploadCommand;
import com.vendo.aws_service.domain.type.AwsEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3UploadEventHandler implements AwsEventHandler {

    private final UploadFileUseCase uploadFileUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public boolean canHandle(AwsEventType type) {
        return type == AwsEventType.S3_UPLOAD_FILE;
    }

    @Override
    public void handle(JsonNode payload) {
        try {
            FileUploadCommand command = objectMapper.treeToValue(payload, FileUploadCommand.class);

            uploadFileUseCase.uploadFile(command);

        } catch (JsonProcessingException e) {
            log.error("JSON parsing error for S3_UPLOAD_FILE", e);
            throw new AwsEventDeserializationException("Invalid JSON format for file upload");
        }
    }
}
