# Use Maven to build the app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run the app using a JDK
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY serviceAccount.json ./serviceAccount.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]