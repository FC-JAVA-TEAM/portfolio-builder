package com.flexi.profile.exception.base;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String code;
    private final transient Object[] args;

    protected BaseException(String message) {
        super(message);
        this.code = null;
        this.args = null;
    }

    protected BaseException(String message, String code) {
        super(message);
        this.code = code;
        this.args = null;
    }

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
        this.args = null;
    }

    protected BaseException(String message, String code, Object... args) {
        super(message);
        this.code = code;
        this.args = args;
    }

    protected BaseException(String message, Throwable cause, String code, Object... args) {
        super(message, cause);
        this.code = code;
        this.args = args;
    }

    /**
     * Gets a copy of the exception arguments.
     * @return A new array containing the exception arguments, or null if no arguments exist
     */
    public Object[] getArgs() {
        return args == null ? null : args.clone();
    }

    /**
     * Gets the error code associated with this exception.
     * @return The error code, or null if no code was specified
     */
    public String getCode() {
        return code;
    }

    /**
     * Creates a formatted message using the base message and any provided arguments.
     * @return The formatted message
     */
    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        if (args == null || args.length == 0) {
            return baseMessage;
        }
        try {
            return String.format(baseMessage, args);
        } catch (Exception e) {
            return baseMessage;
        }
    }
}
