FROM maven:3.8.4-openjdk-17-slim
RUN apt-get install -y google-chrome-stable
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package