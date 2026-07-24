package com.vendo.aws_service.adapter.presign.in.dto;

import jakarta.validation.constraints.NotNull;

public record FileRequest(

        @NotNull(message = "Id is required.")
        String id,

        @NotNull(message = "Content type is required.")
        String contentType

) {
}
