package com.flexi.profile.service;

import com.flexi.profile.dto.CalendarApiResponse;
import com.flexi.profile.dto.CalendarMeetingRequest;
import com.flexi.profile.dto.JobResponse;
import com.flexi.profile.exception.service.ai.AIJobServiceException;

import java.util.List;

/**
 * Service interface for AI-powered job search functionality.
 * Provides methods to search for jobs using AI-based recommendations
 * through the Fuelix API integration.
 * 
 * @author AI Job Service
 * @version 1.0
 */
public interface AIJobService {

    /**
     * Searches for jobs based on the provided criteria using AI recommendations.
     * 
     * @param skills the required skills for the job search (e.g., "Java, Spring Boot")
     * @param location the preferred job locations (e.g., "Hyderabad, Bangalore")
     * @param experience the required experience level (e.g., "5+ years")
     * @return a list of job responses matching the criteria
     * @throws AIJobServiceException if there's an error during the job search process
     * @throws IllegalArgumentException if any of the input parameters are invalid
     */
    List<JobResponse> searchJobs(String skills, String location, String experience) 
            throws AIJobServiceException;

    /**
     * Generates a Google Calendar API request using AI based on the provided meeting details.
     * 
     * @param request the calendar meeting request containing dynamic parameters
     * @return a structured calendar API response with Google Meet integration
     * @throws AIJobServiceException if there's an error during the calendar request generation
     * @throws IllegalArgumentException if any of the input parameters are invalid
     */
    CalendarApiResponse generateGoogleCalendarRequest(CalendarMeetingRequest request) 
            throws AIJobServiceException;
}
