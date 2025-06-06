package com.flexi.profile.exception.service.auth;

import com.flexi.profile.exception.service.common.ServiceException;

public class UserAlreadyExistsException extends ServiceException {
    private static final String DEFAULT_CODE = "USER_ALREADY_EXISTS";
    private final String username;
    private final String email;

    public UserAlreadyExistsException(String username, String email) {
        super(String.format("User already exists with username '%s' or email '%s'", username, email), DEFAULT_CODE);
        this.username = username;
        this.email = email;
    }

    public UserAlreadyExistsException(String username, String email, String message) {
        super(message, DEFAULT_CODE);
        this.username = username;
        this.email = email;
    }

    public UserAlreadyExistsException( String email) {
        super(email, DEFAULT_CODE);
		this.username = "";
        this.email = email;
    }
    public UserAlreadyExistsException(String username, String email, String message, Throwable cause) {
        super(message, cause, DEFAULT_CODE);
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
