FROM gradle:jdk17 AS first-stage
WORKDIR /app
COPY . /app/.
RUN gradle build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=first-stage /app/build/libs/*.jar /app/*.jar
EXPOSE 5001
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/*.jar"]