FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY services/challenge-service/pom.xml services/challenge-service/
RUN mvn -f services/challenge-service/pom.xml dependency:go-offline -B -q || true
COPY services/challenge-service services/challenge-service
RUN mvn -f services/challenge-service/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/services/challenge-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
