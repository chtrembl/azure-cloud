#
# Build stage
#
FROM maven:3.6.0-jdk-8-slim AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package

#
# Package stage
#
FROM openjdk:8-jre-alpine

WORKDIR /app

RUN apk update && apk add curl 
CMD /bin/bash
RUN echo "Downloading applicationinsights-agent-3.4.12.jar"
RUN curl "https://repo1.maven.org/maven2/com/microsoft/azure/applicationinsights-agent/3.4.12/applicationinsights-agent-3.4.12.jar" -o /app/applicationinsights-agent-3.4.12.jar

COPY --from=build /build/target/*.jar /app/petstoreproductservice.jar
COPY applicationinsights.json /app/applicationinsights.json
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:applicationinsights-agent-3.4.12.jar", "-jar","petstoreproductservice.jar"]