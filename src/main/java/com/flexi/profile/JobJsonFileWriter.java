package com.flexi.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JobJsonFileWriter {

    public static void main(String[] args) throws IOException {
        // Simulate the API JSON response as a string (you can get this from HttpClient in real case)
        String jsonResponse = new String(Files.readAllBytes(Paths.get("jobs-response.json")));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jobsArray = mapper.readTree(jsonResponse);

        // Create a folder to store job files
        File folder = new File("jobs");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Iterate over each job in the array
        for (JsonNode jobNode : jobsArray) {
            String jobId = jobNode.get("id").asText();
            String filename = "jobs/job_" + jobId + ".json";

            // Write the raw JSON of the job into a file
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File(filename), jobNode);

            System.out.println("Saved: " + filename);
        }

        System.out.println("✅ All jobs saved successfully.");
    }
}
