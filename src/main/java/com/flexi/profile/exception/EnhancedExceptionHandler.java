package com.flexi.profile.exception;

import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.response.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice(basePackages = "com.flexi.profile.controller")
public class EnhancedExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        if (!request.getDescription(false).startsWith("/api/v")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", ex.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("RESOURCE_NOT_FOUND")
            .message(ex.getMessage())
            .details("The requested resource does not exist")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Resource not found")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        if (!request.getDescription(false).startsWith("/api/v")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", ex.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("UNAUTHORIZED")
            .message(ex.getMessage())
            .details("You are not authorized to perform this operation")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Authorization failed")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        if (!request.getDescription(false).startsWith("/api/v")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.FORBIDDEN.value());
            response.put("message", "Access denied");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("ACCESS_DENIED")
            .message("Access denied")
            .details("You do not have permission to perform this operation")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Access denied")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(
            Exception ex, WebRequest request) {
        if (!request.getDescription(false).startsWith("/api/v")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "An unexpected error occurred");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("INTERNAL_SERVER_ERROR")
            .message(ex.getMessage())
            .details("An unexpected error occurred")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Operation failed")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
