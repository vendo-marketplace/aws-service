package com.vendo.aws_service.domain.file.exception;

public class DuplicateFileIdException extends RuntimeException {
    public DuplicateFileIdException(String message) {
        super(message);
    }
}
