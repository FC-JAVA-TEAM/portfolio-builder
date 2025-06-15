package com.flexi.profile;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.*;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

public class AIJobFilterProcessor_update_3 {

    private static final String API_URL = "https://api-beta.fuelix.ai/chat/completions";
    private static final String AUTH_TOKEN = "nHeX0UQumAogwKoOX9k6RSDrPDAyLGgTKoCMqYlinqGrSKLw";
    private static final String MODEL = "claude-3-7-sonnet";
    private static final String JOBS_FILE = "cloud-eng-obs-response3.json";
    
    static int count = 0;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tasmob?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String DB_USER = "root";

    private static final String DB_PASS = "Welcome@1992";

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

        System.out.printf("Loaded %d total jobs. Sending in batch to AI with keywords: %s%n%n",
                jobList.size(), keywords.isEmpty() ? "(no keywords, return all)" : keywords);

        processInBatchesWithStreamingOutput(jobList, keywords);
    }

    private static void processInBatchesWithStreamingOutput(List<JsonNode> jobList, List<String> keywords) throws Exception {
        int batchSize = 2;
        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        ObjectMapper mapper = new ObjectMapper();
        int batchCount = 0;

        for (int i = 0; i < jobList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, jobList.size());
            List<JsonNode> batch = jobList.subList(i, end);
            final int batchNum = ++batchCount;

            completionService.submit(() -> {
                try {
                    System.out.printf("➡️  [Thread %s] Starting batch %d with %d jobs%n",
                            Thread.currentThread().getName(), batchNum, batch.size());

                    String prompt = buildPrompt(batch, keywords);
                    String aiResponse = callAI(prompt);

                    System.out.printf("✅  [Thread %s] Completed batch %d%n", Thread.currentThread().getName(), batchNum);
                    return aiResponse;

                } catch (Exception e) {
                    System.err.printf("❌  [Thread %s] Failed batch %d: %s%n",
                            Thread.currentThread().getName(), batchNum, e.getMessage());
                    return "[]";
                }
            });
        }

        System.out.printf("🚀 Waiting for %d batches to complete...\n\n", batchCount);

        for (int i = 0; i < batchCount; i++) {
            Future<String> completed = completionService.take(); // wait for next completed batch
            String result = completed.get();
            System.out.println("📦 Processed Batch Output:\n" + result);
            
           try {

                JsonNode jobsArray = new ObjectMapper().readTree(result);

                if (jobsArray.isArray()) {

                    for (JsonNode job : jobsArray) {
                    	System.out.printf("Processing job ID: %s%n", job);

                        saveToDatabase(job);

                        System.out.printf("✅ Inserted job ID %s into DB%n", job.path("id").asText());

                    }

                }

            } catch (Exception e) {

                System.err.println("❌ Error saving batch to DB: " + e.getMessage());

            }
       
        }

        executor.shutdown();
    }

    private static String buildPrompt(List<JsonNode> jobs, List<String> keywords) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jobArray = mapper.createArrayNode();
        jobs.forEach(jobArray::add);

        String keywordStr = keywords.isEmpty() ? "(no keywords, return all)" : String.join(", ", keywords);

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
                - description_text

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
                   
                    		String dd = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cleaned);
                    		  return dd;
                } else {
                    return "⚠️ No valid JSON array found in content:\n" + content;
                }
            }
        } catch (Exception e) {
        	
        	count++;
        	System.err.println("❌ Error parsing AI response count: " + count);
            return "❌ Error parsing JSON response: " + e.getMessage();
        }
        return "❌ Invalid response format.";
    }
    
    private static void saveToDatabase(JsonNode job) throws Exception {

        String sql = "INSERT INTO jobs (id, date_posted, date_created, date_validthrough, jobTitle, salary_raw, company, " +

                "experienceRequired, locations_derived, skills, employment_type, source_type, postedTime, source, " +

                "saveStatus, jobLink, description_text) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";



        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);

             PreparedStatement stmt = conn.prepareStatement(sql)) {



            stmt.setString(1, job.path("id").asText(null));

            stmt.setString(2, job.path("date_posted").asText(null));

            stmt.setString(3, job.path("date_created").asText(null));

            stmt.setString(4, job.path("date_validthrough").asText(null));

            stmt.setString(5, job.path("jobTitle").asText(null));

            stmt.setString(6, job.path("salary_raw").asText(null));

            stmt.setString(7, job.path("company").asText(null));

            stmt.setString(8, job.path("experienceRequired").asText(null));

            //stmt.setString(9, job.path("locations_derived").toString());
            stmt.setString(9, job.path("locations_derived").toString());

            stmt.setString(10, job.path("skills").asText(null));
            

            stmt.setString(11, job.path("employment_type").asText(null));

            stmt.setString(12, job.path("source_type").asText(null));

            stmt.setString(13, job.path("postedTime").asText(null));

            stmt.setString(14, job.path("source").asText(null));

            stmt.setString(15, job.path("saveStatus").asText(null));

            stmt.setString(16, job.path("jobLink").asText(null));

            stmt.setString(17, job.path("description_text").asText(null));



            stmt.executeUpdate();

        }
    }
}
