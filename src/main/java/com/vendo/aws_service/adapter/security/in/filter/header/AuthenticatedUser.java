package com.vendo.aws_service.adapter.security.in.filter.header;

import com.vendo.user_lib.type.UserStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record AuthenticatedUser(
        String id,
        String email,
        UserStatus status,
        List<String> roles,
        boolean emailVerified
) {
}