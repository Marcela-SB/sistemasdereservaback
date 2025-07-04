FROM maven:3.8.3-openjdk-17 AS build
COPY src /app/src
COPY pom.xml /app

RUN mvn -f /app/pom.xml clean package -DskipTests
FROM openjdk:17-alpine

COPY --from=build /app/target/sistemadereservasdeart-1.0.0.jar /app-service/sistemadereservasdeart-1.0.0.jar
WORKDIR /app-service

EXPOSE 8080
ENTRYPOINT ["java","-jar","sistemadereservasdeart-1.0.0.jar"]

