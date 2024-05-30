FROM openjdk:23-slim-bullseye

WORKDIR /app

# Copy required files
COPY src /app/src
COPY .mvn /app/.mvn
COPY mvnw /app
COPY mvnw.cmd /app
COPY pom.xml /app
RUN mv ./src/main/resources/application.docker.properties ./src/main/resources/application.properties

RUN ./mvnw package -DskipTests

# Run the application
CMD ["java", "-jar", "target/dependency/webapp-runner.jar", "target/*.war"]
