package com.vendo.aws_service.adapter.presign.in.dto;

import com.vendo.aws_service.domain.presign.dto.PresignBody;

import java.util.List;

public record PresignResponse(
        List<PresignBody> data
) {

    public static PresignResponse of(List<PresignBody> files) {
        return new PresignResponse(files);
    }

}
