package com.vendo.aws_service.adapter.security.in.filter.header;

import com.vendo.user_lib.type.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.vendo.core_lib.constants.Delimiters.COMMA_DELIMITER;
import static com.vendo.security_lib.type.UserHeaders.*;

@Slf4j
@Component
public class UserHeadersExtractor {

    public AuthenticatedUser extract(HttpServletRequest request) {
        return AuthenticatedUser.builder()
                .id(require(request, ID.getHeader()))
                .email(request.getHeader(EMAIL.getHeader()))
                .status(extractStatus(request.getHeader(STATUS.getHeader())))
                .roles(extractRoles(request.getHeader(ROLES.getHeader())))
                .emailVerified(Boolean.parseBoolean(request.getHeader(EMAIL_VERIFIED.getHeader())))
                .build();
    }

    private String require(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        if (value == null || value.isBlank()) {
            log.error("Required user header {} is missing.", header);
            throw new AuthenticationCredentialsNotFoundException("Unauthorized.");
        }
        return value;
    }

    private UserStatus extractStatus(String status) {
        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Invalid status header: {}.", status);
            throw new BadCredentialsException("Invalid user context.");
        }
    }

    private List<String> extractRoles(String roles) {
        if (roles == null || roles.isBlank()) return List.of();

        return Arrays.stream(roles.split(COMMA_DELIMITER))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .toList();
    }
}