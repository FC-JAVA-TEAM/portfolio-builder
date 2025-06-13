package com.flexi.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for Google Calendar meeting request.
 * Contains dynamic parameters for scheduling Google Meet interviews.
 * 
 * @author AI Calendar Service
 * @version 1.0
 */
public class CalendarMeetingRequest {

    /**
     * Date and time for the meeting (e.g., "June 14, 2025, at 11:00 AM IST")
     */
    @NotBlank(message = "Date and time cannot be blank")
    @Size(max = 100, message = "Date and time must not exceed 100 characters")
    private String dateTime;

    /**
     * Job ID for the interview (e.g., "Job ID 1045")
     */
    @NotBlank(message = "Job ID cannot be blank")
    @Size(max = 50, message = "Job ID must not exceed 50 characters")
    private String jobId;

    /**
     * Type of interview (e.g., "Senior Backend Developer Interview")
     */
    @NotBlank(message = "Interview type cannot be blank")
    @Size(max = 200, message = "Interview type must not exceed 200 characters")
    private String interviewType;

    /**
     * Duration of the meeting (e.g., "30 minutes")
     */
    @NotBlank(message = "Duration cannot be blank")
    @Size(max = 50, message = "Duration must not exceed 50 characters")
    private String duration;

    /**
     * Description of the meeting (e.g., "Discussion with shortlisted candidates")
     */
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Default constructor
     */
    public CalendarMeetingRequest() {
    }

    /**
     * Constructor with all fields
     * 
     * @param dateTime the meeting date and time
     * @param jobId the job identifier
     * @param interviewType the type of interview
     * @param duration the meeting duration
     * @param description the meeting description
     */
    public CalendarMeetingRequest(String dateTime, String jobId, String interviewType, 
                                String duration, String description) {
        this.dateTime = dateTime;
        this.jobId = jobId;
        this.interviewType = interviewType;
        this.duration = duration;
        this.description = description;
    }

    // Getters and Setters

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CalendarMeetingRequest{" +
                "dateTime='" + dateTime + '\'' +
                ", jobId='" + jobId + '\'' +
                ", interviewType='" + interviewType + '\'' +
                ", duration='" + duration + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
