FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY services/auth-service/pom.xml services/auth-service/
RUN mvn -f services/auth-service/pom.xml dependency:go-offline -B -q || true
COPY services/auth-service services/auth-service
RUN mvn -f services/auth-service/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/services/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
