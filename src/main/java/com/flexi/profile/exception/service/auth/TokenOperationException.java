package com.flexi.profile.exception.service.auth;

public class TokenOperationException extends RuntimeException {
    public TokenOperationException(String message) {
        super(message);
    }

    public TokenOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
