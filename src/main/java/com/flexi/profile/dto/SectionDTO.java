package com.flexi.profile.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class SectionDTO {
    private Long id;
    private String title;
    private String content;
    private Long profileId;
    private Integer orderIndex;
    private List<SubSectionDTO> subSections = new ArrayList<>();

    public List<SubSectionDTO> getSubSections() {
        return subSections;
    }
}
