package com.flexi.profile.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDetails {
    private String code;
    private String message;
    private String details;
    private LocalDateTime timestamp;
}
