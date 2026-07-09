package com.vendo.aws_service.test_utils.security;

import com.vendo.aws_service.domain.user.User;
import com.vendo.user_lib.type.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public class SecurityContextService {

    public static Authentication initializeAuth(User user) {
        Set<UserRole> roles = user.roles();
        if (roles == null || roles.isEmpty()) roles = Set.of(UserRole.USER);

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
        );
    }
}
