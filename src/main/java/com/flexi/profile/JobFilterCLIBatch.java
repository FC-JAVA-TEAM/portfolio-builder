package com.flexi.profile;


import java.io.File;
import java.util.*;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JobFilterCLIBatch {

    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String MODEL = "claude-3-7-sonnet";
    private static final String JOBS_FILE = "cloud-eng-obs-response3.json";
    private static final int BATCH_SIZE = 20;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            Map<String, String> filters = collectUserFilters(scanner);
            ObjectMapper mapper = new ObjectMapper();
            List<JsonNode> allJobs = mapper.readValue(new File(JOBS_FILE), new TypeReference<List<JsonNode>>() {});

            ArrayNode finalResults = mapper.createArrayNode();

            for (int i = 0; i < allJobs.size(); i += BATCH_SIZE) {
            	System.out.println("Processing batch starting at index: " + i);
                List<JsonNode> batch = allJobs.subList(i, Math.min(i + BATCH_SIZE, allJobs.size()));
                String batchJson = mapper.writeValueAsString(batch);
                String prompt = buildPrompt(filters, batchJson);

                String aiResponse = callAI(prompt);
                try {
                    JsonNode jsonNode = mapper.readTree(aiResponse);
                    if (jsonNode.isArray()) {
                        finalResults.addAll((ArrayNode) jsonNode);
                        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalResults));
                    } else {
                        System.err.println("⚠️ AI response is not an array. Skipping batch " + (i / BATCH_SIZE));
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error parsing batch response: " + e.getMessage());
                }
            }

            // Final output
            System.out.println("✅ Filtered Jobs:");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(finalResults));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> collectUserFilters(Scanner scanner) {
        Map<String, String> filters = new LinkedHashMap<>();
        System.out.println("Enter filters (leave blank to skip).");

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
        System.out.print("filter end\n ");

        return filters;
    }

    private static String buildPrompt(Map<String, String> filters, String jobsJsonArray) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are given a JSON array of job postings.\n");
        prompt.append("Return only those jobs that match ALL of the following:\n");

        filters.forEach((key, value) -> {
            prompt.append("- ").append(key).append(" equals or contains: ").append(value).append("\n");
        });

        prompt.append("Return only these fields:\n");
        prompt.append("id, date_posted, date_created, date_validthrough, jobTitle, salary_raw, company,\n");
        prompt.append("experienceRequired, locations_derived, linkedin_org_specialties (rename to 'skills'),\n");
        prompt.append("employment_type, source_type, postedTime, source, saveStatus, jobLink, description_text.\n");
        prompt.append("Output only a JSON array. No explanation.\n\n");
        prompt.append("Input:\n").append(jobsJsonArray);

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
            return "[]"; // return empty array string on failure
        }
    }

    private static ObjectNode buildRequestBody(ObjectMapper mapper, String prompt) {
        ObjectNode requestBody = mapper.createObjectNode();
        ArrayNode messages = mapper.createArrayNode();

        ObjectNode systemMessage = mapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant. Return JSON arrays only.");
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

            content = content.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```", "").trim();
            return content;
        }
        return "[]";
    }
}
