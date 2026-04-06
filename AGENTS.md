# House Viewing - Agent Instructions

## Project Overview
Spring Boot 3.5.10 + Java 17 + Gradle 8.14.4 application for house viewing management.

## Build & Run Commands

```bash
./gradlew build              # Build (skips tests)
./gradlew bootJar           # Create executable JAR
./gradlew bootRun          # Run application directly
./gradlew test             # Run all tests
./gradlew test --tests "*ServiceTest"        # Unit tests only
./gradlew test --tests "*IntegrationTest"    # Integration tests only
./gradlew clean            # Clean build artifacts
```

## Dependencies
- **MySQL 8.0** on port 3307 (docker-compose)
- **Redis 7** on port 6379
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

## Docker
```bash
docker-compose up --build   # Start app, MySQL, Redis
docker-compose down         # Stop all services
```

## Test Configuration
- Integration tests (`*IntegrationTest.java`) require MySQL + Redis running
- Unit tests (`*ServiceTest.java`) use Mockito mocks only
- Uses `application.yml` config; no separate test resources
- JPA `ddl-auto: create` - tables recreated on each run

## Architecture
```
src/main/java/com/house/houseviewing/
├── api/query/          # REST controllers
├── domain/             # Business logic (auth, user, house, contract, subscription, analysis, report)
├── global/
│   ├── config/         # Spring configurations
│   ├── security/       # JWT authentication
│   └── exception/      # Exception handling
└── infrastructure/
    ├── mock/registry/  # Mock data (SAFE/WARNING/DANGER/ORIGINAL JSON files)
    └── python/          # Python API integration
```

## Key Conventions
- JWT tokens for authentication (access: 1h, refresh: 7d)
- Lombok used for entity/builder classes
- Entity tests (`*EntityTest.java`) for domain model validation
- Fixtures in `src/test/java/com/house/houseviewing/fixture/`

## Environment Variables
Set `KAKAO_REST_API_KEY` in `.env` for Kakao API integration.
