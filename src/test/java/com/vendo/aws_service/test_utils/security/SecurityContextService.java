package com.vendo.aws_service.test_utils.security;

import com.vendo.aws_service.domain.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class SecurityContextService {

    public static Authentication initializeAuth(User authUser) {
        List<SimpleGrantedAuthority> authorities = authUser.roles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();

        return new UsernamePasswordAuthenticationToken(
                authUser,
                null,
                authorities);
    }
}
