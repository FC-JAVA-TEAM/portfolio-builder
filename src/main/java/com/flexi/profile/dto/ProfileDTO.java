package com.flexi.profile.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProfileDTO {
    private Long id;
    private String userId;
    private String userEmail;
    private String name;
    private String bio;
    private Boolean isPublic;
    private List<SectionDTO> sections;
}
