# ==============================================================================
# Dockerfile for rules-agent-entity-service
# Build context: the service's target/ directory
# Expects the fat JAR to be pre-built via build-images.sh or ./mvnw locally
# ==============================================================================

FROM eclipse-temurin:25-jre

# Install the healthcheck HTTP client and create non-root user
RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/* \
  && groupadd -r appuser \
  && useradd -r -g appuser -d /app appuser

WORKDIR /app

# Copy the pre-built fat JAR
COPY --chown=appuser:appuser rules-agent-entity-service-1.0.0.jar /app/app.jar

USER appuser

EXPOSE 9108 5014

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
