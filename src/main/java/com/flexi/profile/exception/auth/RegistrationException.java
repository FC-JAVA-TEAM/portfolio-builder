package com.flexi.profile.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
    public RegistrationException(String email, String roleName) {
        super(String.format("Registration failed for user %s with role %s", email, roleName));
    }
}
