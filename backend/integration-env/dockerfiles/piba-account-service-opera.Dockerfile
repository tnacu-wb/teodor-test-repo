FROM eclipse-temurin:25-jre

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/* \
  && groupadd -r appuser \
  && useradd -r -g appuser -d /app appuser

WORKDIR /app

COPY --chown=appuser:appuser piba-account-service-opera-1.0.0.jar /app/app.jar

USER appuser

EXPOSE 9064 5008

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
