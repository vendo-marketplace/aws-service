package com.vendo.aws_service.domain.file;

public record PresignedFile(
        String key,
        long size,
        String contentType
) {

    public static PresignedFile of(String key, Long size, String contentType) {
        return new PresignedFile(key, size, contentType);
    }

}
