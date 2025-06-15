package com.flexi.profile;

import java.io.File;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JobProcessor {

    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String MODEL = "claude-3-7-sonnet";
    private static final String JOBS_FILE = "jobs-response.json";

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<JsonNode> jobList = mapper.readValue(new File(JOBS_FILE), new TypeReference<List<JsonNode>>() {});

            for (JsonNode jobNode : jobList) {
                String jobJson = mapper.writeValueAsString(jobNode);
                String prompt = buildPrompt(jobJson);
                String aiResponse = callAI(prompt);

                System.out.println("AI Response:\n" + aiResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildPrompt(String jobJson) {
       // return "Extract the following fields: source, employment_type, and url from this job posting:\n\n" + jobJson;
        return  "Give me only id,date_posted,date_created,date_validthrough,jobTitle, salary_raw,company, experienceRequired, locations_derived, linkedin_org_specialties as skills,employment_type,source_type, postedTime, source, saveStatus, jobLink,linkedin_org_description: and give only jobTitle = Java Developer \n\n" + jobJson;
    }

    private static String callAI(String prompt) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(AUTH_TOKEN);

            ObjectNode requestBody = buildRequestBody(mapper, prompt);
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            return extractResponse(mapper, response.getBody());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error.");
        }
        
        
        
        
        
        
        
        
    }
    
    

    private static ObjectNode buildRequestBody(ObjectMapper mapper, String prompt) {
        ObjectNode requestBody = mapper.createObjectNode();
        ArrayNode messages = mapper.createArrayNode();

        // Optional: system message to clarify task (can be removed or adjusted)
        ObjectNode systemMessage = mapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant. Return only a JSON object and give only jobTitle = Java Developer");
       // systemMessage.put("content", "You are a helpful assistant. Return only a JSON object with fields: source, employment_type, url.");
       // systemMessage.put("content", "Give me only id,date_posted,date_created,date_validthrough,jobTitle, salary_raw,company, experienceRequired, locations_derived, linkedin_org_specialties as skills,employment_type,source_type, postedTime, source, saveStatus, jobLink,linkedin_org_description: and jobTitle is Java Developer");
        messages.add(systemMessage);

        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.set("messages", messages);
        requestBody.put("model", MODEL);
        requestBody.put("temperature", 0.1);

        return requestBody;
    }


    
    private static String extractResponse(ObjectMapper mapper, String responseBody) throws Exception {
        JsonNode root = mapper.readTree(responseBody);
        JsonNode choices = root.path("choices");

        if (choices.isArray() && choices.size() > 0) {
            String content = choices.get(0).path("message").path("content").asText();

            // Attempt to extract the first JSON object from the text
            int jsonStart = content.indexOf('{');
            int jsonEnd = content.lastIndexOf('}');
            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                String jsonString = content.substring(jsonStart, jsonEnd + 1);

                // Try parsing cleaned JSON
                try {
                    JsonNode contentJson = mapper.readTree(jsonString);
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(contentJson);
                } catch (Exception ex) {
                    return "JSON Parse Error: " + ex.getMessage() + "\nOriginal content:\n" + content;
                }
            } else {
                return "Could not find JSON object in response.\nRaw content:\n" + content;
            }
        }
        return "Invalid AI response.";
    }

}
