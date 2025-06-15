package com.flexi.profile;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class LinkedInJobService {
    private static final String API_URL = "https://linkedin-job-search-api.p.rapidapi.com/active-jb-7d";
    private static final String API_KEY = "1a60d722a7mshf6b249af08d8ddep1b2c0djsn03b277bc4ff8";
    private static final String API_HOST = "linkedin-job-search-api.p.rapidapi.com";

    public String searchJobs(String title, String location, int limit, int offset) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            String url = buildUrl(title, location, limit, offset);
            HttpGet request = new HttpGet(url);
            
            // Add headers
            request.setHeader("x-rapidapi-key", API_KEY);
            request.setHeader("x-rapidapi-host", API_HOST);
            
            // Execute request
            CloseableHttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            
            // Map response to our format
            return mapToRequiredFormat(responseBody);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String buildUrl(String title, String location, int limit, int offset) {
		return location;
        // Build URL with proper encoding
    }

    private String mapToRequiredFormat(String linkedInResponse) {
		return linkedInResponse;
        // Map LinkedIn response to our required format
    }
    
//    public static void main(String[] args) {
//        LinkedInJobService jobService = new LinkedInJobService();
//        String jobsJson = jobService.searchJobs(
//            "Data Engineer",
//            "United States OR United Kingdom",
//            10,
//            0
//        );
//        
//        // Use AI to format the response
//        String prompt = "Format these job listings according to the specified structure: " + jobsJson;
//        String formattedResponse = getAIResponse(prompt);
//        System.out.println("AI Response:" + formattedResponse);
//    }

}

