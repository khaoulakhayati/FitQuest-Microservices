FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY services/nutrition-service/pom.xml services/nutrition-service/
RUN mvn -f services/nutrition-service/pom.xml dependency:go-offline -B -q || true
COPY services/nutrition-service services/nutrition-service
RUN mvn -f services/nutrition-service/pom.xml package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/services/nutrition-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
