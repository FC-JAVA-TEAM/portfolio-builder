# AIController Postman Testing Guide

## Overview
This guide provides comprehensive documentation for testing the AIController using the provided Postman collection and environment files.

## 📁 Files Included

### 1. Collection File
- **`AIController_Postman_Collection.json`** - Main Postman collection with all test scenarios

### 2. Environment File
- **`AIController_Postman_Environment.json`** - Environment variables for all test scenarios

## 🎯 Test Coverage

### Endpoints Tested
1. **GET /api/ai/health** - Health check functionality
2. **GET /api/ai/jobs** - AI-powered job search with multiple scenarios
3. **POST /api/ai/calendar/google-meet** - Google Calendar meeting generation

### Test Scenarios

#### ✅ **Health Check**
- Basic health monitoring with response validation
- Service status verification
- Response structure validation

#### ✅ **Job Search - Success Cases**
- **Java Developer Search**: Java, Spring Boot, Microservices skills
- **Python Developer Search**: Python, Django, Flask, FastAPI skills
- **Multiple Locations Search**: Geographic filtering across multiple cities

#### ❌ **Job Search - Error Handling**
- **Missing All Parameters**: Validation of required parameters
- **Missing Skills Parameter**: Specific parameter validation

#### 📅 **Google Calendar Integration**
- **Successful Meeting Generation**: Complete calendar request creation
- **Validation Error Handling**: Missing required fields testing

## 📊 Latest Test Results

### Test Execution Summary
- **Total Requests**: 8
- **Total Assertions**: 18
- **Passed Assertions**: 18 (100%)
- **Failed Assertions**: 0 (0%)
- **Execution Time**: 51 seconds
- **Success Rate**: 100%

### Performance Metrics
- **Fastest Response**: 6ms (error handling)
- **Slowest Response**: 16.6s (multiple locations job search)
- **Average Response**: 6.2s
- **Total Data Transferred**: 13.52kB

### Detailed Results
- ✅ **Health Check**: 47ms response time
- ✅ **Java Developer Search**: 16.1s (AI processing)
- ✅ **Python Developer Search**: 11.6s (AI processing)
- ✅ **Multiple Locations Search**: 16.6s (complex query)
- ✅ **Error Handling**: 6-8ms (fast error responses)
- ✅ **Google Calendar Success**: 5.7s (calendar generation)
- ✅ **Calendar Validation**: 8ms (validation error)

## 🚀 How to Use

### Prerequisites
- Postman application installed
- Newman CLI tool (optional, for command-line testing)
- AIController service running on localhost:8080

### Import Instructions

#### Using Postman GUI
1. Open Postman application
2. Click "Import" button
3. Select `AIController_Postman_Collection.json`
4. Import the environment file `AIController_Postman_Environment.json`
5. Select the "AIController Test Environment" from the environment dropdown
6. Run individual tests or the entire collection

#### Using Newman CLI
```bash
# Install Newman globally (if not already installed)
npm install -g newman

# Run the complete test suite
newman run AIController_Postman_Collection.json -e AIController_Postman_Environment.json --reporters cli

# Run with detailed HTML report
newman run AIController_Postman_Collection.json -e AIController_Postman_Environment.json --reporters cli,html --reporter-html-export test-report.html

# Run with JSON export for CI/CD
newman run AIController_Postman_Collection.json -e AIController_Postman_Environment.json --reporters cli,json --reporter-json-export test-results.json
```

## 🔧 Environment Variables

### Base Configuration
- **baseUrl**: `http://localhost:8080` - API base URL
- **timeout**: `30000` - Request timeout in milliseconds

### Skills Testing Variables
- **javaSkills**: `Java,Spring Boot,Microservices,REST API,MySQL`
- **pythonSkills**: `Python,Django,Flask,FastAPI,PostgreSQL`
- **frontendSkills**: `React,JavaScript,TypeScript,HTML,CSS,Node.js`
- **generalSkills**: `JavaScript,React,Node.js`
- **specialCharSkills**: `C++,C#,.NET,ASP.NET`

### Location Testing Variables
- **primaryLocation**: `Bangalore`
- **secondaryLocation**: `Mumbai`
- **tertiaryLocation**: `Delhi`
- **multipleLocations**: `Mumbai,Delhi,Bangalore,Hyderabad,Chennai`

### Experience Testing Variables
- **seniorExperience**: `5+ years`
- **midExperience**: `3 years`
- **juniorExperience**: `1-2 years`
- **experienceRange**: `2-5 years`

### Calendar Testing Variables
- **testDateTime**: `June 14, 2025, at 11:00 AM IST`
- **testJobId**: `Job ID 1045`
- **testInterviewType**: `Senior Backend Developer Interview`
- **testDuration**: `30 minutes`
- **testDescription**: `Discussion with shortlisted candidates`

## 📋 Test Scenarios Detail

### 1. Health Check Tests
```javascript
GET /api/ai/health
Expected: 200 OK, service status, response structure validation
```

### 2. Job Search Success Scenarios
```javascript
// Java developer search
GET /api/ai/jobs?skills=Java,Spring Boot,Microservices,REST API,MySQL&location=Bangalore&experience=5+ years
Expected: 200 OK, relevant Java jobs, proper data structure

// Python developer search
GET /api/ai/jobs?skills=Python,Django,Flask,FastAPI,PostgreSQL&location=Mumbai&experience=3 years
Expected: 200 OK, Python-related positions

// Multiple locations search
GET /api/ai/jobs?skills=JavaScript,React,Node.js&location=Mumbai,Delhi,Bangalore,Hyderabad,Chennai&experience=2-5 years
Expected: 200 OK, jobs from multiple cities
```

### 3. Error Handling Tests
```javascript
// Missing all parameters
GET /api/ai/jobs
Expected: 400/500 error, proper error structure

// Missing skills parameter
GET /api/ai/jobs?location=Bangalore&experience=5+ years
Expected: 400/500 error, skills parameter error message
```

### 4. Google Calendar Tests
```javascript
// Successful calendar request
POST /api/ai/calendar/google-meet
Body: {dateTime, jobId, interviewType, duration, description}
Expected: 200 OK, Google Calendar API request structure

// Missing required fields
POST /api/ai/calendar/google-meet
Body: {dateTime} (missing other fields)
Expected: 400/500 error, validation error message
```

## 🎯 Quality Metrics

### Response Time Standards
- **Health Check**: < 1 second ✅
- **Job Search**: < 30 seconds (AI processing) ✅
- **Calendar Generation**: < 10 seconds ✅
- **Error Responses**: < 1 second ✅

### Data Quality Validation
- **Response Structure**: Consistent API format across all endpoints ✅
- **Required Fields**: All job objects contain id, title, company, skills ✅
- **Data Types**: Arrays for skills, strings for text fields ✅
- **Content Relevance**: Skills match search criteria ✅

### Error Handling Standards
- **HTTP Status Codes**: Proper 200/400/500 codes ✅
- **Error Messages**: Descriptive error information ✅
- **Response Format**: Consistent error structure ✅
- **Response Time**: Fast error responses ✅

## 🔍 Troubleshooting

### Common Issues
1. **Service Not Running**: Ensure AIController is running on port 8080
2. **Newman Not Found**: Install Newman globally with `npm install -g newman`
3. **Timeout Errors**: Increase timeout in environment variables
4. **Network Issues**: Check localhost connectivity

### Debug Commands
```bash
# Check if service is running
curl http://localhost:8080/api/ai/health

# Test individual endpoint
curl "http://localhost:8080/api/ai/jobs?skills=Java&location=Bangalore&experience=5 years"

# Verbose Newman output
newman run AIController_Postman_Collection.json -e AIController_Postman_Environment.json --verbose
```

## 📈 Continuous Integration

### CI/CD Integration Example
```yaml
# GitHub Actions workflow
- name: Run AIController Tests
  run: |
    npm install -g newman
    newman run AIController_Postman_Collection.json \
      -e AIController_Postman_Environment.json \
      --reporters cli,json \
      --reporter-json-export ai-test-results.json
```

### Jenkins Pipeline Example
```groovy
stage('API Testing') {
    steps {
        sh 'npm install -g newman'
        sh 'newman run AIController_Postman_Collection.json -e AIController_Postman_Environment.json --reporters cli,junit --reporter-junit-export newman-results.xml'
    }
    post {
        always {
            junit 'newman-results.xml'
        }
    }
}
```

## 📝 Customization

### Adding New Test Cases
1. Open the collection in Postman
2. Duplicate an existing request
3. Modify the request parameters
4. Update test assertions as needed
5. Export the updated collection

### Modifying Environment Variables
1. Open the environment in Postman
2. Add/modify variables as needed
3. Update collection requests to use new variables
4. Export the updated environment

### Creating Different Environments
- **Development**: `http://localhost:8080`
- **Staging**: `https://staging-api.example.com`
- **Production**: `https://api.example.com`

## 🎉 Summary

This Postman collection provides comprehensive testing coverage for the AIController with:
- **100% test success rate**
- **Complete endpoint coverage**
- **Performance validation**
- **Error handling verification**
- **Easy CI/CD integration**

The collection is production-ready and can be used for:
- **Manual testing** during development
- **Automated testing** in CI/CD pipelines
- **Regression testing** before releases
- **Performance monitoring** in different environments

---
**Created**: June 13, 2025  
**Collection Version**: 1.0  
**API Version**: v1  
**Status**: ✅ READY FOR USE
