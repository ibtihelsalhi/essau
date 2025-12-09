# Multi-stage Dockerfile for Prestify Spring Boot Application

# Stage 1: Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY Prestify_Plateforme_de_gestion_des_services-main/pom.xml .

# Cache dependencies layer
RUN mvn dependency:go-offline -B

# Copy source code
COPY Prestify_Plateforme_de_gestion_des_services-main/src ./src

# Build application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime Stage (Optimized)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

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

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD java -version || exit 1

# Run application with optimized JVM flags
ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:InitialHeapSize=512m", \
    "-XX:MaxHeapSize=1g", \
    "-Dspring.profiles.active=prod", \
    "-jar", "app.jar"]
