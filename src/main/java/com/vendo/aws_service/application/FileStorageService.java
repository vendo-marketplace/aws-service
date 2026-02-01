package com.vendo.aws_service.application;

import com.vendo.aws_service.application.usecase.UploadFileUseCase;
import com.vendo.aws_service.domain.model.FileUploadCommand;
import com.vendo.aws_service.port.aws.StoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService implements UploadFileUseCase {

    private final StoragePort storagePort;

    @Override
    public void uploadFile(FileUploadCommand command) {
        log.info("Starting upload process for file: {}", command.fileName());

        storagePort.upload(command.fileName(), command.content(), command.contentType());

        log.info("Upload process completed for file: {}", command.fileName());
    }
}
