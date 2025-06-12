package com.flexi.profile.exception.service.hr;

import com.flexi.profile.exception.service.common.ServiceException;

public class UserAlreadyRoleException extends ServiceException {
    public UserAlreadyRoleException(String message) {
        super(message);
    }

    public UserAlreadyRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
