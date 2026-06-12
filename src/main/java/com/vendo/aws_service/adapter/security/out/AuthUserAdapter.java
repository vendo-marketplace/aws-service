package com.vendo.aws_service.adapter.security.out;

import com.vendo.aws_service.domain.user.User;
import com.vendo.aws_service.port.user.AuthUserPort;
import com.vendo.security_starter.context.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserAdapter implements AuthUserPort {

    @Override
    public User getAuthUser() {
        return SecurityContextHelper.getAuthFromContext(User.class);
    }

}
