FROM eclipse-temurin:25-jre

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/* \
  && groupadd -r appuser \
  && useradd -r -g appuser -d /app appuser

WORKDIR /app

COPY --chown=appuser:appuser threec-payment-service-opera-1.0.0.jar /app/app.jar

USER appuser

EXPOSE 9001 5020

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
