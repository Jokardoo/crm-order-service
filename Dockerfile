FROM maven:3.8.5-openjdk-17 AS build
LABEL authors="jokardo"

COPY /src /src
COPY pom.xml /
RUN mvn -f /pom.xml cleam package

FROM openjdk:17-jdk-slim
COPY --from build /target/*.jar application.jar
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "application.jar"]