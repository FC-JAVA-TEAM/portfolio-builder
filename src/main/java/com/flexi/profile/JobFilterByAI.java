package com.flexi.profile;


import java.io.File;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JobFilterByAI {

    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String MODEL = "claude-3-7-sonnet";
    private static final String JOBS_FILE = "jobs-response.json";

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<JsonNode> allJobs = mapper.readValue(new File(JOBS_FILE), new TypeReference<List<JsonNode>>() {});

            String jobsJsonArray = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(allJobs);
            String prompt = buildPrompt(jobsJsonArray);

            String aiResponse = callAI(prompt);
            System.out.println("Filtered AI Response:\n" + aiResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildPrompt(String jobsJsonArray) {
        return """
            I have a list of job postings in JSON array format.
            Please extract only those jobs where the `jobTitle` is contains "Java Developer" or contains the phrase " * Java ".
            Return an array of JSON objects containing ONLY the following fields:
            - id
            - date_posted
            - date_created
            - date_validthrough
            - jobTitle
            - salary_raw
            - company
            - experienceRequired
            - locations_derived
            - linkedin_org_specialties (rename this field to `skills`)
            - employment_type
            - source_type
            - postedTime
            - source
            - saveStatus
            - jobLink
            - linkedin_org_description

            Return only a clean JSON array. Do not add explanations or markdown syntax.

            Here is the input:

            """ + jobsJsonArray;
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

        ObjectNode systemMessage = mapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an expert AI who returns clean filtered JSON arrays only.");
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

            // Remove markdown fences if present
            content = content.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```", "").trim();

            // Try to pretty-print the JSON
            try {
                JsonNode parsed = mapper.readTree(content);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsed);
            } catch (Exception ex) {
                return "⚠️ JSON parse failed. Raw response:\n" + content;
            }
        }

        return "Invalid AI response.";
    }
}

