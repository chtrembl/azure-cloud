FROM openjdk:13-jdk-alpine
RUN apk add --no-cache openssh
COPY sshd_config /etc/ssh/ 
EXPOSE 2222
EXPOSE 8080
CMD ["ssh"]
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} petstoreapp.jar
ENTRYPOINT ["java","-jar","/petstoreapp.jar"]
