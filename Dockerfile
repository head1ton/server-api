FROM openjdk:17.0.2-slim

COPY build/libs/server-api-0.0.1-SNAPSHOT.jar serverapi.jar
ENTRYPOINT ["java", "-jar", "serverapi.jar"]