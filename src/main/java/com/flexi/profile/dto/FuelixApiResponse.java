package com.flexi.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data Transfer Object representing a response from the Fuelix AI API.
 * Contains the complete response structure including choices, usage, and metadata.
 * 
 * @author AI Job Service
 * @version 1.0
 */
public class FuelixApiResponse {

    /**
     * Unique identifier for the API response
     */
    private String id;

    /**
     * Timestamp when the response was created
     */
    private Long created;

    /**
     * AI model used for generating the response
     */
    private String model;

    /**
     * Object type (typically "chat.completion")
     */
    private String object;

    /**
     * System fingerprint for the response
     */
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    /**
     * List of response choices from the AI model
     */
    private List<Choice> choices;

    /**
     * Usage statistics for the API call
     */
    private Usage usage;

    /**
     * Default constructor
     */
    public FuelixApiResponse() {
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getSystemFingerprint() {
        return systemFingerprint;
    }

    public void setSystemFingerprint(String systemFingerprint) {
        this.systemFingerprint = systemFingerprint;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    /**
     * Inner class representing a choice in the API response
     */
    public static class Choice {
        
        /**
         * Reason why the generation finished
         */
        @JsonProperty("finish_reason")
        private String finishReason;

        /**
         * Index of this choice in the choices array
         */
        private Integer index;

        /**
         * Message content from the AI model
         */
        private Message message;

        /**
         * Default constructor
         */
        public Choice() {
        }

        // Getters and Setters

        public String getFinishReason() {
            return finishReason;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Choice{" +
                    "finishReason='" + finishReason + '\'' +
                    ", index=" + index +
                    ", message=" + message +
                    '}';
        }
    }

    /**
     * Inner class representing a message in the API response
     */
    public static class Message {
        
        /**
         * Content of the message from the AI model
         */
        private String content;

        /**
         * Role of the message sender
         */
        private String role;

        /**
         * Tool calls made by the AI (if any)
         */
        @JsonProperty("tool_calls")
        private Object toolCalls;

        /**
         * Function call made by the AI (if any)
         */
        @JsonProperty("function_call")
        private Object functionCall;

        /**
         * Default constructor
         */
        public Message() {
        }

        // Getters and Setters

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Object getToolCalls() {
            return toolCalls;
        }

        public void setToolCalls(Object toolCalls) {
            this.toolCalls = toolCalls;
        }

        public Object getFunctionCall() {
            return functionCall;
        }

        public void setFunctionCall(Object functionCall) {
            this.functionCall = functionCall;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "content='" + content + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }

    /**
     * Inner class representing usage statistics for the API call
     */
    public static class Usage {
        
        /**
         * Number of tokens used in the completion
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * Number of tokens used in the prompt
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * Total number of tokens used
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;

        /**
         * Default constructor
         */
        public Usage() {
        }

        // Getters and Setters

        public Integer getCompletionTokens() {
            return completionTokens;
        }

        public void setCompletionTokens(Integer completionTokens) {
            this.completionTokens = completionTokens;
        }

        public Integer getPromptTokens() {
            return promptTokens;
        }

        public void setPromptTokens(Integer promptTokens) {
            this.promptTokens = promptTokens;
        }

        public Integer getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }

        @Override
        public String toString() {
            return "Usage{" +
                    "completionTokens=" + completionTokens +
                    ", promptTokens=" + promptTokens +
                    ", totalTokens=" + totalTokens +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FuelixApiResponse{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", choices=" + choices +
                ", usage=" + usage +
                '}';
    }
}
