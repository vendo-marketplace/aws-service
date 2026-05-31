package com.vendo.aws_service.adapter.security.out;

import com.vendo.aws_service.domain.user.User;
import com.vendo.aws_service.port.auth.AuthenticationService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextHelper implements AuthenticationService {

    @Override
    public User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User authUer)) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized.");
        }

        return authUer;
    }
}