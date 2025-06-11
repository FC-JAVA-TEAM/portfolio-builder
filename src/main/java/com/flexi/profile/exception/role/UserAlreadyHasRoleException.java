package com.flexi.profile.exception.role;

import com.flexi.profile.exception.UnauthorizedException;

public class UserAlreadyHasRoleException extends UnauthorizedException {
    public UserAlreadyHasRoleException(String message) {
        super(message);
    }

    public UserAlreadyHasRoleException(String userEmail, String roleName) {
        super(String.format("User %s already has the role: %s", userEmail, roleName));
    }
}
