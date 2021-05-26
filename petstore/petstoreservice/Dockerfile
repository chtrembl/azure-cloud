FROM mcr.microsoft.com/openjdk/jdk:16-ubuntu
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} petstoreservice.jar
ENTRYPOINT ["java","-jar","/petstoreservice.jar"]
