package com.vendo.aws_service.test_utils.security;

import com.vendo.aws_service.adapter.security.in.filter.header.AuthenticatedUser;
import com.vendo.user_lib.type.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class SecurityContextService {

    public static Authentication initializeAuth(AuthenticatedUser user) {
        String role = user.roles().get(0);
        if (role == null || role.isBlank()) role = UserRole.USER.name();

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
