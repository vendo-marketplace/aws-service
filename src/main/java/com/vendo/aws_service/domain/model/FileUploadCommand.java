package com.vendo.aws_service.domain.model;

public record FileUploadCommand(
        String fileName,
        byte[] content,
        String contentType
) {
}
