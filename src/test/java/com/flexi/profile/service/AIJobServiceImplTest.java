package com.flexi.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexi.profile.dto.FuelixApiRequest;
import com.flexi.profile.dto.FuelixApiResponse;
import com.flexi.profile.dto.JobResponse;
import com.flexi.profile.exception.service.ai.AIJobServiceException;
import com.flexi.profile.exception.service.ai.FuelixApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AIJobServiceImpl using Mockito.
 * Tests service layer logic, external API integration, and error handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AI Job Service Implementation Tests")
class AIJobServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AIJobServiceImpl aiJobService;

    private static final String FUELIX_API_URL = "https://api-betta.fuelix.ai/chat/completions";
    private static final String FUELIX_API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(aiJobService, "fuelixApiUrl", FUELIX_API_URL);
        ReflectionTestUtils.setField(aiJobService, "fuelixApiKey", FUELIX_API_KEY);
        ReflectionTestUtils.setField(aiJobService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(aiJobService, "objectMapper", new ObjectMapper());
    }

    @Test
    @DisplayName("Search Jobs - Should return jobs successfully")
    void testSearchJobs_Success() throws Exception {
        // Given
        String skills = "Java,Spring Boot";
        String location = "Bangalore";
        String experience = "5 years";
        
        FuelixApiResponse mockApiResponse = createMockFuelixApiResponse();
        ResponseEntity<FuelixApiResponse> responseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        )).thenReturn(responseEntity);

        // When
        List<JobResponse> result = aiJobService.searchJobs(skills, location, experience);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        JobResponse firstJob = result.get(0);
        assertEquals("JOB123456", firstJob.getId());
        assertEquals("Senior Java Developer", firstJob.getJobTitle());
        assertEquals("TechSolutions India", firstJob.getCompany());
        assertTrue(firstJob.getSkills().contains("Java"));
        
        // Verify RestTemplate was called
        verify(restTemplate, times(1)).exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        );
    }

    @Test
    @DisplayName("Search Jobs - Should handle empty response")
    void testSearchJobs_EmptyResponse() throws Exception {
        // Given
        String skills = "COBOL";
        String location = "Remote";
        String experience = "20 years";
        
        FuelixApiResponse mockApiResponse = createEmptyFuelixApiResponse();
        ResponseEntity<FuelixApiResponse> responseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        )).thenReturn(responseEntity);

        // When
        List<JobResponse> result = aiJobService.searchJobs(skills, location, experience);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(restTemplate, times(1)).exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        );
    }

    @Test
    @DisplayName("Search Jobs - Should handle null skills parameter")
    void testSearchJobs_NullSkills() {
        // When & Then
        assertThrows(AIJobServiceException.class, () -> {
            aiJobService.searchJobs(null, "Bangalore", "5 years");
        });
        
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Search Jobs - Should handle empty skills parameter")
    void testSearchJobs_EmptySkills() {
        // When & Then
        assertThrows(AIJobServiceException.class, () -> {
            aiJobService.searchJobs("", "Bangalore", "5 years");
        });
        
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Search Jobs - Should handle null location parameter")
    void testSearchJobs_NullLocation() {
        // When & Then
        assertThrows(AIJobServiceException.class, () -> {
            aiJobService.searchJobs("Java", null, "5 years");
        });
        
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Search Jobs - Should handle empty location parameter")
    void testSearchJobs_EmptyLocation() {
        // When & Then
        assertThrows(AIJobServiceException.class, () -> {
            aiJobService.searchJobs("Java", "", "5 years");
        });
        
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Search Jobs - Should handle null experience parameter")
    void testSearchJobs_NullExperience() {
        // When & Then
        assertThrows(AIJobServiceException.class, () -> {
            aiJobService.searchJobs("Java", "Bangalore", null);
        });
        
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Search Jobs - Should handle empty experience parameter")
    void testSearchJobs_EmptyExperience() {
        // When & Then
        assertThrows(AIJobServiceException.class, () -> {
            aiJobService.searchJobs("Java", "Bangalore", "");
        });
        
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Search Jobs - Should handle RestTemplate exception")
    void testSearchJobs_RestTemplateException() {
        // Given
        String skills = "Java";
        String location = "Bangalore";
        String experience = "5 years";
        
        when(restTemplate.exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        )).thenThrow(new RestClientException("Connection timeout"));

        // When & Then
        FuelixApiException exception = assertThrows(FuelixApiException.class, () -> {
            aiJobService.searchJobs(skills, location, experience);
        });
        
        assertTrue(exception.getMessage().contains("Failed to call Fuelix API"));
        
        verify(restTemplate, times(1)).exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        );
    }

    @Test
    @DisplayName("Search Jobs - Should handle HTTP error response")
    void testSearchJobs_HttpErrorResponse() {
        // Given
        String skills = "Java";
        String location = "Bangalore";
        String experience = "5 years";
        
        ResponseEntity<FuelixApiResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        
        when(restTemplate.exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        )).thenReturn(responseEntity);

        // When & Then
        FuelixApiException exception = assertThrows(FuelixApiException.class, () -> {
            aiJobService.searchJobs(skills, location, experience);
        });
        
        assertTrue(exception.getMessage().contains("Fuelix API returned error"));
        
        verify(restTemplate, times(1)).exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        );
    }

    @Test
    @DisplayName("Search Jobs - Should handle malformed API response")
    void testSearchJobs_MalformedResponse() {
        // Given
        String skills = "Java";
        String location = "Bangalore";
        String experience = "5 years";
        
        FuelixApiResponse malformedResponse = new FuelixApiResponse();
        // Set malformed response with null choices
        malformedResponse.setChoices(null);
        
        ResponseEntity<FuelixApiResponse> responseEntity = new ResponseEntity<>(malformedResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        )).thenReturn(responseEntity);

        // When & Then
        AIJobServiceException exception = assertThrows(AIJobServiceException.class, () -> {
            aiJobService.searchJobs(skills, location, experience);
        });
        
        assertTrue(exception.getMessage().contains("Failed to parse jobs from AI response"));
        
        verify(restTemplate, times(1)).exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        );
    }

    @Test
    @DisplayName("Search Jobs - Should handle special characters in parameters")
    void testSearchJobs_SpecialCharacters() throws Exception {
        // Given
        String skills = "C++,C#,.NET";
        String location = "Delhi";
        String experience = "3+ years";
        
        FuelixApiResponse mockApiResponse = createMockFuelixApiResponse();
        ResponseEntity<FuelixApiResponse> responseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        )).thenReturn(responseEntity);

        // When
        List<JobResponse> result = aiJobService.searchJobs(skills, location, experience);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        verify(restTemplate, times(1)).exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        );
    }

    @Test
    @DisplayName("Search Jobs - Should handle long parameter values")
    void testSearchJobs_LongParameters() throws Exception {
        // Given
        String longSkills = "Java,Spring Boot,Microservices,AWS,Docker,Kubernetes,Jenkins,Git,Maven,Gradle,Hibernate,JPA,REST,SOAP,JSON,XML";
        String longLocation = "Bangalore,Hyderabad,Chennai,Mumbai,Delhi,Pune,Kolkata,Ahmedabad,Surat,Jaipur";
        String experience = "5-10 years";
        
        FuelixApiResponse mockApiResponse = createMockFuelixApiResponse();
        ResponseEntity<FuelixApiResponse> responseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        )).thenReturn(responseEntity);

        // When
        List<JobResponse> result = aiJobService.searchJobs(longSkills, longLocation, experience);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        verify(restTemplate, times(1)).exchange(
                eq(FUELIX_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FuelixApiResponse.class)
        );
    }

    /**
     * Helper method to create mock Fuelix API response
     */
    private FuelixApiResponse createMockFuelixApiResponse() {
        FuelixApiResponse response = new FuelixApiResponse();
        
        FuelixApiResponse.Choice choice = new FuelixApiResponse.Choice();
        FuelixApiResponse.Message message = new FuelixApiResponse.Message();
        
        String mockJobsJson = """
            [
                {
                    "id": "JOB123456",
                    "company": "TechSolutions India",
                    "skills": ["Java", "Spring Boot", "Microservices", "AWS"],
                    "source": "Naukri.com",
                    "date_posted": "2023-05-15",
                    "jobTitle": "Senior Java Developer",
                    "salary_raw": "₹15,00,000 - ₹20,00,000 per annum",
                    "experienceRequired": "5-7 years",
                    "locations_derived": "Bangalore, Karnataka",
                    "employment_type": "Full-time"
                },
                {
                    "id": "JOB789012",
                    "company": "InnovateTech",
                    "skills": ["Java", "Spring", "Hibernate", "REST API"],
                    "source": "LinkedIn",
                    "date_posted": "2023-05-18",
                    "jobTitle": "Java Technical Lead",
                    "salary_raw": "₹18,00,000 - ₹25,00,000 per annum",
                    "experienceRequired": "6-8 years",
                    "locations_derived": "Hyderabad, Telangana",
                    "employment_type": "Full-time"
                }
            ]
            """;
        
        message.setContent(mockJobsJson);
        choice.setMessage(message);
        response.setChoices(Collections.singletonList(choice));
        
        return response;
    }

    /**
     * Helper method to create empty Fuelix API response
     */
    private FuelixApiResponse createEmptyFuelixApiResponse() {
        FuelixApiResponse response = new FuelixApiResponse();
        
        FuelixApiResponse.Choice choice = new FuelixApiResponse.Choice();
        FuelixApiResponse.Message message = new FuelixApiResponse.Message();
        
        message.setContent("[]");
        choice.setMessage(message);
        response.setChoices(Collections.singletonList(choice));
        
        return response;
    }
}
