package com.flexi.profile.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private String title;
    private String organization;
    private String description;
    private List<String> skills;
    private String locationsDerived;
    private String type;
    private LocalDate datePosted;
    private LocalDateTime dateCreated;
    private String source;
    private String sourceDomain;
    private Integer rating;
    private String status;
    private String job_link;
    private Boolean applied;

    public ErrorResponse(int httpStatus, String message) {
        this.title = "N/A";
        this.organization = "N/A";
        this.description = message;
        this.skills = List.of("N/A");
        this.locationsDerived = "N/A";
        this.type = "N/A";
        this.datePosted = LocalDate.now();
        this.dateCreated = LocalDateTime.now();
        this.source = "Internal";
        this.sourceDomain = "example.com";
        this.rating = 0;
        this.status = HttpStatus.valueOf(httpStatus).name();
        this.job_link = "N/A";
        this.applied = false;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    
    public String getLocationsDerived() { return locationsDerived; }
    public void setLocationsDerived(String locationsDerived) { this.locationsDerived = locationsDerived; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public LocalDate getDatePosted() { return datePosted; }
    public void setDatePosted(LocalDate datePosted) { this.datePosted = datePosted; }
    
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getSourceDomain() { return sourceDomain; }
    public void setSourceDomain(String sourceDomain) { this.sourceDomain = sourceDomain; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getJob_link() { return job_link; }
    public void setJob_link(String job_link) { this.job_link = job_link; }
    
    public Boolean getApplied() { return applied; }
    public void setApplied(Boolean applied) { this.applied = applied; }
}
