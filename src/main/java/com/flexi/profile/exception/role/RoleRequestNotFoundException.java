package com.flexi.profile.exception.role;

import com.flexi.profile.exception.ResourceNotFoundException;

public class RoleRequestNotFoundException extends ResourceNotFoundException {
    public RoleRequestNotFoundException(String message) {
        super(message);
    }

    public RoleRequestNotFoundException(Long requestId) {
        super(String.format("Role request with ID %d not found", requestId));
    }
}
