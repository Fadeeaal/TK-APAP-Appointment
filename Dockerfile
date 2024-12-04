FROM eclipse-temurin:21-jre-alpine

COPY build/libs/appointment-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8081

CMD ["java", "-jar", "/app.jar"]