FROM gradle:8.3-jdk-17 AS first-stage
WORKDIR /app
COPY . /app/.
RUN gradle build

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=first-stage /app/target/*.jar /app/*.jar
EXPOSE 5001
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/*.jar"]