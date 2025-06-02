FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/profile-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 2222

ENTRYPOINT ["java", "-jar", "app.jar"]
