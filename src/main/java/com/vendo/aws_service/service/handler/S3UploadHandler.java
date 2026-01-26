package com.vendo.aws_service.service.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.aws_service.common.exceptions.AwsEventDeserializationException;
import com.vendo.aws_service.service.S3Service;
import com.vendo.aws_service.service.dto.S3UploadPayload;
import com.vendo.aws_service.service.type.AwsEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadHandler implements AwsEventHandler {

    private final S3Service s3Service;

    private final ObjectMapper objectMapper;

    @Override
    public boolean canHandle(AwsEventType type) {
        return type == AwsEventType.S3_UPLOAD_FILE;
    }

    @Override
    public void handle(JsonNode payload) {
        S3UploadPayload uploadPayload;
        try {
            uploadPayload = objectMapper.treeToValue(payload, S3UploadPayload.class);
        } catch (JsonProcessingException e) {
            log.error("Unable to parse JSON for S3UploadPayload", e);
            throw new AwsEventDeserializationException("Invalid JSON format");
        }
        s3Service.uploadFile(uploadPayload.fileName(), uploadPayload.content());
    }
}
