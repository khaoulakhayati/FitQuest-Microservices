FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY services/gamification-service/pom.xml services/gamification-service/
RUN mvn -f services/gamification-service/pom.xml dependency:go-offline -B -q || true
COPY services/gamification-service services/gamification-service
RUN mvn -f services/gamification-service/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/services/gamification-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
