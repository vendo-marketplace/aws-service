package com.vendo.aws_service.adapter.aws.in;

import com.vendo.aws_service.adapter.aws.out.exception.AwsException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AwsExceptionHandler {

    @ExceptionHandler(AwsException.class)
    public ResponseEntity<ExceptionResponse> handleAwsException(AwsException e, HttpServletRequest request) {
        log.error("Aws internal error occurred: {}.", e.getMessage());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Internal server error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
