# Build aşaması
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app

# Maven ve diğer build araçlarını buraya ekle (daha büyük image kullanman gerekebilir)
RUN apk add --no-cache maven

# Kaynak kodu kopyala
COPY . .

# Build jar dosyasını oluştur
RUN mvn clean package -DskipTests

# Çalıştırma aşaması
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Build aşamasından jar dosyasını kopyala
COPY --from=builder /app/target/bazaarx-backend-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Port aç
EXPOSE 8080