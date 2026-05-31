package com.vendo.aws_service.application;

import com.vendo.aws_service.domain.file.exception.DuplicateFileIdException;
import com.vendo.aws_service.domain.file.exception.InvalidFileTypeException;
import com.vendo.aws_service.domain.storage.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.storage.dto.PresignedBody;
import com.vendo.aws_service.domain.user.User;
import com.vendo.aws_service.port.auth.AuthenticationService;
import com.vendo.aws_service.port.file.FileValidationPort;
import com.vendo.aws_service.port.storage.PresignQueryPort;
import com.vendo.aws_service.port.storage.StorageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageService implements StorageUseCase {

    private final PresignQueryPort presignQueryPort;
    private final FileValidationPort fileValidationPort;
    private final AuthenticationService authenticationService;

    @Override
    public List<PresignedBody> presign(ContextType type, List<File> files) {
        User authUser = authenticationService.getAuthUser();
        authUser.throwIfEmailNotVerified();

        validateFiles(files);
        return files.stream()
                .map(file -> presignQueryPort.presign(type, file))
                .toList();
    }

    private void validateFiles(List<File> files) {
        Set<String> ids = files.stream()
                .peek(file -> throwIfInvalidImageType(file.contentType()))
                .map(File::id)
                .collect(Collectors.toSet());

        if (ids.size() != files.size()) {
            throw new DuplicateFileIdException("File ids must be unique.");
        }
    }

    private void throwIfInvalidImageType(String contentType) {
        if (!fileValidationPort.isImage(contentType)) {
            throw new InvalidFileTypeException("Invalid file type of image: %s.".formatted(contentType));
        }
    }
}
