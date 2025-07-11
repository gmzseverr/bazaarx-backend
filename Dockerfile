# JDK 17 kullanıyoruz
FROM eclipse-temurin:17-jdk-alpine

# Çalışma dizini oluştur
WORKDIR /app

# JAR dosyasını kopyala
COPY target/bazaarx-backend-0.0.1-SNAPSHOT.jar app.jar


# Uygulamayı çalıştır
ENTRYPOINT ["java","-jar","/app/app.jar"]
