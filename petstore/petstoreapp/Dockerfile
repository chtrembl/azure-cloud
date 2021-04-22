FROM openjdk:13-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} petstoreapp.jar
ENTRYPOINT ["java","-jar","/petstoreapp.jar"]