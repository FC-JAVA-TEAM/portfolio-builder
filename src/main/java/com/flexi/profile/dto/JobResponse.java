package com.flexi.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object representing a job response from the Fuelix AI API.
 * Contains all job-related information including company details, requirements,
 * and application links.
 * 
 * @author AI Job Service
 * @version 1.0
 */
public class JobResponse {

    /**
     * Unique identifier for the job posting
     */
    @NotBlank(message = "Job ID cannot be blank")
    private String id;

    /**
     * Date when the job was posted
     */
    @JsonProperty("date_posted")
    private String datePosted;

    /**
     * Date when the job record was created
     */
    @JsonProperty("date_created")
    private String dateCreated;

    /**
     * Date until which the job posting is valid
     */
    @JsonProperty("date_validthrough")
    private String dateValidThrough;

    /**
     * Title of the job position
     */
    @NotBlank(message = "Job title cannot be blank")
    @JsonProperty("jobTitle")
    private String jobTitle;

    /**
     * Raw salary information as provided by the source
     */
    @JsonProperty("salary_raw")
    private String salaryRaw;

    /**
     * Company name offering the job
     */
    @NotBlank(message = "Company name cannot be blank")
    private String company;

    /**
     * Experience required for the job
     */
    @JsonProperty("experienceRequired")
    private String experienceRequired;

    /**
     * Derived location information for the job
     */
    @JsonProperty("locations_derived")
    private String locationsDerived;

    /**
     * List of skills required for the job
     */
    @NotNull(message = "Skills list cannot be null")
    private List<String> skills;

    /**
     * Type of employment (Full-time, Part-time, Contract, etc.)
     */
    @JsonProperty("employment_type")
    private String employmentType;

    /**
     * Type of source where the job was found
     */
    @JsonProperty("source_type")
    private String sourceType;

    /**
     * Time when the job was posted (relative format)
     */
    @JsonProperty("postedTime")
    private String postedTime;

    /**
     * Source platform where the job was found
     */
    private String source;

    /**
     * Save status of the job (Saved, Not Saved, etc.)
     */
    @JsonProperty("saveStatus")
    private String saveStatus;

    /**
     * Direct link to the job posting
     */
    @JsonProperty("jobLink")
    private String jobLink;

    /**
     * LinkedIn organization description
     */
    @JsonProperty("linkedin_org_description")
    private String linkedinOrgDescription;

    /**
     * Default constructor
     */
    public JobResponse() {
    }

    /**
     * Constructor with essential fields
     * 
     * @param id the job ID
     * @param jobTitle the job title
     * @param company the company name
     * @param skills the required skills
     */
    public JobResponse(String id, String jobTitle, String company, List<String> skills) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.company = company;
        this.skills = skills;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateValidThrough() {
        return dateValidThrough;
    }

    public void setDateValidThrough(String dateValidThrough) {
        this.dateValidThrough = dateValidThrough;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSalaryRaw() {
        return salaryRaw;
    }

    public void setSalaryRaw(String salaryRaw) {
        this.salaryRaw = salaryRaw;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getExperienceRequired() {
        return experienceRequired;
    }

    public void setExperienceRequired(String experienceRequired) {
        this.experienceRequired = experienceRequired;
    }

    public String getLocationsDerived() {
        return locationsDerived;
    }

    public void setLocationsDerived(String locationsDerived) {
        this.locationsDerived = locationsDerived;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(String postedTime) {
        this.postedTime = postedTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(String saveStatus) {
        this.saveStatus = saveStatus;
    }

    public String getJobLink() {
        return jobLink;
    }

    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getLinkedinOrgDescription() {
        return linkedinOrgDescription;
    }

    public void setLinkedinOrgDescription(String linkedinOrgDescription) {
        this.linkedinOrgDescription = linkedinOrgDescription;
    }

    @Override
    public String toString() {
        return "JobResponse{" +
                "id='" + id + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", company='" + company + '\'' +
                ", locationsDerived='" + locationsDerived + '\'' +
                ", experienceRequired='" + experienceRequired + '\'' +
                ", skills=" + skills +
                ", employmentType='" + employmentType + '\'' +
                ", salaryRaw='" + salaryRaw + '\'' +
                '}';
    }
}
