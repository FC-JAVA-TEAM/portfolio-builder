#!/bin/bash
set -e

echo "Step 1: Building with Maven"
mvn clean package

echo "Step 2: Running tests"
mvn test

echo "Step 3: Building Docker image"
docker build -t portfolio-builder:test .

echo "Step 4: Removing existing Docker container (if any)"
docker rm -f test-container || true

echo "Step 5: Running Docker container"
docker run -d -p 2223:2222 --name test-container portfolio-builder:test

echo "Step 6: Checking if container is running"
docker ps | grep test-container

echo "Step 7: Waiting for application to start (30 seconds)"
sleep 30

echo "Step 8: Testing the application"
curl http://localhost:2223/actuator/health

echo "Step 9: Cleaning up"
docker stop test-container
docker rm test-container
docker rmi portfolio-builder:test
