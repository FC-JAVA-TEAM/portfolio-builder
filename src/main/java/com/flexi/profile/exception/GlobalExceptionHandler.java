package com.flexi.profile.exception;

import com.flexi.profile.exception.profile.*;
import com.flexi.profile.exception.section.*;
import com.flexi.profile.exception.service.auth.*;
import com.flexi.profile.exception.service.common.*;
import com.flexi.profile.exception.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Authentication & Authorization Exceptions
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
        logger.error("Authentication error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Authentication failed: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access denied: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // Token Related Exceptions
    @ExceptionHandler({
        TokenExpiredException.class, 
        TokenValidationException.class, 
        RefreshTokenException.class,
        TokenOperationException.class
    })
    public ResponseEntity<ErrorResponse> handleTokenException(Exception ex) {
        logger.error("Token error: {}", ex.getMessage());
        HttpStatus status = ex instanceof TokenOperationException ? 
            HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.UNAUTHORIZED;
        ErrorResponse error = new ErrorResponse(status.value(), ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    // User Registration Exceptions
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User registration error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Not Found Exceptions
    @ExceptionHandler({ProfileNotFoundException.class, SectionNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex) {
        logger.error("Resource not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Validation Exceptions
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        logger.error("Validation error: {}", ex.getMessage());
        String message = ex instanceof MethodArgumentNotValidException ?
                ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(", ")) :
                ex.getMessage();
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed: " + message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Business Logic Exceptions
    @ExceptionHandler({BusinessLogicException.class, ServiceException.class})
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(Exception ex) {
        logger.error("Business logic error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Repository Exceptions
    @ExceptionHandler({DataAccessException.class, DataIntegrityException.class, DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleRepositoryException(Exception ex) {
        logger.error("Repository error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database operation failed: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Profile Specific Exceptions
    @ExceptionHandler({ProfileCreationException.class, ProfileUpdateException.class, ProfileAccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleProfileException(Exception ex) {
        logger.error("Profile operation error: {}", ex.getMessage());
        HttpStatus status = ex instanceof ProfileAccessDeniedException ? HttpStatus.FORBIDDEN : HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(status.value(), ex.getMessage());
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
        ErrorResponse error = new ErrorResponse(status.value(), ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    // Type Mismatch Exception
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Type mismatch error: {}", ex.getMessage());
        String error = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), error);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Generic Exception Handler (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please contact support if the problem persists."
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static class ErrorResponse {
        private final int status;
        private final String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
