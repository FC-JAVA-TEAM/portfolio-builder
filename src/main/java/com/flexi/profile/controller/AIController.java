package com.flexi.profile.controller;

import com.flexi.profile.dto.CalendarApiResponse;
import com.flexi.profile.dto.CalendarMeetingRequest;
import com.flexi.profile.dto.JobResponse;
import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.service.AIJobService;
import com.flexi.profile.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for AI-powered functionality.
 * Provides endpoints for AI-based services including job search recommendations
 * through the Fuelix API integration.
 * 
 * @author AI Service
 * @version 1.0
 */
@RestController
@RequestMapping("/api/ai")
@Validated
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    @Autowired
    private AIJobService aiJobService;

    /**
     * Searches for jobs based on skills, location, and experience using AI recommendations.
     * 
     * Example: GET /api/ai/jobs?skills=Java&location=Hyderabad,Bangalore&experience=5+ years
     * 
     * @param skills the required skills for the job search (e.g., "Java, Spring Boot")
     * @param location the preferred job locations (e.g., "Hyderabad, Bangalore")
     * @param experience the required experience level (e.g., "5+ years")
     * @return ResponseEntity containing ApiResponse with list of job recommendations
     */
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> searchJobs(
            @RequestParam 
            @NotBlank(message = "Skills parameter is required")
            @Size(min = 1, max = 500, message = "Skills must be between 1 and 500 characters")
            String skills,
            
            @RequestParam 
            @NotBlank(message = "Location parameter is required")
            @Size(min = 1, max = 200, message = "Location must be between 1 and 200 characters")
            String location,
            
            @RequestParam 
            @NotBlank(message = "Experience parameter is required")
            @Size(min = 1, max = 50, message = "Experience must be between 1 and 50 characters")
            String experience) {

        logger.info("Received AI job search request - Skills: {}, Location: {}, Experience: {}", 
                   skills, location, experience);

        List<JobResponse> jobs = aiJobService.searchJobs(skills.trim(), location.trim(), experience.trim());
        
        logger.info("AI job search completed successfully. Found {} jobs", jobs.size());
        return ResponseEntity.ok(ApiResponseBuilder.success("AI job search completed successfully", jobs));
    }

    /**
     * Generates a Google Calendar API request for scheduling Google Meet interviews.
     * 
     * Example: POST /api/ai/calendar/google-meet
     * Request Body: {
     *   "dateTime": "June 14, 2025, at 11:00 AM IST",
     *   "jobId": "Job ID 1045",
     *   "interviewType": "Senior Backend Developer Interview",
     *   "duration": "30 minutes",
     *   "description": "Discussion with shortlisted candidates"
     * }
     * 
     * @param request the calendar meeting request containing dynamic parameters
     * @return ResponseEntity containing ApiResponse with Google Calendar API request body
     */
    @PostMapping("/calendar/google-meet")
    public ResponseEntity<ApiResponse<CalendarApiResponse>> generateGoogleCalendarRequest(
            @Valid @RequestBody CalendarMeetingRequest request) {

        logger.info("Received Google Calendar request generation - Job ID: {}, Interview Type: {}", 
                   request.getJobId(), request.getInterviewType());

        CalendarApiResponse calendarResponse = aiJobService.generateGoogleCalendarRequest(request);
        
        logger.info("Google Calendar request generated successfully for job: {}", request.getJobId());
        return ResponseEntity.ok(ApiResponseBuilder.success("Google Calendar request generated successfully", calendarResponse));
    }

    /**
     * Health check endpoint for the AI service.
     * 
     * @return ResponseEntity indicating the service status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.debug("AI service health check requested");
        
        return ResponseEntity.ok(ApiResponseBuilder.success(
            "AI Service is running", 
            "Service is healthy and ready to process AI requests"
        ));
    }
}
