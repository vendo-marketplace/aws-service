package com.vendo.aws_service.adapter.storage.in.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FileRequest(

        @NotNull(message = "Id is required.")
        String id,

        @Max(value = 1_048_576 * 8, message = "Maximum allowed size is 8MB.")
        @Min(value = 1, message = "Minimum allowed size cannot be less than 1.")
        Long size,

        @NotNull(message = "Content type is required.")
        String contentType

) {
}
