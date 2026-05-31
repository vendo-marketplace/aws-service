package com.vendo.aws_service.adapter.security.out;

import com.vendo.aws_service.domain.user.User;
import com.vendo.aws_service.port.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final AuthenticationService authenticationService;

    public boolean hasAccess() {

        try {
            User authUser = authenticationService.getAuthUser();
            authUser.throwIfBlocked();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
