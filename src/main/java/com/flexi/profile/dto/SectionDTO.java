package com.flexi.profile.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SectionDTO {
    private Long id;
    private Long profileId;
    private String type;
    private String title;
    private Integer order;
    private String backgroundUrl;
    private List<SubSectionDTO> subsections;
}
