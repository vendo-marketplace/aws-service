package com.vendo.aws_service.domain.presign.dto;

public record PresignBody(
        String id,
        String uploadUrl,
        String key
) {

}
