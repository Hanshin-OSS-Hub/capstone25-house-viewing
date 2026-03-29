# 1단계: 빌드
FROM gradle:8.7-jdk17 AS builder

WORKDIR /app

# Gradle 캐시 활용
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 전체 소스 복사 후 빌드
COPY . .
RUN gradle clean bootJar --no-daemon

# 2단계: 실행
FROM eclipse-temurin:17-jdk

WORKDIR /app

# builder 단계에서 생성된 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]