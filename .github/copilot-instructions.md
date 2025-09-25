# GitHub Copilot Instructions - Terminkalender Application

## Project Overview
This is a Spring Boot 3.1.0 calendar/appointment management application developed as part of a Software Engineering course (SWTP1). The project follows a layered architecture with JWT authentication and is designed for easy development with H2 database switching to MySQL in production.

## Architecture Patterns

### Layered Architecture (ADR-0001)
- **Controller Layer**: REST endpoints (`@RestController`) in `controller/` package
- **Service Layer**: Business logic (`@Service`) in `service/` package  
- **Repository Layer**: Data access (`@Repository`) extending JpaRepository
- **Entity Layer**: JPA entities in `entity/` package
- **DTO Layer**: Request/Response objects in `dto/` package

**Critical Rule**: Controllers MUST NOT directly access repositories - always go through services.

### Manual DTO Mapping (ADR-0003)
- No MapStruct or ModelMapper - use manual mapping in service classes
- Pattern: `toDto()` and `fromDto()` methods within services
- Keep mapping logic close to business logic for better control

### JWT Authentication (ADR-0002)
- Stateless JWT tokens with HS512 signature
- Claims: userId, username, role
- Security config in `config/SecurityConfig.java`
- Use `SecurityUtils.getCurrentUserId()` to get authenticated user

## Development Workflow

### Running the Application
```bash
# Development (H2 database)
mvn spring-boot:run

# Production (MySQL with Docker)
mvn clean package
docker-compose up -d

# Or use VS Code task: "Start Terminkalender Application"
```

### Database Access
- **H2 Console** (Dev): http://localhost:8080/h2-console
  - URL: `jdbc:h2:file:./data/terminkalender`
  - Username: `sa`, Password: `password`
- **MySQL** (Prod): Configured via Docker Compose
- **phpMyAdmin** (Prod): http://localhost:8081

### Monitoring & Health Checks
- **Actuator Health**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Environment**: http://localhost:8080/actuator/env (dev only)

### Debug Interfaces
The project includes several HTML-based debug tools in `src/main/resources/static/`:
- `debug-interface.html`: Main API testing interface
- `appointment-test.html`: Appointment CRUD operations testing
- `database-tools.html`: Database utilities
- `h2-debug.html`: H2-specific debugging

Access via: http://localhost:8080/[filename].html

## Configuration Patterns

### Environment-Specific Config
- `application.yml`: Main development configuration (H2, Debug logging, Actuator)
- `application-prod.properties`: Production overrides (MySQL, Mail, Security)
- Docker Compose: Complete production setup with MySQL and phpMyAdmin

### Containerization
- `Dockerfile`: Spring Boot application container
- `docker-compose.yml`: Full stack with MySQL database
- `scripts/init.sql`: MySQL initialization script

### Monitoring Setup
- Spring Boot Actuator enabled for health checks and metrics
- Development: All endpoints exposed
- Production: Limited to health, info, metrics

### CORS Setup
- Permissive CORS configuration for development (`CorsConfig.java`)
- All origins allowed with `@CrossOrigin(origins = "*")` on controllers

### Security Configuration
- JWT filter chain in `JwtAuthenticationFilter.java`
- Endpoints: `/api/v1/auth/**` public, all others require authentication
- H2 console access secured by `H2SecurityConfig.java`

## Code Conventions

### Package Structure
```
de.swtp1.terminkalender/
├── config/          # Spring configuration classes
├── controller/      # REST controllers (@RestController)
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── repository/     # Data repositories
├── service/        # Business logic services
└── util/           # Utility classes (SecurityUtils, etc.)
```

### API Patterns
- REST endpoints: `/api/v1/{resource}`
- Request validation with `@Valid` annotation
- Consistent error handling across controllers
- Pagination support with Spring Data `Pageable`

### Entity Relationships
- User entities have appointments (One-to-Many)
- Appointment entities include: title, description, startTime, endTime, location, priority
- Soft delete pattern where applicable

## Testing Strategy

### Debug-First Development
- Use HTML debug interfaces for rapid API testing
- Manual testing preferred over extensive unit tests for this educational project
- Test authentication flow before testing protected endpoints

### API Testing Flow
1. Test basic connectivity via debug interfaces
2. Authenticate via `/api/v1/auth/login`
3. Use returned JWT token in Authorization header
4. Test CRUD operations through debug tools

## Common Patterns

### Service Implementation
```java
@Service
@Transactional
public class SomeService {
    // Constructor injection
    private final SomeRepository repository;
    
    // Manual DTO mapping
    private SomeResponseDto toDto(SomeEntity entity) { ... }
    private SomeEntity fromDto(SomeRequestDto dto) { ... }
}
```

### Controller Implementation
```java
@RestController
@RequestMapping("/api/v1/resource")
@CrossOrigin(origins = "*")
public class SomeController {
    // Constructor injection, delegate to service
}
```

## Development Notes

- **Recurrence Feature**: Deferred to Phase 2 (ADR-0005)
- **Database Strategy**: H2 → MySQL migration planned with Flyway
- **Documentation**: Comprehensive ADRs in `docs/architecture/adr/`
- **German Language**: Comments and documentation primarily in German

## Common Commands

```bash
# Build project
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package

# Development mode
mvn spring-boot:run

# Production mode with Docker
docker-compose up --build

# Stop production
docker-compose down

# View application logs
docker-compose logs -f app
```

## Email Service

### Configuration
- `EmailService`: Handles password reset emails
- SMTP configuration in `application-prod.properties`
- Uses Spring Boot Mail Starter

### Usage Pattern
```java
@Autowired
private EmailService emailService;

// Send password reset
emailService.sendPasswordResetEmail(userEmail, resetToken);
```

When working on this project, prioritize the layered architecture constraints, use the debug interfaces for testing, and follow the manual DTO mapping patterns established in the codebase.