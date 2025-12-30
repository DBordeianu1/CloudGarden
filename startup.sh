#!/bin/sh
# Startup script for CloudGarden container
# This script is kept for future use, currently using supervisor

echo "Starting CloudGarden services..."

# Start nginx in the background
nginx -g 'daemon off;' &

# Start Spring Boot application
exec java -jar /app/backend.jar
