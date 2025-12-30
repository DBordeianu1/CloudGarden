# Multi-stage Dockerfile for CloudGarden
# This builds both React frontend and Spring Boot backend in a single container

# ===================================
# Stage 1: Build Frontend
# ===================================
FROM node:20-alpine AS frontend-build

WORKDIR /app/frontend

# Copy frontend package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy frontend source code
COPY frontend/ ./

# Build the React app for production
# Use relative URL so nginx can proxy to backend
ENV REACT_APP_API_URL=/api/plants
RUN npm run build

# ===================================
# Stage 2: Build Backend
# ===================================
FROM maven:3.9-eclipse-temurin-17-alpine AS backend-build

WORKDIR /app/backend

# Copy Maven configuration
COPY backend/pom.xml ./

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy backend source code
COPY backend/src ./src

# Build the Spring Boot application
RUN mvn clean package -DskipTests -B

# ===================================
# Stage 3: Runtime Container
# ===================================
FROM eclipse-temurin:17-jre-alpine

# Install nginx and supervisor for running multiple processes
RUN apk add --no-cache nginx supervisor

# Create necessary directories
RUN mkdir -p /var/www/html \
    /var/log/supervisor \
    /run/nginx

# Copy frontend build from Stage 1
COPY --from=frontend-build /app/frontend/build /var/www/html

# Copy backend JAR from Stage 2
COPY --from=backend-build /app/backend/target/*.jar /app/backend.jar

# Copy nginx configuration
COPY nginx.conf /etc/nginx/http.d/default.conf

# Copy supervisor configuration
COPY supervisord.conf /etc/supervisord.conf

# Copy startup script
COPY startup.sh /startup.sh
RUN chmod +x /startup.sh

# Expose ports
# 80 - nginx (frontend)
# 8080 - Spring Boot (backend API)
EXPOSE 80 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80 || exit 1

# Run supervisor to manage both nginx and Spring Boot
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisord.conf"]
