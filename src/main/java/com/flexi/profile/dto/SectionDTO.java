package com.flexi.profile.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SectionDTO {
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public void setSubSections(List<SubSectionDTO> subSections) {
		this.subSections = subSections;
	}

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
