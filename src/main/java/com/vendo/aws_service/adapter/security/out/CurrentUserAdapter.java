package com.vendo.aws_service.adapter.security.out;

import com.vendo.aws_service.port.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserAdapter implements CurrentUserPort {

    private final SecurityContextHelper securityContextHelper;

    @Override
    public String getCurrentUserId() {
        return securityContextHelper.getCurrentUser().id();
    }

}
