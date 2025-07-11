# Build aşaması
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /usr/src

COPY . .

RUN mvn clean package -DskipTests

# Runtime aşaması
FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

# Build aşamasından jar dosyasını kopyala
COPY --from=build /usr/src/target/bazaarx-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
