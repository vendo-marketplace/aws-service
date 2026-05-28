package com.vendo.aws_service.domain.storage.dto;

public record PresignedBody(
        String id,
        String uploadUrl,
        String key
) {

}
