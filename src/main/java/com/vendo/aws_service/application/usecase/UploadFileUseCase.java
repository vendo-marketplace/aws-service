package com.vendo.aws_service.application.usecase;

import com.vendo.aws_service.domain.model.FileUploadCommand;

public interface UploadFileUseCase {
    void uploadFile(FileUploadCommand command);
}
