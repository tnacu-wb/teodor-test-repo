# ==============================================================================
# Dockerfile for company-entity-service
# Build context: identity/services/company-entity-service/target
# Expects the fat JAR to be pre-built via build.sh or ./mvnw locally
# ==============================================================================

FROM eclipse-temurin:25-jre

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/* \
  && groupadd -r appuser \
  && useradd -r -g appuser -d /app appuser

WORKDIR /app

COPY --chown=appuser:appuser company-entity-service-1.0.0.jar /app/app.jar

USER appuser

EXPOSE 9118 5011

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
