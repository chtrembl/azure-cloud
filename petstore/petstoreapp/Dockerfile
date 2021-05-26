FROM mcr.microsoft.com/openjdk/jdk:16-ubuntu
RUN apt-get update -y && apt-get install -y --no-install-recommends openssh-server 
RUN mkdir -p /run/sshd
COPY sshd_config /etc/ssh/ 
EXPOSE 2222 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} petstoreapp.jar
#ENTRYPOINT ["java","-jar","/petstoreapp.jar"]
ENTRYPOINT ["/bin/bash", "-c", "/usr/sbin/sshd && java -jar /petstoreapp.jar"]
