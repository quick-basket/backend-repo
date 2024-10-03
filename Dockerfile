FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# Uncomment the next line if you need to copy an .env file
COPY .env.development .

# Build the application
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/target/*.jar app.jar

# Uncomment the next line if you need to copy an .env file
COPY --from=build /app/.env.development ./.env.development

# Expose the port the app runs on
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# (Optional) Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1