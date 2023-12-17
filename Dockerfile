FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY gradle/ gradle
COPY gradlew build.gradle settings.gradle ./

COPY src ./src

EXPOSE 8080

CMD ["./gradlew", "bootRun"]
