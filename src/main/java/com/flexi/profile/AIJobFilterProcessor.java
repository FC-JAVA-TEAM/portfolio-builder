package com.flexi.profile;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

public class AIJobFilterProcessor {

    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String MODEL = "claude-3-7-sonnet";
    private static final String JOBS_FILE = "cloud-eng-obs-response3.json";

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> jobList = mapper.readValue(new File(JOBS_FILE), new TypeReference<>() {});

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter keywords (e.g., Java, Data, Remote). Press enter to finish:");
        List<String> keywords = new ArrayList<>();

        while (true) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) break;
            keywords.add(line);
        }

        System.out.printf("Loaded %d total jobs. Sending in batch to AI with keywords: %s%n%n", jobList.size(), keywords);

        String prompt = buildPrompt(jobList, keywords);
        String response = callAI(prompt);
        System.out.println("AI Response:\n" + response);
    }

    private static String buildPrompt(List<JsonNode> jobs, List<String> keywords) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jobArray = mapper.createArrayNode();
        jobs.forEach(jobArray::add);

        String keywordStr = String.join(", ", keywords);

        return """
               I have a list of job postings in JSON array format.

               Please extract only those jobs where the `jobTitle` contains any of the following keywords: [%s].

               Return a JSON array of objects containing ONLY the following fields:
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

               Return only the final JSON array. Do not include any explanation or extra text.

               Jobs:
               %s
               """.formatted(keywordStr, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jobArray));
    }

    private static String callAI(String prompt) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);

        ObjectNode requestBody = mapper.createObjectNode();
        ArrayNode messages = mapper.createArrayNode();

        ObjectNode systemMsg = mapper.createObjectNode();
        systemMsg.put("role", "system");
        systemMsg.put("content", "Return only a JSON array of cleaned job objects with specified fields.");
        messages.add(systemMsg);

        ObjectNode userMsg = mapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);

        requestBody.set("messages", messages);
        requestBody.put("model", MODEL);
        requestBody.put("temperature", 0.1);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        return extractJsonArray(mapper, response.getBody());
    }

    private static String extractJsonArray(ObjectMapper mapper, String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode choices = root.path("choices");

            if (choices.isArray() && choices.size() > 0) {
                String content = choices.get(0).path("message").path("content").asText();
                int start = content.indexOf('[');
                int end = content.lastIndexOf(']');
                if (start != -1 && end > start) {
                    String jsonArray = content.substring(start, end + 1);
                    JsonNode cleaned = mapper.readTree(jsonArray);
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cleaned);
                } else {
                    return "No JSON array found in content:\n" + content;
                }
            }
        } catch (Exception e) {
            return "Error parsing JSON response: " + e.getMessage();
        }
        return "Invalid response format.";
    }
}
