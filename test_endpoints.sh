#!/bin/bash

# Base URL
BASE_URL="http://localhost:2222"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

# Function to print response
print_response() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ $2${NC}"
    else
        echo -e "${RED}✗ $2${NC}"
    fi
}

# 1. Register admin user
echo "Testing Auth Controller..."
ADMIN_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" \
    -d '{"email":"admin@example.com", "password":"admin123", "firstName":"Admin", "lastName":"User", "role":"ADMIN"}' \
    $BASE_URL/api/auth/register)
ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | jq -r '.accessToken')
print_response $? "Register admin user"

# 2. Register regular user
USER_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" \
    -d '{"email":"user@example.com", "password":"password123", "firstName":"Test", "lastName":"User"}' \
    $BASE_URL/api/auth/register)
USER_TOKEN=$(echo $USER_RESPONSE | jq -r '.accessToken')
print_response $? "Register regular user"

# 3. Test login
LOGIN_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" \
    -d '{"email":"user@example.com", "password":"password123"}' \
    $BASE_URL/api/auth/login)
print_response $? "Login user"

# 4. Test auth status
curl -s -H "Authorization: Bearer $USER_TOKEN" $BASE_URL/api/auth/status
print_response $? "Check auth status"

echo -e "\nTesting Profile Controller..."
# 5. Create profile
PROFILE_RESPONSE=$(curl -s -X POST -H "Authorization: Bearer $USER_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Profile", "bio":"Test Bio"}' \
    $BASE_URL/api/profiles)
PROFILE_ID=$(echo $PROFILE_RESPONSE | jq -r '.id')
print_response $? "Create profile"

# 6. Get profile
curl -s -H "Authorization: Bearer $USER_TOKEN" $BASE_URL/api/profiles/$PROFILE_ID
print_response $? "Get profile"

# 7. Create section
SECTION_RESPONSE=$(curl -s -X POST -H "Authorization: Bearer $USER_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"type":"ABOUT", "title":"About Me", "order":1}' \
    $BASE_URL/api/profiles/$PROFILE_ID/sections)
SECTION_ID=$(echo $SECTION_RESPONSE | jq -r '.id')
print_response $? "Create section"

echo -e "\nTesting Admin Controller..."
# 8. Test admin endpoints
curl -s -X POST -H "Authorization: Bearer $ADMIN_TOKEN" \
    $BASE_URL/api/admin/tokens/revoke/user@example.com
print_response $? "Revoke user tokens"

curl -s -H "Authorization: Bearer $ADMIN_TOKEN" $BASE_URL/api/admin/tokens/cleanup
print_response $? "Cleanup expired tokens"

curl -s -X POST -H "Authorization: Bearer $ADMIN_TOKEN" $BASE_URL/api/admin/tokens/revoke-all
print_response $? "Revoke all tokens"

echo -e "\nTest complete!"
