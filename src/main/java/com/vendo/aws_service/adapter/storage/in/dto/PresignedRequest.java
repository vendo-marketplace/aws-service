package com.vendo.aws_service.adapter.storage.in.dto;

import com.vendo.aws_service.domain.storage.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PresignedRequest(
        @NotNull(message = "Type is required.")
        ContextType type,
        @NotEmpty(message = "At least 1 file is required.")
        List<File> files
) {
}
