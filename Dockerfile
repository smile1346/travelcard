# Use Eclipse Temurin JDK 17 as build environment
FROM eclipse-temurin:17-jdk as build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first to leverage Docker cache
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies only
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src ./src

# Package the application
RUN ./mvnw clean package -DskipTests

# Second stage: run the app with JRE only
FROM eclipse-temurin:17-jre

# Set working directory in container
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port your Spring Boot app runs on (default 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]