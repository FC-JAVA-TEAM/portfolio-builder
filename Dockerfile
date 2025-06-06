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

# Create a non-root user
RUN useradd -r -u 1001 -g root appuser

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Set ownership to non-root user
RUN chown appuser:root /app \
    && chmod g+rwX /app \
    && chown appuser:root app.jar

# Switch to non-root user
USER 1001

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:2222/api/health/ping || exit 1

# Run the application with optimized JVM options
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar" \
]
