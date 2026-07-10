package com.vendo.aws_service.application;

import com.vendo.aws_service.domain.file.PresignedFile;
import com.vendo.aws_service.domain.file.exception.DuplicateFileIdException;
import com.vendo.aws_service.domain.file.exception.InvalidFileTypeException;
import com.vendo.aws_service.domain.storage.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.storage.dto.PresignedBody;
import com.vendo.aws_service.port.file.FileValidationPort;
import com.vendo.aws_service.port.product.ProductImageEventSenderPort;
import com.vendo.aws_service.port.storage.PresignQueryPort;
import com.vendo.aws_service.port.storage.StorageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageService implements StorageUseCase {

    private final PresignQueryPort presignQueryPort;
    private final FileValidationPort fileValidationPort;
    private final ProductImageEventSenderPort productImageEventSenderPort;

    @Override
    public List<PresignedBody> presign(ContextType type, List<File> files) {
        validateAllFiles(files);
        Map<String, PresignedBody> bodiesById = presignAll(type, files);
        sendEvents(toPresignedFiles(files, bodiesById));
        return List.copyOf(bodiesById.values());
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

    private Map<String, PresignedBody> presignAll(ContextType type, List<File> files) {
        return files.stream()
                .map(file -> presignQueryPort.presign(type, file))
                .collect(Collectors.toMap(PresignedBody::id, pb -> pb));
    }

    private List<PresignedFile> toPresignedFiles(List<File> files, Map<String, PresignedBody> bodies) {
        return files.stream()
                .map(file -> PresignedFile.of(findBodyById(file.id(), bodies).key(), file.size(), file.contentType()))
                .toList();
    }

    private PresignedBody findBodyById(String id, Map<String, PresignedBody> bodies) {
        PresignedBody presignedBody = bodies.get(id);
        if (presignedBody == null) throw new IllegalStateException("Body not found by id %s.".formatted(id));
        return presignedBody;
    }

    private void sendEvents(List<PresignedFile> files) {
        files.forEach(productImageEventSenderPort::send);
    }
}
