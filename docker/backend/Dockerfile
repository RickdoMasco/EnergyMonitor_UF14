FROM gradle:8.12.1-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle --no-daemon bootJar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
