package com.flexi.profile;

import java.net.http.HttpClient;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class AIService2 {
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
            
            // Add system message
            ObjectNode systemMessage = mapper.createObjectNode();
            systemMessage.put("role", "system");
         
            
            
            systemMessage.put("content", 
            		"[\n    {\n        \"id\": \"1733886661\",\n        \"date_posted\": \"2025-06-12T14:11:01\",\n        \"date_created\": \"2025-06-12T14:24:22.749183\",\n        \"title\": \"Java Developer - C11 - PUNE\",\n        \"organization\": \"Citi\",\n        \"organization_url\": \"https://www.linkedin.com/company/citi\",\n        \"date_validthrough\": \"2025-07-30T00:25:06\",\n        \"locations_raw\": [\n            {\n                \"@type\": \"Place\",\n                \"address\": {\n                    \"@type\": \"PostalAddress\",\n                    \"addressCountry\": \"IN\",\n                    \"addressLocality\": \"Poona\",\n                    \"addressRegion\": null,\n                    \"streetAddress\": null\n                },\n                \"latitude\": 18.528904,\n                \"longitude\": 73.87435\n            }\n        ],\n        \"location_type\": null,\n        \"location_requirements_raw\": null,\n        \"salary_raw\": null,\n        \"employment_type\": [\n            \"FULL_TIME\"\n        ],\n        \"url\": \"https://in.linkedin.com/jobs/view/java-developer-c11-pune-at-citi-4218586321\",\n        \"source_type\": \"jobboard\",\n        \"source\": \"linkedin\",\n        \"source_domain\": \"in.linkedin.com\",\n        \"organization_logo\": \"https://media.licdn.com/dms/image/v2/D4E0BAQFgF4xtqyXBcg/company-logo_200_200/company-logo_200_200/0/1719257270317/citi_logo?e=2147483647&v=beta&t=T_6i1dMnSZi57cf_iBkNh8LOps-zfc6qG4e6AueXkSI\",\n        \"cities_derived\": [\n            \"Pune\"\n        ],\n        \"regions_derived\": [\n            \"Mahārāshtra\"\n        ],\n        \"countries_derived\": [\n            \"India\"\n        ],\n        \"locations_derived\": [\n            \"Pune, Mahārāshtra, India\"\n        ],\n        \"timezones_derived\": [\n            \"Asia/Kolkata\"\n        ],\n        \"lats_derived\": [\n            18.5203\n        ],\n        \"lngs_derived\": [\n            73.8567\n        ],\n        \"remote_derived\": false,\n        \"recruiter_name\": null,\n        \"recruiter_title\": null,\n        \"recruiter_url\": null,\n        \"linkedin_org_employees\": 195224,\n        \"linkedin_org_url\": \"http://www.citigroup.com\",\n        \"linkedin_org_size\": \"10,001+ employees\",\n        \"linkedin_org_slogan\": null,\n        \"linkedin_org_industry\": \"Financial Services\",\n        \"linkedin_org_followers\": 4658689,\n        \"linkedin_org_headquarters\": \"New York, New York\",\n        \"linkedin_org_type\": \"Public Company\",\n        \"linkedin_org_foundeddate\": \"1812\",\n        \"linkedin_org_specialties\": [\n            \"Banking\",\n            \"Commercial Banking\",\n            \"Investment Banking\",\n            \"Wealth Management\",\n            \"Credit Cards\",\n            \"Capital Markets\",\n            \"Equity and Fixed Income Research\",\n            \"Consumer Lending\",\n            \"Cash Management\",\n            \"and Transaction Services\"\n        ],\n        \"linkedin_org_locations\": [\n            \"388 Greenwich Street, New York, New York 10013, US\",\n            \"Locations in over 100 countries, http://www.citigroup.com/citi/global/index.htm, 10022, US\",\n            \"5900 Hurontario St, Mississauga, ON L5R 0B8, CA\",\n            \"165 Jalan Ampang, Kuala Lumpur, Federal Territory of Kuala Lumpur 50450, MY\",\n            \"Calle 62C O, Panama City, Panama, PA\",\n            \"11060 Grader St, Dallas, TX 75238, US\",\n            \"Avenida Canaval Moreyra 498, San Isidro, Lima 15047, PE\",\n            \"153 E 53rd St, New York, NY 10022, US\",\n            \"Roberto Medellin 800, Alvaro Obregon, CDMX 01376, MX\",\n            \"34th St, Taguig City, National Capital Region, PH\",\n            \"3 Garden Rd, Central & Western, Hong Kong, HK\",\n            \"Financial Center Road, Dubai, Dubai, AE\",\n            \"33 Canada Square, London, England E14 5, GB\",\n            \"Carrera 9A 99-2, Bogota, Bogota, D.C. 110221, CO\",\n            \"Bandra Kurla Complex Road, Mumbai, Maharashtra 400051, IN\",\n            \"Arlozorov 111, Tel Aviv-Yafo, Tel Aviv 62098, IL\",\n            \"No. 1, Song Zhi Rd., Taipei City, Taipei City 110, TW\",\n            \"C P Ramaswamy Road, Chennai, Tamil Nadu 600018, IN\",\n            \"14000 Citicards Way, Jacksonville, FL 32258, US\"\n        ],\n        \"linkedin_org_description\": \"Citi's mission is to serve as a trusted partner to our clients by responsibly providing financial services that enable growth and economic progress. Our core activities are safeguarding assets, lending money, making payments and accessing the capital markets on behalf of our clients. We have over 200 years of experience helping our clients meet the world's toughest challenges and embrace its greatest opportunities. We are Citi, the global bank – an institution connecting millions of people across hundreds of countries and cities.\\n\\nFor information on Citi’s commitment to privacy, visit on.citi/privacy.\",\n        \"linkedin_org_recruitment_agency_derived\": false,\n        \"seniority\": \"No corresponde\",\n        \"directapply\": false,\n        \"linkedin_org_slug\": \"citi\"\n    }, jive me json object includes srouce, employment_type, url as a fields"
            	);



            messages.add(systemMessage);
            
            // Add user message
            ObjectNode userMessage = mapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.set("messages", messages);
            requestBody.put("model", "claude-3-7-sonnet");
            requestBody.put("temperature", 0.1);

            // Print request body for debugging
            System.out.println("Request Body: " + requestBody.toString());

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // Print raw response for debugging
            System.out.println("Raw Response: " + response.getBody());


            JsonNode responseBody = mapper.readTree(response.getBody());
            if (responseBody.has("choices") && responseBody.get("choices").isArray()) {
                JsonNode choices = responseBody.get("choices");
                if (choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null && message.has("content")) {
                        String content = message.get("content").asText();
                        // Remove markdown code block markers
                        content = content.replaceAll("```json\n", "").replaceAll("\n```", "");
                        // Pretty print the JSON
                        JsonNode jsonContent = mapper.readTree(content);
                        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonContent);
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
		return prompt;
    }


	public static void main(String[] args) {
	        String prompt = "Give me only id,date_posted,date_created,date_validthrough,jobTitle, salary_raw,company, experienceRequired, locations_derived, linkedin_org_specialties as skills,employment_type,source_type, postedTime, source, saveStatus, jobLink,linkedin_org_description\n"
	        		+ " in json format ";
	        String response = getAIResponse(prompt);
	        System.err.println(response);
//
	}

}
