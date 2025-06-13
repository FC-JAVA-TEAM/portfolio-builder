package com.flexi.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexi.profile.dto.CalendarApiResponse;
import com.flexi.profile.dto.CalendarMeetingRequest;
import com.flexi.profile.dto.FuelixApiRequest;
import com.flexi.profile.dto.FuelixApiResponse;
import com.flexi.profile.dto.JobResponse;
import com.flexi.profile.exception.service.ai.AIJobServiceException;
import com.flexi.profile.exception.service.ai.FuelixApiException;
import com.flexi.profile.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AIJobService for AI-powered job search functionality.
 * Integrates with the Fuelix AI API to provide intelligent job recommendations
 * based on skills, location, and experience criteria.
 * 
 * @author AI Job Service
 * @version 1.0
 */
@Service
public class AIJobServiceImpl implements AIJobService {

    private static final Logger logger = LoggerFactory.getLogger(AIJobServiceImpl.class);

    // Configuration properties
    @Value("${fuelix.api.url}")
    private String fuelixApiUrl;

    @Value("${fuelix.api.token}")
    private String fuelixApiToken;

    @Value("${fuelix.api.model}")
    private String fuelixApiModel;

    @Value("${fuelix.api.temperature}")
    private Double fuelixApiTemperature;

    @Value("${fuelix.api.timeout:30000}")
    private Integer fuelixApiTimeout;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Validation constants
    private static final int MAX_SKILLS_LENGTH = 500;
    private static final int MAX_LOCATION_LENGTH = 200;
    private static final int MAX_EXPERIENCE_LENGTH = 50;
    private static final String MARKDOWN_JSON_PREFIX = "```json";
    private static final String MARKDOWN_SUFFIX = "```";

    /**
     * Constructor with dependency injection
     */
    public AIJobServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        // Configure RestTemplate timeout
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", "FlexiProfile-AIJobService/1.0");
            return execution.execute(request, body);
        });
    }

    @Override
    public List<JobResponse> searchJobs(String skills, String location, String experience) 
            throws AIJobServiceException {
        
        logger.debug("Starting AI job search with skills: {}, location: {}, experience: {}", 
                     skills, location, experience);
        LogUtil.logMethodEntry(logger, "searchJobs", skills, location, experience);

        try {
            // Validate input parameters
            validateSearchParameters(skills, location, experience);

            // Build the AI request
            FuelixApiRequest apiRequest = buildFuelixApiRequest(skills, location, experience);
            
            // Call the Fuelix API
            FuelixApiResponse apiResponse = callFuelixApi(apiRequest);
            
            // Extract and parse the response content
            String aiResponseContent = extractResponseContent(apiResponse);
            
            // Parse jobs from the AI response
            List<JobResponse> jobs = parseJobsFromAIResponse(aiResponseContent);
            
            logger.info("Successfully retrieved {} jobs from AI search", jobs.size());
            LogUtil.logMethodExit(logger, "searchJobs", jobs.size() + " jobs found");
            
            return jobs;
            
        } catch (AIJobServiceException e) {
            logger.error("AI job service error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during AI job search: {}", e.getMessage(), e);
            throw new AIJobServiceException("Unexpected error during job search", e);
        }
    }

    private void validateSearchParameters(String skills, String location, String experience) 
            throws IllegalArgumentException {
        
        if (!StringUtils.hasText(skills)) {
            throw new IllegalArgumentException("Skills parameter cannot be null or empty");
        }
        if (!StringUtils.hasText(location)) {
            throw new IllegalArgumentException("Location parameter cannot be null or empty");
        }
        if (!StringUtils.hasText(experience)) {
            throw new IllegalArgumentException("Experience parameter cannot be null or empty");
        }
        
        if (skills.trim().length() > MAX_SKILLS_LENGTH) {
            throw new IllegalArgumentException("Skills parameter exceeds maximum length of " + MAX_SKILLS_LENGTH);
        }
        if (location.trim().length() > MAX_LOCATION_LENGTH) {
            throw new IllegalArgumentException("Location parameter exceeds maximum length of " + MAX_LOCATION_LENGTH);
        }
        if (experience.trim().length() > MAX_EXPERIENCE_LENGTH) {
            throw new IllegalArgumentException("Experience parameter exceeds maximum length of " + MAX_EXPERIENCE_LENGTH);
        }
    }

    private String buildJobSearchPrompt(String skills, String location, String experience) {
        return String.format(
            "Give me jobs from browser who is looking for Jobs in %s location %s with %s experienceRequired. " +
            "Give me details like id, date_posted, date_created, date_validthrough, jobTitle, salary_raw, " +
            "company, experienceRequired, locations_derived, skills, employment_type, source_type, " +
            "postedTime, source, saveStatus, jobLink, linkedin_org_description. Give me response in object.",
            skills, location, experience
        );
    }

    private List<JobResponse> parseJobsFromAIResponse(String aiResponseContent) 
            throws AIJobServiceException {
        
        try {
            // Extract JSON from markdown formatted response
            String jsonContent = extractJsonFromMarkdown(aiResponseContent);
            
            // Parse the JSON content
            JsonNode rootNode = objectMapper.readTree(jsonContent);
            
            // Extract the jobs array
            JsonNode jobsNode = rootNode.get("jobs");
            if (jobsNode == null || !jobsNode.isArray()) {
                throw new AIJobServiceException("Invalid response format: 'jobs' array not found");
            }
            
            // Convert to JobResponse objects
            List<JobResponse> jobs = objectMapper.convertValue(
                jobsNode, 
                new TypeReference<List<JobResponse>>() {}
            );
            
            logger.debug("Successfully parsed {} jobs from AI response", jobs.size());
            return jobs;
            
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response: {}", e.getMessage(), e);
            throw new AIJobServiceException("Failed to parse AI response as JSON", e);
        } catch (Exception e) {
            logger.error("Error processing AI response: {}", e.getMessage(), e);
            throw new AIJobServiceException("Failed to process AI response", e);
        }
    }

    /**
     * Builds the Fuelix API request object
     */
    private FuelixApiRequest buildFuelixApiRequest(String skills, String location, String experience) {
        String userPrompt = buildJobSearchPrompt(skills, location, experience);
        
        List<FuelixApiRequest.Message> messages = Arrays.asList(
            new FuelixApiRequest.Message("system", "Hello"),
            new FuelixApiRequest.Message("user", userPrompt)
        );
        
        return new FuelixApiRequest(messages, fuelixApiModel, fuelixApiTemperature);
    }

    /**
     * Makes the HTTP call to the Fuelix API
     */
    private FuelixApiResponse callFuelixApi(FuelixApiRequest apiRequest) throws FuelixApiException {
        LogUtil.logMethodEntry(logger, "callFuelixApi");
        
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(fuelixApiToken);
            
            // Create HTTP entity
            HttpEntity<FuelixApiRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
            
            logger.debug("Calling Fuelix API at: {}", fuelixApiUrl);
            
            // Make the API call
            ResponseEntity<FuelixApiResponse> responseEntity = restTemplate.exchange(
                fuelixApiUrl,
                HttpMethod.POST,
                requestEntity,
                FuelixApiResponse.class
            );
            
            FuelixApiResponse response = responseEntity.getBody();
            if (response == null) {
                throw new FuelixApiException("Received null response from Fuelix API");
            }
            
            logger.debug("Successfully received response from Fuelix API");
            LogUtil.logMethodExit(logger, "callFuelixApi", "API call successful");
            
            return response;
            
        } catch (HttpClientErrorException e) {
            logger.error("Client error calling Fuelix API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new FuelixApiException(
                "Client error calling Fuelix API: " + e.getMessage(), 
                e, 
                e.getStatusCode().value()
            );
        } catch (HttpServerErrorException e) {
            logger.error("Server error calling Fuelix API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new FuelixApiException(
                "Server error calling Fuelix API: " + e.getMessage(), 
                e, 
                e.getStatusCode().value()
            );
        } catch (ResourceAccessException e) {
            logger.error("Network error calling Fuelix API: {}", e.getMessage(), e);
            throw new FuelixApiException("Network error calling Fuelix API: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error calling Fuelix API: {}", e.getMessage(), e);
            throw new FuelixApiException("Unexpected error calling Fuelix API", e);
        }
    }

    /**
     * Extracts the content from the first choice in the API response
     */
    private String extractResponseContent(FuelixApiResponse apiResponse) throws AIJobServiceException {
        if (apiResponse.getChoices() == null || apiResponse.getChoices().isEmpty()) {
            throw new AIJobServiceException("No choices found in API response");
        }
        
        FuelixApiResponse.Choice firstChoice = apiResponse.getChoices().get(0);
        if (firstChoice.getMessage() == null || firstChoice.getMessage().getContent() == null) {
            throw new AIJobServiceException("No content found in API response message");
        }
        
        return firstChoice.getMessage().getContent();
    }

    /**
     * Removes markdown formatting from the AI response content
     */
    private String cleanMarkdownFormatting(String content) {
        if (content == null) {
            return null;
        }
        
        String cleaned = content.trim();
        
        // Remove markdown JSON code block formatting
        if (cleaned.startsWith(MARKDOWN_JSON_PREFIX)) {
            cleaned = cleaned.substring(MARKDOWN_JSON_PREFIX.length());
        }
        if (cleaned.endsWith(MARKDOWN_SUFFIX)) {
            cleaned = cleaned.substring(0, cleaned.length() - MARKDOWN_SUFFIX.length());
        }
        
        return cleaned.trim();
    }

    /**
     * Extracts JSON content from markdown formatted response
     */
    private String extractJsonFromMarkdown(String content) throws AIJobServiceException {
        if (content == null || content.trim().isEmpty()) {
            throw new AIJobServiceException("AI response content is null or empty");
        }
        
        String trimmedContent = content.trim();
        
        // Look for JSON code block markers
        int jsonStart = trimmedContent.indexOf("```json");
        if (jsonStart == -1) {
            // Try without the language specifier
            jsonStart = trimmedContent.indexOf("```");
        }
        
        if (jsonStart != -1) {
            // Find the start of JSON content
            int contentStart = trimmedContent.indexOf('\n', jsonStart);
            if (contentStart == -1) {
                contentStart = jsonStart + (trimmedContent.substring(jsonStart).startsWith("```json") ? 7 : 3);
            } else {
                contentStart++; // Skip the newline
            }
            
            // Find the end of the code block
            int jsonEnd = trimmedContent.indexOf("```", contentStart);
            if (jsonEnd == -1) {
                // If no closing marker, take the rest of the content
                jsonEnd = trimmedContent.length();
            }
            
            String jsonContent = trimmedContent.substring(contentStart, jsonEnd).trim();
            
            if (jsonContent.isEmpty()) {
                throw new AIJobServiceException("No JSON content found in markdown code block");
            }
            
            return jsonContent;
        } else {
            // No markdown formatting, try to extract JSON directly
            // Look for the first '{' and last '}'
            int firstBrace = trimmedContent.indexOf('{');
            int lastBrace = trimmedContent.lastIndexOf('}');
            
            if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
                return trimmedContent.substring(firstBrace, lastBrace + 1);
            } else {
                // Return the content as-is and let JSON parser handle it
                return trimmedContent;
            }
        }
    }

    @Override
    public CalendarApiResponse generateGoogleCalendarRequest(CalendarMeetingRequest request) 
            throws AIJobServiceException {
        
        logger.debug("Starting Google Calendar request generation for: {}", request);
        LogUtil.logMethodEntry(logger, "generateGoogleCalendarRequest", request);

        try {
            // Validate input parameters
            validateCalendarRequest(request);

            // Build the AI request for calendar
            FuelixApiRequest apiRequest = buildCalendarFuelixApiRequest(request);
            
            // Call the Fuelix API
            FuelixApiResponse apiResponse = callFuelixApi(apiRequest);
            
            // Extract and parse the response content
            String aiResponseContent = extractResponseContent(apiResponse);
            
            // Parse calendar response from the AI response
            CalendarApiResponse calendarResponse = parseCalendarResponseFromAI(aiResponseContent);
            
            logger.info("Successfully generated Google Calendar request for job: {}", request.getJobId());
            LogUtil.logMethodExit(logger, "generateGoogleCalendarRequest", "Calendar request generated");
            
            return calendarResponse;
            
        } catch (AIJobServiceException e) {
            logger.error("AI calendar service error: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during calendar request generation: {}", e.getMessage(), e);
            throw new AIJobServiceException("Unexpected error during calendar request generation", e);
        }
    }

    private void validateCalendarRequest(CalendarMeetingRequest request) 
            throws IllegalArgumentException {
        
        if (request == null) {
            throw new IllegalArgumentException("Calendar meeting request cannot be null");
        }
        
        if (!StringUtils.hasText(request.getDateTime())) {
            throw new IllegalArgumentException("Date and time cannot be null or empty");
        }
        if (!StringUtils.hasText(request.getJobId())) {
            throw new IllegalArgumentException("Job ID cannot be null or empty");
        }
        if (!StringUtils.hasText(request.getInterviewType())) {
            throw new IllegalArgumentException("Interview type cannot be null or empty");
        }
        if (!StringUtils.hasText(request.getDuration())) {
            throw new IllegalArgumentException("Duration cannot be null or empty");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
    }

    private String buildCalendarPrompt(CalendarMeetingRequest request) {
        return String.format(
            "Create a sample Google Calendar API request to Schedule a Google Meet on %s for %s: %s. " +
            "Duration: %s. Description: %s. Generate a request body as a output",
            request.getDateTime(),
            request.getJobId(),
            request.getInterviewType(),
            request.getDuration(),
            request.getDescription()
        );
    }

    private CalendarApiResponse parseCalendarResponseFromAI(String aiResponseContent) 
            throws AIJobServiceException {
        
        try {
            // Extract JSON from markdown formatted response
            String jsonContent = extractJsonFromMarkdown(aiResponseContent);
            
            // Parse the JSON content directly as CalendarApiResponse
            CalendarApiResponse calendarResponse = objectMapper.readValue(jsonContent, CalendarApiResponse.class);
            
            logger.debug("Successfully parsed calendar response from AI");
            return calendarResponse;
            
        } catch (JsonProcessingException e) {
            logger.error("Error parsing calendar JSON response: {}", e.getMessage(), e);
            throw new AIJobServiceException("Failed to parse AI calendar response as JSON", e);
        } catch (Exception e) {
            logger.error("Error processing AI calendar response: {}", e.getMessage(), e);
            throw new AIJobServiceException("Failed to process AI calendar response", e);
        }
    }

    /**
     * Builds the Fuelix API request object for calendar requests
     */
    private FuelixApiRequest buildCalendarFuelixApiRequest(CalendarMeetingRequest request) {
        String userPrompt = buildCalendarPrompt(request);
        
        List<FuelixApiRequest.Message> messages = Arrays.asList(
            new FuelixApiRequest.Message("system", "Hello"),
            new FuelixApiRequest.Message("user", userPrompt)
        );
        
        return new FuelixApiRequest(messages, fuelixApiModel, fuelixApiTemperature);
    }
}
