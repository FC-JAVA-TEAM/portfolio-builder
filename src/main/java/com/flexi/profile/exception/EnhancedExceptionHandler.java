package com.flexi.profile.exception;

import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.response.ErrorDetails;
import com.flexi.profile.exception.role.RoleRequestNotFoundException;
import com.flexi.profile.exception.role.UserAlreadyHasRoleException;
import com.flexi.profile.exception.role.InvalidRequestStatusException;
import com.flexi.profile.exception.jobposting.*;
import com.flexi.profile.exception.jobapplication.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice(basePackages = "com.flexi.profile.controller")
public class EnhancedExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
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

    @ExceptionHandler(RoleRequestNotFoundException.class)
    public ResponseEntity<?> handleRoleRequestNotFoundException(
            RoleRequestNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("ROLE_REQUEST_NOT_FOUND")
            .message(ex.getMessage())
            .details("The requested role request does not exist")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Role request not found")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyHasRoleException.class)
    public ResponseEntity<?> handleUserAlreadyHasRoleException(
            UserAlreadyHasRoleException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("USER_ALREADY_HAS_ROLE")
            .message(ex.getMessage())
            .details("The user already has the requested role")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("User already has role")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidRequestStatusException.class)
    public ResponseEntity<?> handleInvalidRequestStatusException(
            InvalidRequestStatusException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("INVALID_REQUEST_STATUS")
            .message(ex.getMessage())
            .details("The role request is not in the correct status for this operation")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Invalid request status")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(JobPostingNotFoundException.class)
    public ResponseEntity<?> handleJobPostingNotFoundException(
            JobPostingNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("JOB_POSTING_NOT_FOUND")
            .message(ex.getMessage())
            .details("The requested job posting does not exist")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Job posting not found")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedJobPostingActionException.class)
    public ResponseEntity<?> handleUnauthorizedJobPostingActionException(
            UnauthorizedJobPostingActionException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("UNAUTHORIZED_JOB_POSTING_ACTION")
            .message(ex.getMessage())
            .details("You are not authorized to perform this operation on the job posting")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Unauthorized job posting action")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidJobPostingDataException.class)
    public ResponseEntity<?> handleInvalidJobPostingDataException(
            InvalidJobPostingDataException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("INVALID_JOB_POSTING_DATA")
            .message(ex.getMessage())
            .details("The provided job posting data is invalid")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Invalid job posting data")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("USER_NOT_FOUND")
            .message(ex.getMessage())
            .details("The requested user does not exist")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("User not found")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateJobPostingException.class)
    public ResponseEntity<?> handleDuplicateJobPostingException(
            DuplicateJobPostingException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("DUPLICATE_JOB_POSTING")
            .message(ex.getMessage())
            .details("A job posting with the same title already exists in this department")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Duplicate job posting")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(JobPostingStatusException.class)
    public ResponseEntity<?> handleJobPostingStatusException(
            JobPostingStatusException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("INVALID_JOB_POSTING_STATUS")
            .message(ex.getMessage())
            .details("The requested operation cannot be performed due to the job posting's current status")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Invalid job posting status")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(JobApplicationNotFoundException.class)
    public ResponseEntity<?> handleJobApplicationNotFoundException(
            JobApplicationNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("JOB_APPLICATION_NOT_FOUND")
            .message(ex.getMessage())
            .details("The requested job application does not exist")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Job application not found")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<?> handleDuplicateApplicationException(
            DuplicateApplicationException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("DUPLICATE_APPLICATION")
            .message(ex.getMessage())
            .details("A job application for this job posting already exists")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Duplicate job application")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidApplicationStatusTransitionException.class)
    public ResponseEntity<?> handleInvalidApplicationStatusTransitionException(
            InvalidApplicationStatusTransitionException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("INVALID_APPLICATION_STATUS_TRANSITION")
            .message(ex.getMessage())
            .details("The requested status transition is not allowed")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Invalid application status transition")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(ApplicationValidationException.class)
    public ResponseEntity<?> handleApplicationValidationException(
            ApplicationValidationException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("APPLICATION_VALIDATION_ERROR")
            .message(ex.getMessage())
            .details("The job application data is invalid")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Job application validation failed")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(ClosedJobPostingException.class)
    public ResponseEntity<?> handleClosedJobPostingException(
            ClosedJobPostingException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("CLOSED_JOB_POSTING")
            .message(ex.getMessage())
            .details("Cannot apply to a closed job posting")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Job posting is closed")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(UnauthorizedApplicationAccessException.class)
    public ResponseEntity<?> handleUnauthorizedApplicationAccessException(
            UnauthorizedApplicationAccessException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("UNAUTHORIZED_APPLICATION_ACCESS")
            .message(ex.getMessage())
            .details("User is not authorized to access this job application")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Unauthorized access to job application")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MaxApplicationsExceededException.class)
    public ResponseEntity<?> handleMaxApplicationsExceededException(
            MaxApplicationsExceededException ex, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
            .code("MAX_APPLICATIONS_EXCEEDED")
            .message(ex.getMessage())
            .details("User has exceeded the maximum number of allowed job applications")
            .timestamp(LocalDateTime.now())
            .build();

        ApiResponse<Void> response = ApiResponse.<Void>error()
            .message("Maximum applications limit exceeded")
            .error(errorDetails)
            .requestId(UUID.randomUUID().toString())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(
            Exception ex, WebRequest request) {
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
