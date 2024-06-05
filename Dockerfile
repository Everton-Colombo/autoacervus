FROM openjdk:23-slim-bullseye

WORKDIR /app

# Copy required files
COPY src /app/src
COPY .mvn /app/.mvn
COPY mvnw /app
COPY pom.xml /app
RUN mv ./src/main/resources/application.docker.properties ./src/main/resources/application.properties

RUN apt update && apt install -y wget unzip
RUN wget https://storage.googleapis.com/chrome-for-testing-public/125.0.6422.141/linux64/chrome-linux64.zip
RUN unzip chrome-linux64.zip
RUN ln -s /app/chrome-linux64/chrome /usr/bin/google-chrome
RUN wget https://storage.googleapis.com/chrome-for-testing-public/125.0.6422.141/linux64/chromedriver-linux64.zip
RUN unzip chromedriver-linux64.zip
RUN cp chromedriver-linux64/chromedriver /app/src/main/resources/webdrivers/chromedriver

RUN ./mvnw package -DskipTests

# Run the application
CMD ["java", "-jar", "target/autoacervus-0.0.1-SNAPSHOT.jar", "target/*.war"]
