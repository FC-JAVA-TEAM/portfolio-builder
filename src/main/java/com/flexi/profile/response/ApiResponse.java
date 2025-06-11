package com.flexi.profile.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private ErrorDetails error;
    private String requestId;
    private LocalDateTime timestamp;

    public static <T> ApiResponseBuilder<T> success() {
        return ApiResponse.<T>builder().status("success").error(null);
    }

    public static <T> ApiResponseBuilder<T> error() {
        return ApiResponse.<T>builder().status("error").data(null);
    }
}
