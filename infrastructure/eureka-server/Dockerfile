FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY infrastructure/eureka-server/pom.xml infrastructure/eureka-server/
RUN mvn -f infrastructure/eureka-server/pom.xml dependency:go-offline -B -q || true
COPY infrastructure/eureka-server infrastructure/eureka-server
COPY pom.xml .
RUN mvn -f infrastructure/eureka-server/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/infrastructure/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
