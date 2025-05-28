package com.flexi.profile.exception;

import com.flexi.profile.exception.base.ErrorResponse;
import com.flexi.profile.exception.repository.*;
import com.flexi.profile.exception.service.auth.*;
import com.flexi.profile.exception.service.common.*;
import com.flexi.profile.exception.service.profile.*;
import com.flexi.profile.exception.service.section.*;
import org.springframework.dao.DataIntegrityViolationException;
import com.flexi.profile.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class, ProfileNotFoundException.class, SectionNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex) {
        LogUtil.logError(logger, "Resource not found", ex);
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ValidationException.class, InvalidSectionOrderException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        LogUtil.logError(logger, "Validation failed", ex);
        Map<String, Object> details = new HashMap<>();
        if (ex instanceof ValidationException) {
            details.put("errors", ((ValidationException) ex).getErrors());
        }
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), details);
    }

    @ExceptionHandler({BusinessLogicException.class, ProfileCreationException.class, ProfileUpdateException.class, SectionCreationException.class, SectionUpdateException.class})
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(Exception ex) {
        LogUtil.logError(logger, "Business logic violation", ex);
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({InvalidCredentialsException.class, TokenExpiredException.class, TokenValidationException.class, RefreshTokenException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
        LogUtil.logError(logger, "Authentication failed", ex);
        return createErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class, ProfileAccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception ex) {
        LogUtil.logError(logger, "Access denied", ex);
        return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        LogUtil.logError(logger, "Request validation failed", ex);
        Map<String, Object> details = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationEx = (MethodArgumentNotValidException) ex;
            Map<String, String> errors = new HashMap<>();
            validationEx.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            details.put("errors", errors);
        }
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        LogUtil.logError(logger, "Constraint violation", ex);
        Map<String, Object> details = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
            errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        details.put("errors", errors);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    @ExceptionHandler({DataIntegrityException.class, DataIntegrityViolationException.class, DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityException(Exception ex) {
        LogUtil.logError(logger, "Data integrity violation", ex);
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        LogUtil.logError(logger, "Data access error", ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while accessing the data");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        LogUtil.logError(logger, "User already exists", ex);
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        LogUtil.logError(logger, "Unexpected error occurred", ex);
        return createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later."
        );
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message) {
        return createErrorResponse(status, message, null);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message, Map<String, Object> details) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .details(details)
            .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}
