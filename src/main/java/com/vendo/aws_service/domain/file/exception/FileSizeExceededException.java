package com.vendo.aws_service.domain.file.exception;

public class FileSizeExceededException extends RuntimeException {
  public FileSizeExceededException(String message) {
    super(message);
  }
}
