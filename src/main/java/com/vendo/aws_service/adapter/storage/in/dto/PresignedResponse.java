package com.vendo.aws_service.adapter.storage.in.dto;

import com.vendo.aws_service.domain.storage.dto.PresignedBody;

import java.util.List;

public record PresignedResponse(
        List<PresignedBody> files
) {

    public static PresignedResponse of(List<PresignedBody> files) {
        return new PresignedResponse(files);
    }

}
