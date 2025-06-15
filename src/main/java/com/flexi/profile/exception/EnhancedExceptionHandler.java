package com.flexi.profile.exception;

import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.exception.role.RoleRequestNotFoundException;
import com.flexi.profile.exception.role.UserAlreadyHasRoleException;
import com.flexi.profile.exception.role.InvalidRequestStatusException;
import com.flexi.profile.exception.jobposting.*;
import com.flexi.profile.exception.auth.RegistrationException;
import com.flexi.profile.exception.jobapplication.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice(basePackages = "com.flexi.profile.controller")
public class EnhancedExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Resource not found")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Authorization failed")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access denied: " + ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Access denied")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(RoleRequestNotFoundException.class)
    public ResponseEntity<?> handleRoleRequestNotFoundException(
            RoleRequestNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Role request not found")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyHasRoleException.class)
    public ResponseEntity<?> handleUserAlreadyHasRoleException(
            UserAlreadyHasRoleException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("User already has role")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<?> handleUserAlreadyExistException(
            RegistrationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("User already available")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidRequestStatusException.class)
    public ResponseEntity<?> handleInvalidRequestStatusException(
            InvalidRequestStatusException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Invalid request status")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(JobPostingNotFoundException.class)
    public ResponseEntity<?> handleJobPostingNotFoundException(
            JobPostingNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Job posting not found")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedJobPostingActionException.class)
    public ResponseEntity<?> handleUnauthorizedJobPostingActionException(
            UnauthorizedJobPostingActionException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Unauthorized job posting action")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidJobPostingDataException.class)
    public ResponseEntity<?> handleInvalidJobPostingDataException(
            InvalidJobPostingDataException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Invalid job posting data")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("User not found")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateJobPostingException.class)
    public ResponseEntity<?> handleDuplicateJobPostingException(
            DuplicateJobPostingException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Duplicate job posting")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(JobPostingStatusException.class)
    public ResponseEntity<?> handleJobPostingStatusException(
            JobPostingStatusException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Invalid job posting status")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(JobApplicationNotFoundException.class)
    public ResponseEntity<?> handleJobApplicationNotFoundException(
            JobApplicationNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Job application not found")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<?> handleDuplicateApplicationException(
            DuplicateApplicationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Duplicate job application")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidApplicationStatusTransitionException.class)
    public ResponseEntity<?> handleInvalidApplicationStatusTransitionException(
            InvalidApplicationStatusTransitionException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Invalid application status transition")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(ApplicationValidationException.class)
    public ResponseEntity<?> handleApplicationValidationException(
            ApplicationValidationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Job application validation failed")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(ClosedJobPostingException.class)
    public ResponseEntity<?> handleClosedJobPostingException(
            ClosedJobPostingException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Job posting is closed")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(UnauthorizedApplicationAccessException.class)
    public ResponseEntity<?> handleUnauthorizedApplicationAccessException(
            UnauthorizedApplicationAccessException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Unauthorized access to job application")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MaxApplicationsExceededException.class)
    public ResponseEntity<?> handleMaxApplicationsExceededException(
            MaxApplicationsExceededException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getStatus().value(), ex.getMessage());
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Maximum applications limit exceeded")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed");
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>error()
            .message("Validation failed")
            .data(errors)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(
            Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please contact support if the problem persists."
        );
        ApiResponse<ErrorResponse> response = ApiResponse.<ErrorResponse>error()
            .message("Operation failed")
            .data(error)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
