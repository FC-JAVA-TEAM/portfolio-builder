package com.flexi.profile.dto;

import java.time.Instant;
import java.util.List;

public class RolePromotionResponse {
    private boolean success;
    private String message;
    private PromotedUserInfo promotedUser;
    private Instant promotedAt;
    private String promotedBy;

    public RolePromotionResponse() {}

    public RolePromotionResponse(boolean success, String message, PromotedUserInfo promotedUser, 
                              Instant promotedAt, String promotedBy) {
        this.success = success;
        this.message = message;
        this.promotedUser = promotedUser;
        this.promotedAt = promotedAt;
        this.promotedBy = promotedBy;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PromotedUserInfo getPromotedUser() {
        return promotedUser;
    }

    public void setPromotedUser(PromotedUserInfo promotedUser) {
        this.promotedUser = promotedUser;
    }

    public Instant getPromotedAt() {
        return promotedAt;
    }

    public void setPromotedAt(Instant promotedAt) {
        this.promotedAt = promotedAt;
    }

    public String getPromotedBy() {
        return promotedBy;
    }

    public void setPromotedBy(String promotedBy) {
        this.promotedBy = promotedBy;
    }

    // Inner class for promoted user info (without sensitive data)
    public static class PromotedUserInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private List<String> roles;
        private boolean isActive;

        public PromotedUserInfo() {}

        public PromotedUserInfo(Long id, String email, String firstName, String lastName, 
                               List<String> roles, boolean isActive) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
            this.isActive = isActive;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }
}
