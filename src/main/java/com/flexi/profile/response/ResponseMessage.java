package com.flexi.profile.response;

public enum ResponseMessage {
    // Role messages
    USER_REQUESTS_RETRIEVED("User role requests retrieved successfully"),
    ROLE_REQUEST_CREATED("Role request created successfully"),
    ROLE_REQUEST_APPROVED("Role request approved successfully"),
    ROLE_REQUEST_REJECTED("Role request rejected successfully"),
    PENDING_REQUESTS_RETRIEVED("Pending role requests retrieved successfully"),

    // Job Application messages
    APPLICATION_SUBMITTED("Job application submitted successfully"),
    APPLICATION_STATUS_UPDATED("Job application status updated successfully"),
    APPLICATIONS_RETRIEVED("Job applications retrieved successfully"),
    APPLICATION_RETRIEVED("Job application retrieved successfully"),
    JOB_APPLICATIONS_RETRIEVED("Job applications for posting retrieved successfully"),
    STATUS_APPLICATIONS_RETRIEVED("Job applications with status retrieved successfully"),

    // Job Posting messages
    JOB_POSTING_CREATED("Job posting created successfully"),
    JOB_POSTING_UPDATED("Job posting updated successfully"),
    JOB_POSTING_DELETED("Job posting deleted successfully"),
    JOB_POSTINGS_RETRIEVED("Job postings retrieved successfully"),
    JOB_POSTING_RETRIEVED("Job posting retrieved successfully"),
    STATUS_JOB_POSTINGS_RETRIEVED("Job postings with status retrieved successfully"),
    DEPARTMENT_JOB_POSTINGS_RETRIEVED("Job postings for department retrieved successfully"),
    EMPLOYMENT_TYPE_JOB_POSTINGS_RETRIEVED("Job postings for employment type retrieved successfully");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
