FROM openjdk:23-slim-bullseye

WORKDIR /app

# Copy required files
COPY src /app/src
COPY src/main/resources/application.properties /app/src/main/resources/application.properties
COPY .mvn /app/.mvn
COPY mvnw /app
COPY pom.xml /app

RUN ./mvnw package -DskipTests

# Run the application
CMD ["java", "-jar", "/app/target/autoacervus-0.0.1-SNAPSHOT.jar", "target/*.war"]
