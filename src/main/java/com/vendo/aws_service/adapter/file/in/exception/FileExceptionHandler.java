package com.vendo.aws_service.adapter.file.in.exception;

import com.vendo.aws_service.domain.file.exception.FileSizeExceededException;
import com.vendo.aws_service.domain.file.exception.InvalidFileTypeException;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler({InvalidFileTypeException.class, FileSizeExceededException.class})
    public ResponseEntity<ExceptionResponse> handleFileException(Exception e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }


}
