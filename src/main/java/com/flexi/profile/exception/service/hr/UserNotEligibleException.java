package com.flexi.profile.exception.service.hr;

import com.flexi.profile.exception.service.common.ServiceException;

public class UserNotEligibleException extends ServiceException {
    public UserNotEligibleException(String message) {
        super(message);
    }

    public UserNotEligibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
