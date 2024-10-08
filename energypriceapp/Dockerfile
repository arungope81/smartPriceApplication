# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the project’s JAR file into the container at /app/application.jar
COPY EnergyPriceApplication-1.0.jar /app/EnergyPriceApplication-1.0.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "EnergyPriceApplication-1.0.jar"]