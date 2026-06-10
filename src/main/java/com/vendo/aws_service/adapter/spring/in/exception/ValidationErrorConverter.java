package com.vendo.aws_service.adapter.spring.in.exception;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidationErrorConverter {

    public Map<String, String> fromField(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> StringUtils.defaultIfEmpty(fieldError.getDefaultMessage(),
                                "No error message."))
                );
    }

    public Map<String, String> fromParameter(List<ParameterValidationResult> parameterErrors) {
        Map<String, String> errors = new TreeMap<>();

        for (ParameterValidationResult error : parameterErrors) {
            String parameterName = error.getMethodParameter().getParameterName();

            String message = error.getResolvableErrors()
                    .stream()
                    .findFirst()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .orElse("No error message.");

            errors.put(parameterName, message);
        }

        return errors;
    }
}
