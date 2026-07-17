package com.vendo.aws_service.application;

import com.vendo.aws_service.domain.file.exception.DuplicateFileIdException;
import com.vendo.aws_service.domain.file.exception.InvalidFileTypeException;
import com.vendo.aws_service.domain.presign.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.presign.dto.PresignBody;
import com.vendo.aws_service.port.file.FileValidationPort;
import com.vendo.aws_service.port.presign.PresignQueryPort;
import com.vendo.aws_service.port.presign.PresignUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PresignService implements PresignUseCase {

    private final PresignQueryPort presignQueryPort;
    private final FileValidationPort fileValidationPort;

    @Override
    public List<PresignBody> presign(ContextType type, List<File> files) {
        validateAllFiles(files);
        return presignAll(type, files);
    }

    private void validateAllFiles(List<File> files) {
        Set<String> ids = new HashSet<>();

        for (File file : files) {
            throwIfInvalidImageType(file.contentType());

            if (!ids.add(file.id())) {
                throw new DuplicateFileIdException("File ids must be unique.");
            }
        }
    }

    private void throwIfInvalidImageType(String contentType) {
        if (!fileValidationPort.isImage(contentType)) {
            throw new InvalidFileTypeException("Invalid file type of image: %s.".formatted(contentType));
        }
    }

    private List<PresignBody> presignAll(ContextType type, List<File> files) {
        return files.stream()
                .map(file -> presignQueryPort.presign(type, file))
                .toList();
    }
}
