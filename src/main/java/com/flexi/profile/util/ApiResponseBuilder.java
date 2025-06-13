package com.flexi.profile.util;

import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.response.ErrorDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Utility class for building standardized API responses.
 * Eliminates code duplication across controllers by providing
 * common response building methods.
 * 
 * @author API Response Builder
 * @version 1.0
 */
@Component
public class ApiResponseBuilder {

    /**
     * Creates a successful API response with data
     * 
     * @param message the success message
     * @param data the response data
     * @param <T> the type of data
     * @return standardized success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>success()
                .message(message)
                .data(data)
                .requestId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful API response without data
     * 
     * @param message the success message
     * @param <T> the type of data
     * @return standardized success response
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>success()
                .message(message)
                .requestId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error API response with message
     * 
     * @param message the error message
     * @param <T> the type of data
     * @return standardized error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>error()
                .message(message)
                .requestId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error API response with message and exception details
     * 
     * @param message the error message
     * @param exception the exception that occurred
     * @param <T> the type of data
     * @return standardized error response
     */
    public static <T> ApiResponse<T> error(String message, Exception exception) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getMessage())
                .details(exception.getClass().getSimpleName())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ApiResponse.<T>error()
                .message(message)
                .error(errorDetails)
                .requestId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error API response for validation failures
     * 
     * @param message the validation error message
     * @param <T> the type of data
     * @return standardized validation error response
     */
    public static <T> ApiResponse<T> validationError(String message) {
        return ApiResponse.<T>error()
                .message("Validation error: " + message)
                .requestId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error API response for service failures
     * 
     * @param message the service error message
     * @param <T> the type of data
     * @return standardized service error response
     */
    public static <T> ApiResponse<T> serviceError(String message) {
        return ApiResponse.<T>error()
                .message("Service error: " + message)
                .requestId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
