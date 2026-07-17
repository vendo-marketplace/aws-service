package com.vendo.aws_service.adapter.presign.in.dto;

import com.vendo.aws_service.domain.presign.type.ContextType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PresignRequest(
        @NotNull(message = "Type is required.")
        ContextType type,
        @NotEmpty(message = "At least 1 file is required.")
        List<@Valid FileRequest> files
) {
}
