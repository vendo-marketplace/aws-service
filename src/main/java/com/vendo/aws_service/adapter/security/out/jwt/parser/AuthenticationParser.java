package com.vendo.aws_service.adapter.security.out.jwt.parser;

import com.vendo.aws_service.domain.user.User;

public interface AuthenticationParser {

    User extract(String token);

}
