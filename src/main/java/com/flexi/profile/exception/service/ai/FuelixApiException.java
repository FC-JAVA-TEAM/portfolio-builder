package com.flexi.profile.exception.service.ai;

/**
 * Custom exception for Fuelix API communication errors.
 * Thrown when there are issues communicating with the Fuelix AI API.
 * 
 * @author AI Job Service
 * @version 1.0
 */
public class FuelixApiException extends AIJobServiceException {

    /**
     * HTTP status code from the API response (if available)
     */
    private final Integer statusCode;

    /**
     * Constructs a new FuelixApiException with the specified detail message.
     * 
     * @param message the detail message
     */
    public FuelixApiException(String message) {
        super(message);
        this.statusCode = null;
    }

    /**
     * Constructs a new FuelixApiException with the specified detail message and status code.
     * 
     * @param message the detail message
     * @param statusCode the HTTP status code
     */
    public FuelixApiException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a new FuelixApiException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FuelixApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }

    /**
     * Constructs a new FuelixApiException with the specified detail message, cause, and status code.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     * @param statusCode the HTTP status code
     */
    public FuelixApiException(String message, Throwable cause, Integer statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     * 
     * @return the HTTP status code, or null if not available
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        String baseMessage = super.toString();
        if (statusCode != null) {
            return baseMessage + " [HTTP Status: " + statusCode + "]";
        }
        return baseMessage;
    }
}
