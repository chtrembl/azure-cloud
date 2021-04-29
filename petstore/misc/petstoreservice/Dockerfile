FROM openjdk:8-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} petstoreservice.jar
ENTRYPOINT ["java","-jar","/petstoreservice.jar"]
