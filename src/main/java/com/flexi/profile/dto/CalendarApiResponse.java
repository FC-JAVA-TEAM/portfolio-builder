package com.flexi.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data Transfer Object representing a Google Calendar API response.
 * Contains the structured calendar event data extracted from AI response.
 * 
 * @author AI Calendar Service
 * @version 1.0
 */
public class CalendarApiResponse {

    /**
     * Summary/title of the calendar event
     */
    private String summary;

    /**
     * Description of the calendar event
     */
    private String description;

    /**
     * Start time information
     */
    private EventTime start;

    /**
     * End time information
     */
    private EventTime end;

    /**
     * Conference data for Google Meet integration
     */
    @JsonProperty("conferenceData")
    private ConferenceData conferenceData;

    /**
     * Reminder settings for the event
     */
    private Reminders reminders;

    /**
     * Default constructor
     */
    public CalendarApiResponse() {
    }

    // Getters and Setters

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventTime getStart() {
        return start;
    }

    public void setStart(EventTime start) {
        this.start = start;
    }

    public EventTime getEnd() {
        return end;
    }

    public void setEnd(EventTime end) {
        this.end = end;
    }

    public ConferenceData getConferenceData() {
        return conferenceData;
    }

    public void setConferenceData(ConferenceData conferenceData) {
        this.conferenceData = conferenceData;
    }

    public Reminders getReminders() {
        return reminders;
    }

    public void setReminders(Reminders reminders) {
        this.reminders = reminders;
    }

    /**
     * Inner class representing event time information
     */
    public static class EventTime {
        
        @JsonProperty("dateTime")
        private String dateTime;

        @JsonProperty("timeZone")
        private String timeZone;

        public EventTime() {
        }

        public EventTime(String dateTime, String timeZone) {
            this.dateTime = dateTime;
            this.timeZone = timeZone;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        @Override
        public String toString() {
            return "EventTime{" +
                    "dateTime='" + dateTime + '\'' +
                    ", timeZone='" + timeZone + '\'' +
                    '}';
        }
    }

    /**
     * Inner class representing conference data for Google Meet
     */
    public static class ConferenceData {
        
        @JsonProperty("createRequest")
        private CreateRequest createRequest;

        public ConferenceData() {
        }

        public CreateRequest getCreateRequest() {
            return createRequest;
        }

        public void setCreateRequest(CreateRequest createRequest) {
            this.createRequest = createRequest;
        }

        public static class CreateRequest {
            
            @JsonProperty("requestId")
            private String requestId;

            @JsonProperty("conferenceSolutionKey")
            private ConferenceSolutionKey conferenceSolutionKey;

            public CreateRequest() {
            }

            public String getRequestId() {
                return requestId;
            }

            public void setRequestId(String requestId) {
                this.requestId = requestId;
            }

            public ConferenceSolutionKey getConferenceSolutionKey() {
                return conferenceSolutionKey;
            }

            public void setConferenceSolutionKey(ConferenceSolutionKey conferenceSolutionKey) {
                this.conferenceSolutionKey = conferenceSolutionKey;
            }

            public static class ConferenceSolutionKey {
                
                private String type;

                public ConferenceSolutionKey() {
                }

                public ConferenceSolutionKey(String type) {
                    this.type = type;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                @Override
                public String toString() {
                    return "ConferenceSolutionKey{" +
                            "type='" + type + '\'' +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "CreateRequest{" +
                        "requestId='" + requestId + '\'' +
                        ", conferenceSolutionKey=" + conferenceSolutionKey +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ConferenceData{" +
                    "createRequest=" + createRequest +
                    '}';
        }
    }

    /**
     * Inner class representing reminder settings
     */
    public static class Reminders {
        
        @JsonProperty("useDefault")
        private Boolean useDefault;

        private List<ReminderOverride> overrides;

        public Reminders() {
        }

        public Boolean getUseDefault() {
            return useDefault;
        }

        public void setUseDefault(Boolean useDefault) {
            this.useDefault = useDefault;
        }

        public List<ReminderOverride> getOverrides() {
            return overrides;
        }

        public void setOverrides(List<ReminderOverride> overrides) {
            this.overrides = overrides;
        }

        public static class ReminderOverride {
            
            private String method;
            private Integer minutes;

            public ReminderOverride() {
            }

            public ReminderOverride(String method, Integer minutes) {
                this.method = method;
                this.minutes = minutes;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public Integer getMinutes() {
                return minutes;
            }

            public void setMinutes(Integer minutes) {
                this.minutes = minutes;
            }

            @Override
            public String toString() {
                return "ReminderOverride{" +
                        "method='" + method + '\'' +
                        ", minutes=" + minutes +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Reminders{" +
                    "useDefault=" + useDefault +
                    ", overrides=" + overrides +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CalendarApiResponse{" +
                "summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", conferenceData=" + conferenceData +
                ", reminders=" + reminders +
                '}';
    }
}
