#
# Build stage
#
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package

#
# Package stage
#
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

WORKDIR /app

RUN apt-get update -y && apt-get install -y --no-install-recommends openssh-server && apt-get install -y --no-install-recommends curl 
CMD /bin/bash
RUN echo "Downloading applicationinsights-agent-3.4.12.jar"
RUN curl "https://repo1.maven.org/maven2/com/microsoft/azure/applicationinsights-agent/3.4.12/applicationinsights-agent-3.4.12.jar" -o /app/applicationinsights-agent-3.4.12.jar

RUN mkdir -p /run/sshd && echo "root:Docker!" | chpasswd
COPY sshd_config /etc/ssh/ 
EXPOSE 2222 8080
COPY --from=build /build/target/*.jar /app/petstoreapp.jar
COPY applicationinsights.json /app/applicationinsights.json

ENTRYPOINT ["/bin/bash", "-c", "/usr/sbin/sshd && java -javaagent:applicationinsights-agent-3.4.12.jar -jar petstoreapp.jar"]
