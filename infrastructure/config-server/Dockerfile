FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY infrastructure/config-server/pom.xml infrastructure/config-server/
RUN mvn -f infrastructure/config-server/pom.xml dependency:go-offline -B -q || true
COPY infrastructure/config-server infrastructure/config-server
RUN mvn -f infrastructure/config-server/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/infrastructure/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
