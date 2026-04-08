FROM eclipse-temurin:17-jdk-jammy AS builder

RUN apt-get update && apt-get install -y unzip

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN curl -sLO https://services.gradle.org/distributions/gradle-8.5-bin.zip && \
    unzip -q gradle-8.5-bin.zip && \
    rm gradle-8.5-bin.zip

ENV PATH="/app/gradle-8.5/bin:${PATH}"

COPY src ./src

RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
