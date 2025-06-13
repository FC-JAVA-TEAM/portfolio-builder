# Google Calendar API Integration Documentation

## Overview

This document describes the new Google Calendar API integration that allows you to generate Google Calendar meeting requests using AI through the Fuelix API.

## New Endpoint

### POST `/api/ai/calendar/google-meet`

Generates a Google Calendar API request for scheduling Google Meet interviews with dynamic parameters.

#### Request Body

```json
{
  "dateTime": "June 14, 2025, at 11:00 AM IST",
  "jobId": "Job ID 1045",
  "interviewType": "Senior Backend Developer Interview",
  "duration": "30 minutes",
  "description": "Discussion with shortlisted candidates"
}
```

#### Request Body Parameters

| Parameter | Type | Required | Max Length | Description |
|-----------|------|----------|------------|-------------|
| `dateTime` | String | Yes | 100 | Date and time for the meeting (e.g., "June 14, 2025, at 11:00 AM IST") |
| `jobId` | String | Yes | 50 | Job identifier (e.g., "Job ID 1045") |
| `interviewType` | String | Yes | 200 | Type of interview (e.g., "Senior Backend Developer Interview") |
| `duration` | String | Yes | 50 | Duration of the meeting (e.g., "30 minutes") |
| `description` | String | Yes | 500 | Description of the meeting (e.g., "Discussion with shortlisted candidates") |

#### Response

**Success Response (200 OK):**

```json
{
  "status": "success",
  "message": "Google Calendar request generated successfully",
  "data": {
    "summary": "Job ID 1045: Senior Backend Developer Interview",
    "description": "Discussion with shortlisted candidates",
    "start": {
      "dateTime": "2025-06-14T11:00:00+05:30",
      "timeZone": "Asia/Kolkata"
    },
    "end": {
      "dateTime": "2025-06-14T11:30:00+05:30",
      "timeZone": "Asia/Kolkata"
    },
    "conferenceData": {
      "createRequest": {
        "requestId": "job-1045-backend-interview",
        "conferenceSolutionKey": {
          "type": "hangoutsMeet"
        }
      }
    },
    "reminders": {
      "useDefault": false,
      "overrides": [
        {
          "method": "email",
          "minutes": 60
        },
        {
          "method": "popup",
          "minutes": 15
        }
      ]
    }
  },
  "error": null,
  "requestId": "uuid-string",
  "timestamp": "2025-06-13T15:52:42.123456"
}
```

**Error Response (400 Bad Request):**

```json
{
  "status": "error",
  "message": "Invalid calendar request parameters: Date and time cannot be null or empty",
  "data": null,
  "error": "Validation error details",
  "requestId": "uuid-string",
  "timestamp": "2025-06-13T15:52:42.123456"
}
```

**Error Response (500 Internal Server Error):**

```json
{
  "status": "error",
  "message": "Google Calendar request generation failed: AI service error",
  "data": null,
  "error": "Service error details",
  "requestId": "uuid-string",
  "timestamp": "2025-06-13T15:52:42.123456"
}
```

## Usage Examples

### cURL Example

```bash
curl --location 'http://localhost:8080/api/ai/calendar/google-meet' \
--header 'Content-Type: application/json' \
--data '{
  "dateTime": "June 14, 2025, at 11:00 AM IST",
  "jobId": "Job ID 1045",
  "interviewType": "Senior Backend Developer Interview",
  "duration": "30 minutes",
  "description": "Discussion with shortlisted candidates"
}'
```

### JavaScript/Fetch Example

```javascript
const response = await fetch('http://localhost:8080/api/ai/calendar/google-meet', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    dateTime: "June 14, 2025, at 11:00 AM IST",
    jobId: "Job ID 1045",
    interviewType: "Senior Backend Developer Interview",
    duration: "30 minutes",
    description: "Discussion with shortlisted candidates"
  })
});

const result = await response.json();
console.log(result);
```

### Python Example

```python
import requests
import json

url = "http://localhost:8080/api/ai/calendar/google-meet"
payload = {
    "dateTime": "June 14, 2025, at 11:00 AM IST",
    "jobId": "Job ID 1045",
    "interviewType": "Senior Backend Developer Interview",
    "duration": "30 minutes",
    "description": "Discussion with shortlisted candidates"
}

response = requests.post(url, json=payload)
result = response.json()
print(json.dumps(result, indent=2))
```

## How It Works

1. **Input Processing**: The API accepts dynamic parameters for meeting details
2. **AI Prompt Generation**: Creates a dynamic prompt using the provided parameters
3. **Fuelix API Call**: Sends the prompt to the Fuelix AI API using the configured credentials
4. **Response Parsing**: Extracts JSON data from the AI response content (handles markdown formatting)
5. **Structured Response**: Returns a properly structured Google Calendar API request body

## Key Features

- **Dynamic Parameters**: All meeting details can be customized
- **AI-Powered**: Uses Claude 3.7 Sonnet model via Fuelix API
- **Google Meet Integration**: Automatically includes Google Meet conference data
- **Validation**: Comprehensive input validation with meaningful error messages
- **Consistent API**: Follows the same response pattern as other AI endpoints
- **Comprehensive Testing**: Full test coverage including edge cases

## Configuration

The API uses the following configuration from `application.properties`:

```properties
# Fuelix AI API Configuration
fuelix.api.url=https://api-beta.fuelix.ai/chat/completions
fuelix.api.token=your-api-token
fuelix.api.model=claude-3-7-sonnet
fuelix.api.temperature=0.1
fuelix.api.timeout=30000
```

## Error Handling

The API handles various error scenarios:

- **Validation Errors**: Missing or invalid input parameters
- **AI Service Errors**: Issues with the Fuelix API
- **JSON Parsing Errors**: Problems parsing the AI response
- **Network Errors**: Connection issues with external services

All errors are returned with appropriate HTTP status codes and descriptive error messages.

## Testing

Run the tests to verify the implementation:

```bash
mvn test -Dtest=AIControllerTest
```

The test suite includes:
- Successful calendar request generation
- Validation error handling
- Service exception handling
- Invalid JSON handling
- Empty request body handling

## Integration with Google Calendar

The generated response can be used directly with the Google Calendar API:

```bash
curl --location 'https://www.googleapis.com/calendar/v3/calendars/primary/events?conferenceDataVersion=1' \
--header 'Authorization: Bearer YOUR_OAUTH_TOKEN' \
--header 'Content-Type: application/json' \
--data '[GENERATED_RESPONSE_DATA]'
```

Make sure to include the `conferenceDataVersion=1` parameter to enable Google Meet integration.
