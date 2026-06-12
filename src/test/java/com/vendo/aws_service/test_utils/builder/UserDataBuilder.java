package com.vendo.aws_service.test_utils.builder;

import com.vendo.aws_service.domain.user.User;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;

import java.util.Set;

public final class UserDataBuilder {

    public static User.UserBuilder withAllFields() {
        return User.builder()
                .id("id")
                .email("email")
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .roles(Set.of(UserRole.USER));
    }

}
