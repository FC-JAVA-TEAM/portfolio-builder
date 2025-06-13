package com.flexi.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexi.profile.dto.CalendarApiResponse;
import com.flexi.profile.dto.CalendarMeetingRequest;
import com.flexi.profile.dto.JobResponse;
import com.flexi.profile.exception.service.ai.AIJobServiceException;
import com.flexi.profile.response.ApiResponse;
import com.flexi.profile.service.AIJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AIController using MockMvc and Mockito.
 * Tests all endpoints, success scenarios, error handling, and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AI Controller Tests")
class AIControllerTest {

    @Mock
    private AIJobService aiJobService;

    @InjectMocks
    private AIController aiController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Health Check - Should return success response")
    void testHealthCheck_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ai/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("AI Service is running"))
                .andExpect(jsonPath("$.data").value("Service is healthy and ready to process AI requests"))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        // Verify no service calls were made for health check
        verifyNoInteractions(aiJobService);
    }

    @Test
    @DisplayName("Job Search - Should return jobs successfully")
    void testSearchJobs_Success() throws Exception {
        // Given
        List<JobResponse> mockJobs = createMockJobResponses();
        when(aiJobService.searchJobs("Java", "Bangalore", "5 years")).thenReturn(mockJobs);

        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "Java")
                        .param("location", "Bangalore")
                        .param("experience", "5 years"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("AI job search completed successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value("JOB001"))
                .andExpect(jsonPath("$.data[0].jobTitle").value("Senior Java Developer"))
                .andExpect(jsonPath("$.data[0].company").value("TechCorp"))
                .andExpect(jsonPath("$.data[0].skills").isArray())
                .andExpect(jsonPath("$.data[0].skills", hasItem("Java")))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        // Verify service interaction
        verify(aiJobService, times(1)).searchJobs("Java", "Bangalore", "5 years");
    }

    @Test
    @DisplayName("Job Search - Should handle multiple skills")
    void testSearchJobs_MultipleSkills() throws Exception {
        // Given
        List<JobResponse> mockJobs = createMockJobResponses();
        when(aiJobService.searchJobs("Java,Spring Boot,Microservices", "Hyderabad", "3+ years"))
                .thenReturn(mockJobs);

        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "Java,Spring Boot,Microservices")
                        .param("location", "Hyderabad")
                        .param("experience", "3+ years"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(aiJobService, times(1)).searchJobs("Java,Spring Boot,Microservices", "Hyderabad", "3+ years");
    }

    @Test
    @DisplayName("Job Search - Should handle empty results")
    void testSearchJobs_EmptyResults() throws Exception {
        // Given
        when(aiJobService.searchJobs("COBOL", "Remote", "20 years")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "COBOL")
                        .param("location", "Remote")
                        .param("experience", "20 years"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(aiJobService, times(1)).searchJobs("COBOL", "Remote", "20 years");
    }

    @Test
    @DisplayName("Job Search - Should handle service exception")
    void testSearchJobs_ServiceException() throws Exception {
        // Given
        when(aiJobService.searchJobs(anyString(), anyString(), anyString()))
                .thenThrow(new AIJobServiceException("External API error"));

        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "Java")
                        .param("location", "Bangalore")
                        .param("experience", "5 years"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("AI job search failed: External API error"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error").isNotEmpty());

        verify(aiJobService, times(1)).searchJobs("Java", "Bangalore", "5 years");
    }

    @Test
    @DisplayName("Job Search - Should handle missing skills parameter")
    void testSearchJobs_MissingSkillsParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("location", "Bangalore")
                        .param("experience", "5 years"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify no service interaction
        verifyNoInteractions(aiJobService);
    }

    @Test
    @DisplayName("Job Search - Should handle missing location parameter")
    void testSearchJobs_MissingLocationParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "Java")
                        .param("experience", "5 years"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify no service interaction
        verifyNoInteractions(aiJobService);
    }

    @Test
    @DisplayName("Job Search - Should handle missing experience parameter")
    void testSearchJobs_MissingExperienceParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "Java")
                        .param("location", "Bangalore"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify no service interaction
        verifyNoInteractions(aiJobService);
    }

    @Test
    @DisplayName("Job Search - Should handle special characters in parameters")
    void testSearchJobs_SpecialCharacters() throws Exception {
        // Given
        List<JobResponse> mockJobs = createMockJobResponses();
        when(aiJobService.searchJobs("C++,C#,.NET", "Delhi", "3+ years")).thenReturn(mockJobs);

        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "C++,C#,.NET")
                        .param("location", "Delhi")
                        .param("experience", "3+ years"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(aiJobService, times(1)).searchJobs("C++,C#,.NET", "Delhi", "3+ years");
    }

    @Test
    @DisplayName("Job Search - Should handle long parameter values")
    void testSearchJobs_LongParameters() throws Exception {
        // Given
        String longSkills = "Java,Spring Boot,Microservices,AWS,Docker,Kubernetes,Jenkins,Git,Maven,Gradle";
        String longLocation = "Bangalore,Hyderabad,Chennai,Mumbai,Delhi,Pune,Kolkata,Ahmedabad";
        List<JobResponse> mockJobs = createMockJobResponses();
        when(aiJobService.searchJobs(longSkills, longLocation, "5-10 years")).thenReturn(mockJobs);

        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", longSkills)
                        .param("location", longLocation)
                        .param("experience", "5-10 years"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(aiJobService, times(1)).searchJobs(longSkills, longLocation, "5-10 years");
    }

    @Test
    @DisplayName("Job Search - Should handle runtime exception")
    void testSearchJobs_RuntimeException() throws Exception {
        // Given
        when(aiJobService.searchJobs(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(get("/api/ai/jobs")
                        .param("skills", "Java")
                        .param("location", "Bangalore")
                        .param("experience", "5 years"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));

        verify(aiJobService, times(1)).searchJobs("Java", "Bangalore", "5 years");
    }

    @Test
    @DisplayName("Calendar Request - Should generate Google Calendar request successfully")
    void testGenerateGoogleCalendarRequest_Success() throws Exception {
        // Given
        CalendarMeetingRequest request = createMockCalendarRequest();
        CalendarApiResponse mockResponse = createMockCalendarResponse();
        when(aiJobService.generateGoogleCalendarRequest(org.mockito.ArgumentMatchers.any(CalendarMeetingRequest.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/ai/calendar/google-meet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Google Calendar request generated successfully"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.summary").value("Job ID 1045: Senior Backend Developer Interview"))
                .andExpect(jsonPath("$.data.description").value("Discussion with shortlisted candidates"))
                .andExpect(jsonPath("$.data.start.dateTime").value("2025-06-14T11:00:00+05:30"))
                .andExpect(jsonPath("$.data.end.dateTime").value("2025-06-14T11:30:00+05:30"))
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        // Verify service interaction
        verify(aiJobService, times(1)).generateGoogleCalendarRequest(org.mockito.ArgumentMatchers.any(CalendarMeetingRequest.class));
    }

    @Test
    @DisplayName("Calendar Request - Should handle missing required fields")
    void testGenerateGoogleCalendarRequest_MissingFields() throws Exception {
        // Given - request with missing dateTime
        CalendarMeetingRequest request = new CalendarMeetingRequest();
        request.setJobId("Job ID 1045");
        request.setInterviewType("Senior Backend Developer Interview");
        request.setDuration("30 minutes");
        request.setDescription("Discussion with shortlisted candidates");

        // When & Then
        mockMvc.perform(post("/api/ai/calendar/google-meet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify no service interaction
        verifyNoInteractions(aiJobService);
    }

    @Test
    @DisplayName("Calendar Request - Should handle service exception")
    void testGenerateGoogleCalendarRequest_ServiceException() throws Exception {
        // Given
        CalendarMeetingRequest request = createMockCalendarRequest();
        when(aiJobService.generateGoogleCalendarRequest(org.mockito.ArgumentMatchers.any(CalendarMeetingRequest.class)))
                .thenThrow(new AIJobServiceException("AI service error"));

        // When & Then
        mockMvc.perform(post("/api/ai/calendar/google-meet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Google Calendar request generation failed: AI service error"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error").isNotEmpty());

        verify(aiJobService, times(1)).generateGoogleCalendarRequest(org.mockito.ArgumentMatchers.any(CalendarMeetingRequest.class));
    }

    @Test
    @DisplayName("Calendar Request - Should handle invalid JSON")
    void testGenerateGoogleCalendarRequest_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/ai/calendar/google-meet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify no service interaction
        verifyNoInteractions(aiJobService);
    }

    @Test
    @DisplayName("Calendar Request - Should handle empty request body")
    void testGenerateGoogleCalendarRequest_EmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/ai/calendar/google-meet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Verify no service interaction
        verifyNoInteractions(aiJobService);
    }

    /**
     * Helper method to create mock calendar meeting request for testing
     */
    private CalendarMeetingRequest createMockCalendarRequest() {
        CalendarMeetingRequest request = new CalendarMeetingRequest();
        request.setDateTime("June 14, 2025, at 11:00 AM IST");
        request.setJobId("Job ID 1045");
        request.setInterviewType("Senior Backend Developer Interview");
        request.setDuration("30 minutes");
        request.setDescription("Discussion with shortlisted candidates");
        return request;
    }

    /**
     * Helper method to create mock calendar API response for testing
     */
    private CalendarApiResponse createMockCalendarResponse() {
        CalendarApiResponse response = new CalendarApiResponse();
        response.setSummary("Job ID 1045: Senior Backend Developer Interview");
        response.setDescription("Discussion with shortlisted candidates");
        
        CalendarApiResponse.EventTime start = new CalendarApiResponse.EventTime();
        start.setDateTime("2025-06-14T11:00:00+05:30");
        start.setTimeZone("Asia/Kolkata");
        response.setStart(start);
        
        CalendarApiResponse.EventTime end = new CalendarApiResponse.EventTime();
        end.setDateTime("2025-06-14T11:30:00+05:30");
        end.setTimeZone("Asia/Kolkata");
        response.setEnd(end);
        
        CalendarApiResponse.ConferenceData conferenceData = new CalendarApiResponse.ConferenceData();
        CalendarApiResponse.ConferenceData.CreateRequest createRequest = new CalendarApiResponse.ConferenceData.CreateRequest();
        createRequest.setRequestId("job-1045-backend-interview");
        CalendarApiResponse.ConferenceData.CreateRequest.ConferenceSolutionKey solutionKey = 
            new CalendarApiResponse.ConferenceData.CreateRequest.ConferenceSolutionKey("hangoutsMeet");
        createRequest.setConferenceSolutionKey(solutionKey);
        conferenceData.setCreateRequest(createRequest);
        response.setConferenceData(conferenceData);
        
        return response;
    }

    /**
     * Helper method to create mock job responses for testing
     */
    private List<JobResponse> createMockJobResponses() {
        JobResponse job1 = new JobResponse();
        job1.setId("JOB001");
        job1.setJobTitle("Senior Java Developer");
        job1.setCompany("TechCorp");
        job1.setLocationsDerived("Bangalore, Karnataka");
        job1.setExperienceRequired("5-7 years");
        job1.setSkills(Arrays.asList("Java", "Spring Boot", "Microservices", "AWS"));
        job1.setEmploymentType("Full-time");
        job1.setSalaryRaw("15-20 LPA");

        JobResponse job2 = new JobResponse();
        job2.setId("JOB002");
        job2.setJobTitle("Java Technical Lead");
        job2.setCompany("InnovateTech");
        job2.setLocationsDerived("Hyderabad, Telangana");
        job2.setExperienceRequired("6-8 years");
        job2.setSkills(Arrays.asList("Java", "Spring", "Hibernate", "REST API"));
        job2.setEmploymentType("Full-time");
        job2.setSalaryRaw("18-25 LPA");

        return Arrays.asList(job1, job2);
    }
}
