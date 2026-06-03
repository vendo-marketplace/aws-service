package com.vendo.aws_service.adapter.security.out;

import com.vendo.aws_service.adapter.security.in.filter.header.AuthenticatedUser;
import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.type.UserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public boolean validateActivation(Authentication auth) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();

        if (user.status() == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is blocked.");
        }

        if (!user.emailVerified()) {
            throw new UserEmailNotVerifiedException("User email is not verified.");
        }

        return true;
    }
}
