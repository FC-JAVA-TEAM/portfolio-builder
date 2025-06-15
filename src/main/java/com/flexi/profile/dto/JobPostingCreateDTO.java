package com.flexi.profile.dto;

import java.util.Set;

public class JobPostingCreateDTO {
    private String title;
    private String description;
    private String organization;
    private String locationsDerived;
    private String type;
    private Set<String> skills;
    private String sourceDomain;

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationsDerived() {
        return locationsDerived;
    }

    public void setLocationsDerived(String locationsDerived) {
        this.locationsDerived = locationsDerived;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSourceDomain() {
        return sourceDomain;
    }

    public void setSourceDomain(String sourceDomain) {
        this.sourceDomain = sourceDomain;
    }
}
