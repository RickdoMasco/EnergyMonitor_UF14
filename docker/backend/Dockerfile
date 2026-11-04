FROM eclipse-temurin:21-jre

WORKDIR /app

# Il jar viene generato localmente con: ./gradlew bootJar
COPY build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
