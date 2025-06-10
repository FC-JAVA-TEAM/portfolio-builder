# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Declare build arguments for the build stage
ARG BRANCH_NAME
ARG BUILD_ID
ARG BUILD_DATETIME
ARG IMAGE_TAG
ARG ENVIRONMENT

# Set environment variables (optional for build stage)
ENV BRANCH_NAME=$BRANCH_NAME \
    BUILD_ID=$BUILD_ID \
    BUILD_DATETIME=$BUILD_DATETIME \
    IMAGE_TAG=$IMAGE_TAG \
    ENVIRONMENT=$ENVIRONMENT

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

# Re-declare build arguments for the runtime stage
ARG BRANCH_NAME
ARG BUILD_ID
ARG BUILD_DATETIME
ARG IMAGE_TAG
ARG ENVIRONMENT

# Set environment variables
ENV BRANCH_NAME=$BRANCH_NAME \
    BUILD_ID=$BUILD_ID \
    BUILD_DATETIME=$BUILD_DATETIME \
    IMAGE_TAG=$IMAGE_TAG \
    ENVIRONMENT=$ENVIRONMENT

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
