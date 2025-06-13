package com.flexi.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object representing a request to the Fuelix AI API.
 * Contains the message structure and model configuration for job search requests.
 * 
 * @author AI Job Service
 * @version 1.0
 */
public class FuelixApiRequest {

    /**
     * List of messages for the conversation with the AI model
     */
    @NotNull(message = "Messages list cannot be null")
    private List<Message> messages;

    /**
     * AI model to use for the request
     */
    @NotBlank(message = "Model cannot be blank")
    private String model;

    /**
     * Temperature setting for response randomness (0.0 to 1.0)
     */
    private Double temperature;

    /**
     * Default constructor
     */
    public FuelixApiRequest() {
    }

    /**
     * Constructor with essential fields
     * 
     * @param messages the conversation messages
     * @param model the AI model to use
     * @param temperature the response randomness setting
     */
    public FuelixApiRequest(List<Message> messages, String model, Double temperature) {
        this.messages = messages;
        this.model = model;
        this.temperature = temperature;
    }

    // Getters and Setters

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    /**
     * Inner class representing a message in the conversation
     */
    public static class Message {
        
        /**
         * Role of the message sender (system, user, assistant)
         */
        @NotBlank(message = "Message role cannot be blank")
        private String role;

        /**
         * Content of the message
         */
        @NotBlank(message = "Message content cannot be blank")
        private String content;

        /**
         * Default constructor
         */
        public Message() {
        }

        /**
         * Constructor with role and content
         * 
         * @param role the message role
         * @param content the message content
         */
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        // Getters and Setters

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "role='" + role + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FuelixApiRequest{" +
                "messages=" + messages +
                ", model='" + model + '\'' +
                ", temperature=" + temperature +
                '}';
    }
}
