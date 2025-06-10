package com.flexi.profile.util;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;
import java.util.function.Supplier;

public class LogUtil {
    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String METHOD = "method";
    private static final String DURATION = "duration";

    public static void setupMDC() {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            MDC.put(USER_ID, auth.getName());
        }
    }

    public static void clearMDC() {
        MDC.clear();
    }

    public static void setMethod(String methodName) {
        MDC.put(METHOD, methodName);
    }

    public static void logMethodEntry(Logger logger, String methodName, Object... args) {
        setMethod(methodName);
        if (args.length > 0) {
            logger.debug("Entering {} with arguments: {}", methodName, args);
        } else {
            logger.debug("Entering {}", methodName);
        }
    }

    public static void logMethodExit(Logger logger, String methodName) {
        logger.debug("Exiting {}", methodName);
        MDC.remove(METHOD);
    }

    public static void logMethodExit(Logger logger, String methodName, Object result) {
        logger.debug("Exiting {} with result: {}", methodName, result);
        MDC.remove(METHOD);
    }

    public static <T> T logMethodExecutionTime(Logger logger, String methodName, Supplier<T> operation) {
        long startTime = System.currentTimeMillis();
        try {
            T result = operation.get();
            long duration = System.currentTimeMillis() - startTime;
            MDC.put(DURATION, String.valueOf(duration));
            logger.info("Method {} completed in {} ms", methodName, duration);
            return result;
        } finally {
            MDC.remove(DURATION);
        }
    }

    public static void logError(Logger logger, String message, Throwable error) {
        logger.error("Error in {}: {} - {}", MDC.get(METHOD), message, error.getMessage(), error);
    }

    public static void logWarning(Logger logger, String message) {
        logger.warn("Warning in {}: {}", MDC.get(METHOD), message);
    }

    public static void logInfo(Logger logger, String message) {
        logger.info("{}: {}", MDC.get(METHOD), message);
    }

    public static void logDebug(Logger logger, String message) {
        logger.debug("{}: {}", MDC.get(METHOD), message);
    }

    public static void logTrace(Logger logger, String message) {
        logger.trace("{}: {}", MDC.get(METHOD), message);
    }
}
