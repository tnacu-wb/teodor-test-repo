FROM openjdk:11.0.11-jdk
VOLUME /tmp
ARG JAR_FILE=../target/availability-cache-service-*.jar
RUN echo "Jar file Path: " ${JAR_FILE}
COPY ${JAR_FILE} /app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9060
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES}", "-jar", "/app.jar"]