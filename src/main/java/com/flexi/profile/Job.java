package com.flexi.profile;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Job {
    private String jobId;                  // from "id"
    private String title;                  // from "title"
    private String companyName;            // from "organization"
    private String companyLogo;            // from "organization_logo"
    private String location;               // from "locations_derived[0]"
    private String country;                // from "countries_derived[0]"
    private String employmentType;         // from "employment_type[0]"
    private String jobUrl;                 // from "url"
    private String postedDate;             // from "date_posted"
    private String validThrough;           // from "date_validthrough"
    private boolean isRemote;              // from "remote_derived"
    private String companyIndustry;        // from "linkedin_org_industry"
    private String companySize;            // from "linkedin_org_size"
    private List<String> requiredSkills;   // from "linkedin_org_specialties"
    private String companyDescription;     // from "linkedin_org_description"
}