package com.flexi.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class AIService {
    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String DEFAULT_ERROR = "An error occurred while processing your request. Please try again.";

    public static String getAIResponse(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + AUTH_TOKEN);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            ArrayNode messages = mapper.createArrayNode();

            // Add system message to set expected schema
            ObjectNode systemMessage = mapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content",
            	    "{\"id\":\"\",\"datePosted\":\"\",\"dateCreated\":\"\",\"title\":\"\",\"organization\":\"\"," +
            	    "\"job_link\":\"\",\"source\":\"\",\"sourceDomain\":\"\",\"locationsDerived\":\"\"," +
            	    "\"skills\":[],\"rating\":null,\"status\":\"\",\"applied\":false,\"ai\":\"\"}"
            	);

            messages.add(systemMessage);

            // Add user prompt
            ObjectNode userMessage = mapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.set("messages", messages);
            requestBody.put("model", "claude-3-7-sonnet");
            requestBody.put("temperature", 0.1);

            System.out.println("Request Body:\n" + requestBody.toPrettyString());

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            System.out.println("Raw Response:\n" + response.getBody());

            JsonNode responseBody = mapper.readTree(response.getBody());
            if (responseBody.has("choices") && responseBody.get("choices").isArray()) {
                JsonNode choices = responseBody.get("choices");
                if (choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null && message.has("content")) {
                        String content = message.get("content").asText();

                        // Try to extract the first valid JSON block
                        int jsonStart = content.indexOf("{");
                        int jsonEnd = content.lastIndexOf("}");

                        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                            String jsonSubstring = content.substring(jsonStart, jsonEnd + 1);
                            JsonNode jsonContent = mapper.readTree(jsonSubstring);
                            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonContent);
                        } else {
                            return content;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                return DEFAULT_ERROR;
            }
            return "Error: " + errorMessage;
        }

        return DEFAULT_ERROR;
    }

    public static void main(String[] args) {
        String prompt = "Find Java developer positions with 5+ years of experience. " +
                "Provide at least 10 openings. Include a job_link field for each job, " +
                "which should be a plausible URL for the job posting. All jobs should be " +
                "from within the last month and must be from naukri.com.";

        String response = getAIResponse(prompt);
        System.out.println("AI Response:\n" + response);
    }
}
