package com.flexi.profile;


import java.io.File;
import java.util.*;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JobFilterCLI {

    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String MODEL = "claude-3-7-sonnet";
    private static final String JOBS_FILE = "jobs-response2.json";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            Map<String, String> filters = collectUserFilters(scanner);

            ObjectMapper mapper = new ObjectMapper();
            List<JsonNode> allJobs = mapper.readValue(new File(JOBS_FILE), new TypeReference<List<JsonNode>>() {});

            String jobsJsonArray = mapper.writeValueAsString(allJobs);
            String prompt = buildPrompt(filters, jobsJsonArray);

            String aiResponse = callAI(prompt);
            System.out.println("Filtered Jobs:\n" + aiResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> collectUserFilters(Scanner scanner) {
        Map<String, String> filters = new LinkedHashMap<>();
        System.out.println("Enter filters (leave blank to skip). Example: Java Developer, Mumbai, etc.");

        System.out.print("Filter by jobTitle: ");
        String jobTitle = scanner.nextLine().trim();
        if (!jobTitle.isEmpty()) filters.put("jobTitle", jobTitle);

        System.out.print("Filter by location (locations_derived): ");
        String location = scanner.nextLine().trim();
        if (!location.isEmpty()) filters.put("locations_derived", location);

        System.out.print("Filter by company (organization): ");
        String company = scanner.nextLine().trim();
        if (!company.isEmpty()) filters.put("company", company);

        System.out.print("Filter by employment_type: ");
        String empType = scanner.nextLine().trim();
        if (!empType.isEmpty()) filters.put("employment_type", empType);

        return filters;
    }

    private static String buildPrompt(Map<String, String> filters, String jobsJsonArray) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are given a list of job postings in JSON array format.\n");
        prompt.append("Extract only those jobs that match ALL of the following filters:\n");

        filters.forEach((key, value) -> {
            prompt.append("- ").append(key).append(" contains or equals: ").append(value).append("\n");
        });

        prompt.append("For each matched job, return only the following fields:\n");
        prompt.append("id, date_posted or datePosted, date_created, date_validthrough, jobTitle, salary_raw, company,\n");
        prompt.append("experienceRequired, locations_derived, linkedin_org_specialties (rename to 'skills'),\n");
        prompt.append("employment_type, source_type, postedTime, source, saveStatus, jobLink, linkedin_org_description.\n");
        prompt.append("Return a valid JSON array with no explanation or markdown.\n\n");
        prompt.append("Here is the input:\n\n").append(jobsJsonArray);

        return prompt.toString();
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
        systemMessage.put("content", "You are a smart assistant. Return clean JSON arrays only.");
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
            String content = choices.get(0).path("message").path("content").asText()
            		;

            // Clean up markdown wrappers if present
            content = content.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```", "").trim();

            try {
                JsonNode json = mapper.readTree(content);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            } catch (Exception ex) {
                return "⚠️ Couldn't parse response as JSON:\n" + content;
            }
        }

        return "Invalid AI response.";
    }
}
