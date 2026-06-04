package com.vendo.aws_service.adapter.security.out;

import com.vendo.aws_service.domain.user.User;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final SecurityContextHelper securityContextHelper;

    public boolean validateActivation() {
        User user = securityContextHelper.getCurrentUser();

        if (!user.emailVerified()) {
            throw new UserEmailNotVerifiedException("User email is not verified.");
        }

        return true;
    }
}