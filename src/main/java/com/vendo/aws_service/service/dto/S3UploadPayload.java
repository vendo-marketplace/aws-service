package com.vendo.aws_service.service.dto;

public record S3UploadPayload(
        String fileName,
        byte[] content,
        String contentType
) {
}
