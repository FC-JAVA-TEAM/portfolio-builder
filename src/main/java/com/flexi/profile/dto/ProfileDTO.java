package com.flexi.profile.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileDTO {
    private Long id;
    private String name;
    private String bio;
    private Boolean isPublic;
    private Long userId;
}
