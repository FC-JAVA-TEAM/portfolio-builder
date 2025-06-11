package com.flexi.profile.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class RoleRequestDTO {
    private Long id;
    private Long roleId;
    private Long userId;
    private String status;
    private String justification;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private Long processedBy;
}
