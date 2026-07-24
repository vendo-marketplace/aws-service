package com.vendo.aws_service.domain.file;

public record PresignFile(
        String key,
        long size,
        String contentType
) {

    public static PresignFile of(String key, Long size, String contentType) {
        return new PresignFile(key, size, contentType);
    }

}
