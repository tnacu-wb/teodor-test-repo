# ==============================================================================
# Dockerfile for content-entity-service
# Build context: monorepo root (../../ relative to docker-compose.yml)
# Expects the fat JAR to be pre-built via build.sh or ./mvnw locally
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
COPY --chown=appuser:appuser content-entity-service-1.0.0.jar /app/app.jar

USER appuser

EXPOSE 9106 5006

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
