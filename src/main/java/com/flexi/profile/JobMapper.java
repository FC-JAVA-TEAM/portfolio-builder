package com.flexi.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JobMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Job> mapJobsFromApiResponse(String apiResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            List<Job> jobs = new ArrayList<>();
            
            if (rootNode.isArray()) {
                for (JsonNode jobNode : rootNode) {
                	System.out.println("Processing job node: --------------------------------------" );
                    jobs.add(mapFromJson(jobNode));
                    System.out.println("Job processed: " + jobNode);
                    System.out.println("Processing job done: --------------------------------------" );
                    
                }
            }
            
            
            
            return jobs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

//    private static Job mapSingleJob(JsonNode jobNode) {
//        return Job.builder()
//            .jobId(getStringValue(jobNode, "id"))
//            .title(getStringValue(jobNode, "title"))
//            .companyName(getStringValue(jobNode, "organization"))
//            .companyLogo(getStringValue(jobNode, "organization_logo"))
//            .location(getFirstArrayElement(jobNode, "locations_derived"))
//            .country(getFirstArrayElement(jobNode, "countries_derived"))
//            .employmentType(getFirstArrayElement(jobNode, "employment_type"))
//            .jobUrl(getStringValue(jobNode, "url"))
//            .postedDate(getStringValue(jobNode, "date_posted"))
//            .validThrough(getStringValue(jobNode, "date_validthrough"))
//            .isRemote(getBooleanValue(jobNode, "remote_derived"))
//            .companyIndustry(getStringValue(jobNode, "linkedin_org_industry"))
//            .companySize(getStringValue(jobNode, "linkedin_org_size"))
//            .requiredSkills(getStringList(jobNode, "linkedin_org_specialties"))
//            .companyDescription(getStringValue(jobNode, "linkedin_org_description"))
//            .build();
//    }
//
//    private static String getStringValue(JsonNode node, String fieldName) {
//        JsonNode field = node.get(fieldName);
//        return (field != null && !field.isNull()) ? field.asText() : "";
//    }
//
//    private static boolean getBooleanValue(JsonNode node, String fieldName) {
//        JsonNode field = node.get(fieldName);
//        return (field != null && !field.isNull()) && field.asBoolean();
//    }
//
//    private static String getFirstArrayElement(JsonNode node, String fieldName) {
//        JsonNode arrayNode = node.get(fieldName);
//        if (arrayNode != null && arrayNode.isArray() && arrayNode.size() > 0) {
//            return arrayNode.get(0).asText();
//        }
//        return "";
//    }
//
//    private static List<String> getStringList(JsonNode node, String fieldName) {
//        JsonNode arrayNode = node.get(fieldName);
//        if (arrayNode != null && arrayNode.isArray()) {
//            return StreamSupport.stream(arrayNode.spliterator(), false)
//                .map(JsonNode::asText)
//                .collect(Collectors.toList());
//        }
//        return new ArrayList<>();
//    }
    
    public static Job mapFromJson(JsonNode node) {
        return Job.builder()
            .jobId(getStringValue(node, "id"))
            .title(getStringValue(node, "title"))
            .companyName(getStringValue(node, "organization"))
            .companyLogo(getStringValue(node, "organization_logo"))
            .location(getFirstArrayElement(node, "locations_derived"))
            .country(getFirstArrayElement(node, "countries_derived"))
            .employmentType(getFirstArrayElement(node, "employment_type"))
            .jobUrl(getStringValue(node, "url"))
            .postedDate(getStringValue(node, "date_posted"))
            .validThrough(getStringValue(node, "date_validthrough"))
            .isRemote(getBooleanValue(node, "remote_derived"))
            .companyIndustry(getStringValue(node, "linkedin_org_industry"))
            .companySize(getStringValue(node, "linkedin_org_size"))
            .requiredSkills(getStringList(node, "linkedin_org_specialties"))
            .companyDescription(getStringValue(node, "linkedin_org_description"))
            .build();
    }

    private static String getStringValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : "";
    }

    private static boolean getBooleanValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) && field.asBoolean();
    }

    private static String getFirstArrayElement(JsonNode node, String fieldName) {
        JsonNode arrayNode = node.get(fieldName);
        if (arrayNode != null && arrayNode.isArray() && arrayNode.size() > 0) {
            return arrayNode.get(0).asText();
        }
        return "";
    }

    private static List<String> getStringList(JsonNode node, String fieldName) {
        JsonNode arrayNode = node.get(fieldName);
        if (arrayNode != null && arrayNode.isArray()) {
            return StreamSupport.stream(arrayNode.spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

