package com.vendo.aws_service.port.auth;

import com.vendo.aws_service.domain.user.User;

public interface AuthenticationService {

    User getAuthUser();

}
