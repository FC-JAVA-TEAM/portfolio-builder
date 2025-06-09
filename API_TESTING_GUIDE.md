# Complete API Testing Guide

This guide provides comprehensive testing for all Profile API endpoints including Authentication, Profile Management, Section Management, Admin Operations, and HR Management.

## 📁 Files Created

### **Postman Collection & Environment**
- `Complete_API_Testing.postman_collection.json` - Main testing collection
- `Complete_API_Testing.postman_environment.json` - Environment variables

### **Automated Testing Scripts**
- `run_complete_api_tests.bat` - Windows batch script
- `run_complete_api_tests.sh` - Linux/Mac shell script

### **Documentation**
- `API_TESTING_GUIDE.md` - This comprehensive guide

## 🚀 Quick Start

### **Option 1: Using Postman GUI**
1. Open Postman
2. Import `Complete_API_Testing.postman_collection.json`
3. Import `Complete_API_Testing.postman_environment.json`
4. Select the "Complete API Testing Environment"
5. Update the `baseUrl` variable to match your server (default: `http://localhost:8080`)
6. Run the collection or individual requests

### **Option 2: Using Newman (Command Line)**

#### **Windows:**
```bash
# Run the batch script
run_complete_api_tests.bat
```

#### **Linux/Mac:**
```bash
# Make script executable
chmod +x run_complete_api_tests.sh

# Run the script
./run_complete_api_tests.sh
```

#### **Manual Newman Command:**
```bash
newman run Complete_API_Testing.postman_collection.json \
    -e Complete_API_Testing.postman_environment.json \
    --reporters cli,html \
    --reporter-html-export test-results.html
```

## 📋 Test Collection Overview

### **1. Health Check**
- **Ping** - Basic health check endpoint

### **2. Authentication**
- **Register Admin** - Create admin user
- **Register User** - Create regular user
- **Login Admin** - Admin authentication (stores `adminToken`)
- **Login User** - User authentication (stores `accessToken`)
- **Check Auth Status** - Verify authentication status
- **Refresh Token** - Refresh access token
- **Logout** - User logout

### **3. Profile Management**
- **Create Profile** - Create new profile (stores `profileId`)
- **Get Profile** - Retrieve profile by ID
- **Get All Profiles** - List all profiles
- **Get My Profiles** - Get current user's profiles
- **Get Public Profiles** - List public profiles
- **Update Profile** - Modify existing profile
- **Delete Profile** - Remove profile

### **4. Section Management**
- **Create Section** - Add section to profile (stores `sectionId`)
- **Get Section** - Retrieve section by ID
- **Update Section** - Modify section
- **Delete Section** - Remove section
- **Get Sections by Profile** - List all sections for a profile

### **5. Admin Management** (Requires Admin Token)
- **Revoke User Tokens** - Revoke tokens for specific user
- **Revoke All Tokens** - Revoke all system tokens
- **Cleanup Expired Tokens** - Remove expired tokens

### **6. HR Management** (Requires Admin Token)
- **Promote User to HR** - Promote user to HR role

## 🔧 Environment Variables

The environment file includes these variables:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `baseUrl` | API base URL | `http://localhost:8080` |
| `adminToken` | Admin access token | Auto-set by login |
| `adminRefreshToken` | Admin refresh token | Auto-set by login |
| `accessToken` | User access token | Auto-set by login |
| `refreshToken` | User refresh token | Auto-set by login |
| `profileId` | Created profile ID | Auto-set by create |
| `sectionId` | Created section ID | Auto-set by create |
| `subsectionId` | Created subsection ID | Auto-set by create |
| `userId` | User ID for admin operations | Manual input required |

## 🧪 Test Automation Features

### **Automatic Token Management**
- Login requests automatically store tokens in environment variables
- Subsequent requests use stored tokens for authentication
- Separate token management for admin and regular users

### **Dynamic ID Storage**
- Created resources (profiles, sections) automatically store IDs
- Following requests use stored IDs for operations

### **Comprehensive Test Assertions**
- Status code validation
- Response structure validation
- Data integrity checks
- Authentication verification

## 📊 Test Results

### **Newman HTML Report**
When using Newman, an HTML report is generated as `test-results.html` containing:
- Test execution summary
- Pass/fail status for each test
- Response times
- Detailed error information
- Request/response data

### **Console Output**
Real-time test execution feedback including:
- Test names and status
- Assertion results
- Error messages
- Execution statistics

## 🔄 Test Execution Flow

### **Recommended Execution Order:**
1. **Health Check** - Verify API is running
2. **Register Admin** - Create admin account
3. **Register User** - Create regular user account
4. **Login Admin** - Get admin token
5. **Login User** - Get user token
6. **Profile Operations** - Test profile CRUD
7. **Section Operations** - Test section CRUD
8. **Admin Operations** - Test admin functions
9. **HR Operations** - Test HR functions

### **Dependencies:**
- Admin operations require admin token
- Profile operations require user token
- Section operations require existing profile
- Some operations depend on previously created resources

## 🛠️ Customization

### **Adding New Tests**
1. Open the collection in Postman
2. Add new request to appropriate folder
3. Configure request details (method, URL, headers, body)
4. Add test scripts in the "Tests" tab
5. Export updated collection

### **Modifying Environment**
1. Update environment variables as needed
2. Add new variables for additional endpoints
3. Export updated environment file

### **Custom Test Scripts**
Example test script for new endpoint:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has required fields", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('name');
});

// Store response data for subsequent requests
var jsonData = pm.response.json();
pm.environment.set("resourceId", jsonData.id);
```

## 🚨 Troubleshooting

### **Common Issues:**

#### **Newman Not Found**
```bash
# Install Newman globally
npm install -g newman
```

#### **Connection Refused**
- Ensure the API server is running
- Verify the `baseUrl` in environment variables
- Check firewall settings

#### **Authentication Failures**
- Ensure you run login requests first
- Check token expiration
- Verify user credentials

#### **Missing Dependencies**
- Run tests in recommended order
- Ensure prerequisite resources are created
- Check environment variable values

### **Debug Mode**
Run Newman with verbose output:
```bash
newman run Complete_API_Testing.postman_collection.json \
    -e Complete_API_Testing.postman_environment.json \
    --verbose
```

## 📈 Performance Testing

### **Load Testing with Newman**
```bash
# Run collection multiple times
newman run Complete_API_Testing.postman_collection.json \
    -e Complete_API_Testing.postman_environment.json \
    -n 10 \
    --delay-request 500
```

### **Monitoring Response Times**
- Check HTML report for response time analysis
- Use `--reporter-cli-no-summary` for detailed timing
- Monitor for performance degradation

## 🔒 Security Considerations

### **Token Security**
- Tokens are stored in environment variables
- Use secure environments in production
- Regularly rotate authentication tokens

### **Test Data**
- Use test-specific data
- Avoid production data in tests
- Clean up test resources after execution

## 📝 Best Practices

### **Test Organization**
- Group related tests in folders
- Use descriptive test names
- Include proper documentation

### **Error Handling**
- Test both success and failure scenarios
- Validate error responses
- Include edge cases

### **Maintenance**
- Keep tests updated with API changes
- Review and update test data regularly
- Monitor test execution results

## 🤝 Contributing

To contribute to the test suite:
1. Add new test cases for new endpoints
2. Update environment variables as needed
3. Ensure all tests pass
4. Update documentation
5. Export updated collection and environment files

---

**Note:** Ensure your API server is running before executing tests. The default configuration expects the server at `http://localhost:8080`.
