# Multi-stage Dockerfile for Prestify Spring Boot Application

# Stage 1: Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
# Copy entire project to ensure all files (pom, resources, properties) are available
COPY Prestify_Plateforme_de_gestion_des_services-main/ ./

# Cache dependencies layer and prepare offline deps
RUN mvn dependency:go-offline -B

# Build application (skip tests in image build; CI should run tests)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime Stage (Optimized)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Install curl for healthchecks and lightweight tooling
RUN apt-get update && apt-get install -y --no-install-recommends curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN useradd -m -u 1000 appuser

# Copy jar from builder stage
COPY --from=builder /app/target/prestify-*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port (default Spring Boot)
EXPOSE 8080

# Health check: verify Spring Boot actuator health endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application with optimized JVM flags
ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:InitialHeapSize=512m", \
    "-XX:MaxHeapSize=1g", \
    "-Dspring.profiles.active=prod", \
    "-jar", "app.jar"]
