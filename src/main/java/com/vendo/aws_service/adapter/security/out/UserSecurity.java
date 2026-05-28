package com.vendo.aws_service.adapter.security.out;

import com.vendo.aws_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.type.UserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    public void validateActivation(Authentication auth) {
        TokenClaims claims = (TokenClaims) auth.getPrincipal();

        if (claims.status() == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is blocked.");
        }

        if (!claims.emailVerified()) {
            throw new UserEmailNotVerifiedException("User email is not verified.");
        }

    }
}
