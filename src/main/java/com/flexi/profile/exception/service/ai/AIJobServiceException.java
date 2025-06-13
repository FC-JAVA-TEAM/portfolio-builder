package com.flexi.profile.exception.service.ai;

import com.flexi.profile.exception.service.common.ServiceException;

/**
 * Custom exception for AI Job Service operations.
 * Thrown when there are issues with AI job search functionality.
 * 
 * @author AI Job Service
 * @version 1.0
 */
public class AIJobServiceException extends ServiceException {

    /**
     * Constructs a new AIJobServiceException with the specified detail message.
     * 
     * @param message the detail message
     */
    public AIJobServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new AIJobServiceException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AIJobServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new AIJobServiceException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public AIJobServiceException(Throwable cause) {
        super("AI job service error occurred", cause);
    }
}
