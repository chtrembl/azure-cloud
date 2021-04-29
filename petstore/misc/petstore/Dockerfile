FROM openjdk:8-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} petstoreapi.jar
ENTRYPOINT ["java","-jar","/petstoreapi.jar"]
