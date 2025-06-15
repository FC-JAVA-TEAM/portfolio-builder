package com.flexi.profile;


import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

public class OptimizedJobProcessor {

    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String MODEL = "claude-3-7-sonnet";
    private static final String JOBS_FILE = "test.json";
    private static final int THREAD_COUNT = 5;

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> jobList = mapper.readValue(new File(JOBS_FILE), new TypeReference<List<JsonNode>>() {});

        // Step 1: Take dynamic filters from command line
        Scanner scanner = new Scanner(System.in);
        Map<String, String> filters = new HashMap<>();

        System.out.println("Enter filters (e.g., jobTitle=Java Developer), blank line to finish:");
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) break;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                filters.put(parts[0].trim(), parts[1].trim());
            }
        }

        // Step 2: Apply filters
        List<JsonNode> filteredJobs = jobList.stream()
            .filter(job -> matchesFilters(job, filters))
            .collect(Collectors.toList());

        System.out.println("Filtered " + filteredJobs.size() + " jobs. Sending requests...");

        // Step 3: Process in parallel
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<String>> futures = new ArrayList<>();

        for (JsonNode jobNode : filteredJobs) {
            futures.add(executor.submit(() -> {
                try {
                    String prompt = buildPrompt(jobNode.toString());
                    return callAI(prompt);
                } catch (Exception e) {
                    return "Error processing job: " + e.getMessage();
                }
            }));
        }

        executor.shutdown();

        for (Future<String> future : futures) {
            try {
                System.out.println("AI Response:\n" + future.get());
            } catch (Exception e) {
                System.err.println("Execution error: " + e.getMessage());
            }
        }
    }

    private static boolean matchesFilters(JsonNode job, Map<String, String> filters) {
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey().toLowerCase();
            String value = entry.getValue().toLowerCase();

            if (key.equals("text")) {
                // Match in title OR description
                String title = job.has("jobTitle") ? job.get("jobTitle").asText("").toLowerCase() : "";
                String description = job.has("linkedin_org_description") ? job.get("linkedin_org_description").asText("").toLowerCase() : "";
                if (!(title.contains(value) || description.contains(value))) {
                    return false;
                }
            } else {
                JsonNode field = job.get(entry.getKey());
                if (field == null || !field.asText().toLowerCase().contains(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String buildPrompt(String jobJson) {
        return "Extract these fields as JSON: id, date_posted, date_created, date_validthrough, jobTitle, salary_raw, company, experienceRequired, locations_derived, linkedin_org_specialties as skills, employment_type, source_type, postedTime, source, saveStatus, jobLink, description_text. Return only the JSON. Input:\n" + jobJson;
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
        systemMsg.put("content", "Return only a JSON object with the requested fields.");
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

        return extractJsonResponse(mapper, response.getBody());
    }

    private static String extractJsonResponse(ObjectMapper mapper, String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode choices = root.path("choices");

            if (choices.isArray() && choices.size() > 0) {
                String content = choices.get(0).path("message").path("content").asText();
                int start = content.indexOf('{');
                int end = content.lastIndexOf('}');
                if (start != -1 && end > start) {
                    String json = content.substring(start, end + 1);
                    JsonNode cleaned = mapper.readTree(json);
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cleaned);
                } else {
                    return "No JSON found in content:\n" + content;
                }
            }
        } catch (Exception e) {
            return "Error parsing JSON response: " + e.getMessage();
        }
        return "Invalid response format.";
    }
}

