FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY infrastructure/api-gateway/pom.xml infrastructure/api-gateway/
RUN mvn -f infrastructure/api-gateway/pom.xml dependency:go-offline -B -q || true
COPY infrastructure/api-gateway infrastructure/api-gateway
RUN mvn -f infrastructure/api-gateway/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/infrastructure/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
