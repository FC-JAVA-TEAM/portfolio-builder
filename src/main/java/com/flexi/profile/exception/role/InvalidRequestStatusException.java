package com.flexi.profile.exception.role;

import com.flexi.profile.exception.UnauthorizedException;
import com.flexi.profile.model.RoleRequest;

public class InvalidRequestStatusException extends UnauthorizedException {
    public InvalidRequestStatusException(String message) {
        super(message);
    }

    public InvalidRequestStatusException(RoleRequest.RequestStatus currentStatus, String expectedStatus) {
        super(String.format("Invalid request status. Expected: %s, Current: %s", expectedStatus, currentStatus));
    }
}
