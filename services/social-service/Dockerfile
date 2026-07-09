FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY services/social-service/pom.xml services/social-service/
RUN mvn -f services/social-service/pom.xml dependency:go-offline -B -q || true
COPY services/social-service services/social-service
RUN mvn -f services/social-service/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/services/social-service/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
