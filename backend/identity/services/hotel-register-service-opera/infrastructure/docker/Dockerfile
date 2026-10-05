FROM whitbreaddigital/java-jdk-opera:openjdk_17

ARG APP_NAME
ARG APP_VERSION
ARG APPDYNAMICS_OPTS
ARG JAVA_OPTS

ARG tierName
ARG nodeName
ARG JAR_FILE="/target/${APP_NAME}-${APP_VERSION}.jar"

ENV env_tierName=$tierName
ENV env_nodeName=$nodeName

VOLUME /tmp
ADD $JAR_FILE app.jar

CMD exec java ${APPDYNAMICS_OPTS} ${JAVA_OPTS} \
-Dappdynamics.agent.tierName=${env_tierName} \
-Dappdynamics.agent.reuse.nodeName=true \
-Dappdynamics.agent.reuse.nodeName.prefix=${env_nodeName} \
-Dappdynamics.jvm.shutdown.mark.node.as.historical=true \
-Djava.security.egd=file:/dev/./urandom -jar /app.jar
