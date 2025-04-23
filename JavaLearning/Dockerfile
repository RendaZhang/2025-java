# Use a base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/JavaLearning-1.0-SNAPSHOT.jar app.jar

# Define the startup command
ENTRYPOINT ["java", "-jar", "app.jar"]
