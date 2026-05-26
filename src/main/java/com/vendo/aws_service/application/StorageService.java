package com.vendo.aws_service.application;

import com.vendo.aws_service.domain.file.exception.FileSizeExceededException;
import com.vendo.aws_service.domain.file.exception.InvalidFileTypeException;
import com.vendo.aws_service.domain.storage.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.storage.dto.PresignedBody;
import com.vendo.aws_service.port.file.FileFormatValidationPort;
import com.vendo.aws_service.port.storage.PresignQueryPort;
import com.vendo.aws_service.port.storage.StorageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService implements StorageUseCase {

    private final long MAX_FILE_SIZE;

    private final PresignQueryPort presignQueryPort;
    private final FileFormatValidationPort fileFormatValidationPort;

    @Override
    public List<PresignedBody> presign(ContextType type, List<File> files) {
        files.forEach(this::validateFile);
        return files.stream()
                .map(file -> presignQueryPort.presign(type, file))
                .toList();
    }

    private void validateFile(File file) {
        if (file.size() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("Max size of 8MB reached for file: %s.".formatted(file.id()));
        }

        if (!fileFormatValidationPort.isImage(file.contentType())) {
            throw new InvalidFileTypeException("Invalid file type of image: %s.".formatted(file.contentType()));
        }
    }
}
