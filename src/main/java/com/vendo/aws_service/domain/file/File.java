package com.vendo.aws_service.domain.file;

public record File(

        String id,
        Long size,
        String contentType

) {
}
