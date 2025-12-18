FROM maven:3.8.3-openjdk-17 AS build
COPY src /app/src
COPY pom.xml /app

RUN mvn -f /app/pom.xml clean package -DskipTests
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app-service
COPY --from=build /app/target/sistemadereservasdeart-1.0.0.jar .

EXPOSE 8080

ENTRYPOINT ["java","-jar","sistemadereservasdeart-1.0.0.jar"]