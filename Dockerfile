FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/terminkalender-*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
