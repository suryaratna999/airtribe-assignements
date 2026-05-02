# Use a lightweight OpenJDK image
FROM eclipse-temurin:21-jre-alpine
# Set working directory inside the container
WORKDIR /app
# Copy the built JAR file from your target folder
COPY target/*.jar app.jar
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]