package com.vendo.aws_service.adapter.spring.in.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class SpringExceptionHandler {

    private final ValidationErrorConverter validationErrorConverter;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = validationErrorConverter.fromField(e.getBindingResult().getFieldErrors());

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(errors)
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    protected ResponseEntity<ExceptionResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e, HttpServletRequest request) {
        Map<String, String> errors = validationErrorConverter.fromParameter(e.getParameterValidationResults());

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        if (e.getCause() instanceof InvalidFormatException cause && cause.getTargetType() != null && cause.getTargetType().isEnum()) {
            String fieldName = cause.getPath().isEmpty() ? "field"
                    : cause.getPath().get(cause.getPath().size() - 1).getFieldName();

            String allowedValues = Arrays.stream(cause.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                    .message("Validation failed.")
                    .errors(Map.of(fieldName, "Allowed types are: " + allowedValues))
                    .code(HttpStatus.BAD_REQUEST.value())
                    .path(request.getRequestURI())
                    .build();

            return ResponseEntity.badRequest().body(exceptionResponse);
        }

        log.error(e.getMessage());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Invalid body structure.")
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    protected ResponseEntity<ExceptionResponse> handleCommonExceptions(Exception e, HttpServletRequest request) {
        log.error(e.getMessage());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Internal server error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(exceptionResponse);
    }
}
