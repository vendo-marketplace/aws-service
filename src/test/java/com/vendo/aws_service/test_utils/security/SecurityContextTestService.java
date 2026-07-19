package com.vendo.aws_service.test_utils.security;

import com.vendo.aws_service.domain.user.User;
import com.vendo.security_lib.type.AuthHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static com.vendo.core_lib.constants.Delimiters.COMMA_DELIMITER;

public class SecurityContextTestService {

    public static Authentication initializeEmptyAuth() {
        return new UsernamePasswordAuthenticationToken(null,  null);
    }

    public static HttpHeaders extractHeaders(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(AuthHeader.ID.getHeader(), user.id());
        httpHeaders.add(AuthHeader.EMAIL.getHeader(), user.email());
        httpHeaders.add(AuthHeader.ROLES.getHeader(), String.join(COMMA_DELIMITER, user.toRoleNames()));
        httpHeaders.add(AuthHeader.EMAIL_VERIFIED.getHeader(), String.valueOf(user.emailVerified()));
        httpHeaders.add(AuthHeader.STATUS.getHeader(), String.valueOf(user.status()));

        return httpHeaders;
    }
}
