package com.flexi.profile.exception.service.section;

import com.flexi.profile.exception.service.common.ValidationException;
import java.util.Map;
import java.util.HashMap;

public class InvalidSectionOrderException extends ValidationException {
    private static final String DEFAULT_CODE = "INVALID_SECTION_ORDER";

    public InvalidSectionOrderException(Long sectionId, Integer currentOrder, Integer requestedOrder) {
        super(
            String.format("Invalid order %d requested for section %d (current order: %d)", 
                requestedOrder, sectionId, currentOrder),
            createErrorDetails(sectionId, currentOrder, requestedOrder)
        );
    }

    public InvalidSectionOrderException(String message) {
        super(message);
    }

    public InvalidSectionOrderException(String message, Map<String, String> errors) {
        super(message, errors);
    }

    public InvalidSectionOrderException(String message, Map<String, String> errors, Throwable cause) {
        super(message, errors, cause);
    }

    public InvalidSectionOrderException(String message, String code, Map<String, String> errors) {
        super(message, code, errors);
    }

    public InvalidSectionOrderException(String message, String code, Map<String, String> errors, Throwable cause) {
        super(message, code, errors, cause);
    }

    private static Map<String, String> createErrorDetails(Long sectionId, Integer currentOrder, Integer requestedOrder) {
        Map<String, String> errors = new HashMap<>();
        errors.put("sectionId", sectionId.toString());
        errors.put("currentOrder", currentOrder.toString());
        errors.put("requestedOrder", requestedOrder.toString());
        return errors;
    }
}
