# =====================================================================================
# Dockerfile 설명
# =====================================================================================
# 이 Dockerfile은 Spring Boot 애플리케이션을 빌드하고 실행하기 위한 2단계 빌드(Multi-stage build) 프로세스를 사용합니다.
# 1단계 (Builder): 소스 코드를 컴파일하고 실행 가능한 JAR 파일을 생성합니다. 이 단계에서는 JDK와 Gradle이 포함된 이미지를 사용합니다.
# 2단계 (Runner): 1단계에서 생성된 JAR 파일을 가져와 최소한의 JRE(Java Runtime Environment)만 포함된 이미지에서 애플리케이션을 실행합니다.
#
# 왜 2단계 빌드를 사용하나요?
# 1. 최종 이미지 크기 최소화: 빌드에 필요했던 JDK, Gradle, 소스 코드 등은 최종 이미지에 포함되지 않습니다. 오직 실행에 필요한 JAR 파일과 JRE만 포함되므로 이미지 용량이 매우 작아집니다.
# 2. 보안 강화: 빌드 도구나 소스 코드가 최종 이미지에 없어 공격 표면(attack surface)이 줄어듭니다. 또한, non-root 사용자로 애플리케이션을 실행하여 컨테이너가 탈취되더라도 호스트 시스템에 미치는 영향을 최소화합니다.
# 3. 배포 효율성 증대: 이미지 크기가 작아져 레지스트리 저장 비용이 줄고, 네트워크를 통해 이미지를 주고받는 속도가 빨라집니다.
# =====================================================================================

# --- STAGE 1: Builder ---
# 애플리케이션을 빌드하기 위한 환경입니다.
# 'gradle:8.7-jdk17' 이미지를 기반으로 하며, 이는 Java 17과 Gradle 8.7을 포함하고 있습니다.
# 'AS builder'는 이 단계를 'builder'라는 이름으로 참조할 수 있게 해줍니다.
FROM gradle:8.7-jdk17 AS builder

# 작업 디렉토리를 '/app'으로 설정합니다. 이후의 모든 명령어는 이 디렉토리 내에서 실행됩니다.
WORKDIR /app

# Docker의 레이어 캐싱을 활용하기 위해 소스 코드 전체를 복사하기 전에 빌드 설정 파일부터 먼저 복사합니다.
# 의존성이 변경되지 않았다면, Docker는 이 레이어를 캐시하여 다음 빌드 시 'gradle dependencies' 단계를 다시 실행하지 않고 캐시된 결과를 사용합니다.
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew gradlew.bat ./

# Gradle 의존성을 다운로드합니다. '--no-daemon' 옵션은 CI/CD 환경에서 불필요한 Gradle 데몬 프로세스를 생성하지 않도록 합니다.
# '|| true'는 의존성 분석 중 오류가 발생해도 빌드를 중단하지 않도록 하는 안전장치 역할을 할 수 있으나,
# 여기서는 의존성을 확실히 받아오기 위해 `gradle dependencies`를 실행합니다.
RUN ./gradlew dependencies --no-daemon

# 나머지 소스 코드를 복사합니다.
COPY src ./src

# Gradle wrapper를 사용하여 애플리케이션을 빌드합니다. 'clean'은 이전 빌드 결과물을 삭제하고, 'bootJar'는 실행 가능한 Spring Boot JAR 파일을 생성합니다.
RUN ./gradlew clean bootJar --no-daemon

# --- STAGE 2: Runner ---
# 빌드된 애플리케이션을 실행하기 위한 환경입니다.
# 'eclipse-temurin:17-jre-jammy'는 Ubuntu Jammy 기반의 Java 17 JRE만 포함된 경량 이미지입니다.
# JDK가 아닌 JRE를 사용함으로써 최종 이미지의 크기를 크게 줄일 수 있습니다.
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉토리를 '/app'으로 설정합니다.
WORKDIR /app

# 보안을 위해 애플리케이션을 실행할 전용 사용자 및 그룹을 생성합니다.
# 시스템 계정(--system)으로 생성하며, 홈 디렉토리는 만들지 않습니다(--no-create-home).
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

# builder 단계의 '/app/build/libs/' 디렉토리에서 생성된 JAR 파일을 현재 디렉토리의 'app.jar'라는 이름으로 복사합니다.
# 와일드카드(*)를 사용하여 버전이 바뀌더라도 유연하게 대처할 수 있습니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 복사된 'app.jar' 파일의 소유자를 위에서 생성한 'appuser:appgroup'으로 변경합니다.
# 이렇게 하면 애플리케이션이 불필요하게 높은 권한으로 실행되는 것을 방지합니다.
RUN chown appuser:appgroup app.jar

# 이후의 명령어를 실행할 사용자를 'appuser'로 전환합니다.
USER appuser

# 애플리케이션이 8080 포트를 사용함을 명시합니다. 이는 문서화 목적이며, 실제 포트 매핑은 'docker run' 명령어의 '-p' 옵션으로 지정해야 합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 실행될 명령어를 지정합니다. 'java -jar app.jar'를 실행하여 애플리케이션을 시작합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
