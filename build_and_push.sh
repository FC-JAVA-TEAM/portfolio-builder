#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Function to print messages
print_message() {
    echo "===> $1"
}

# Perform clean install with tests
print_message "Running Maven clean install with tests"
mvn clean install

# Check if tests passed
if [ $? -eq 0 ]; then
    print_message "All tests passed successfully"

    # Get current branch name
    BRANCH_NAME=$(git rev-parse --abbrev-ref HEAD)
    
    print_message "Current branch: $BRANCH_NAME"

    # Push the current branch
    print_message "Pushing branch $BRANCH_NAME to remote"
    git push origin $BRANCH_NAME

    print_message "Branch $BRANCH_NAME has been successfully pushed"
else
    print_message "Tests failed. Please fix the issues before pushing the branch"
    exit 1
fi
