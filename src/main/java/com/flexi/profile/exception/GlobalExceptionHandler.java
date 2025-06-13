package com.flexi.profile.exception;

import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.flexi.profile.exception.profile.ProfileNotFoundException;
import com.flexi.profile.exception.repository.DataAccessException;
import com.flexi.profile.exception.repository.DataIntegrityException;
import com.flexi.profile.exception.repository.DuplicateKeyException;
import com.flexi.profile.exception.service.auth.RefreshTokenException;
import com.flexi.profile.exception.service.auth.TokenExpiredException;
import com.flexi.profile.exception.service.auth.TokenOperationException;
import com.flexi.profile.exception.service.auth.TokenValidationException;
import com.flexi.profile.exception.service.auth.UserAlreadyExistsException;
import com.flexi.profile.exception.service.ai.AIJobServiceException;
import com.flexi.profile.exception.service.common.BusinessLogicException;
import com.flexi.profile.exception.service.common.ResourceNotFoundException;
import com.flexi.profile.exception.service.common.ServiceException;
import com.flexi.profile.exception.service.profile.ProfileAccessDeniedException;
import com.flexi.profile.exception.service.profile.ProfileCreationException;
import com.flexi.profile.exception.service.profile.ProfileUpdateException;
import com.flexi.profile.exception.service.section.InvalidSectionOrderException;
import com.flexi.profile.exception.service.section.InvalidSubSectionException;
import com.flexi.profile.exception.service.section.SectionCreationException;
import com.flexi.profile.exception.service.section.SectionUpdateException;
import com.flexi.profile.exception.service.section.SubSectionNotFoundException;
import com.flexi.profile.exception.base.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Authentication & Authorization Exceptions
    @ExceptionHandler({
        AuthenticationException.class,
        BadCredentialsException.class,
        UnauthorizedException.class,
        TokenExpiredException.class,
        TokenValidationException.class,
        RefreshTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
        logger.error("Authentication error: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Error")
                .message("Authentication failed: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("Access denied: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TokenOperationException.class)
    public ResponseEntity<ErrorResponse> handleTokenOperationException(TokenOperationException ex) {
        logger.error("Token operation error: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Token Operation Error")
                .message("Token operation failed: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Not Found Exceptions
    @ExceptionHandler({ProfileNotFoundException.class, SectionNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Validation Exceptions
    @ExceptionHandler({MethodArgumentNotValidException.class, jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        logger.error("Validation error: {}", ex.getMessage());
        String message = ex instanceof MethodArgumentNotValidException ?
                ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(", ")) :
                ex.getMessage();
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Validation failed: " + message)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // AI Service Exceptions
    @ExceptionHandler(AIJobServiceException.class)
    public ResponseEntity<ErrorResponse> handleAIJobServiceException(AIJobServiceException ex) {
        logger.error("AI service error: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("AI Service Error")
                .message("AI service error: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Business Logic Exceptions
    @ExceptionHandler({BusinessLogicException.class, ServiceException.class})
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(Exception ex) {
        logger.error("Business logic error: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Business Logic Error")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Repository Exceptions
    @ExceptionHandler({DataAccessException.class, DataIntegrityException.class, DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleRepositoryException(Exception ex) {
        logger.error("Repository error: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Database Error")
                .message("Database operation failed: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Profile Specific Exceptions
    @ExceptionHandler({ProfileCreationException.class, ProfileUpdateException.class, ProfileAccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleProfileException(Exception ex) {
        logger.error("Profile operation error: {}", ex.getMessage());
        HttpStatus status = ex instanceof ProfileAccessDeniedException ? HttpStatus.FORBIDDEN : HttpStatus.BAD_REQUEST;
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error("Profile Error")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, status);
    }

    // Section and SubSection Specific Exceptions
    @ExceptionHandler({
        SectionCreationException.class, 
        SectionUpdateException.class, 
        InvalidSectionOrderException.class,
        SubSectionNotFoundException.class,
        InvalidSubSectionException.class
    })
    public ResponseEntity<ErrorResponse> handleSectionException(Exception ex) {
        logger.error("Section/SubSection operation error: {}", ex.getMessage());
        HttpStatus status = ex instanceof SubSectionNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error("Section Error")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, status);
    }

    // Type Mismatch Exception
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Type mismatch error: {}", ex.getMessage());
        String errorMessage = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Type Mismatch")
                .message(errorMessage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Generic Exception Handler (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please contact support if the problem persists.")
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
