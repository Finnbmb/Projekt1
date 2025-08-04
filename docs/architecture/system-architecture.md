# Systemarchitektur - Terminkalender-Anwendung

## 1. Architektur-Übersicht

### 1.1 Architekturstil
Die Anwendung folgt einer **3-Schichten-Architektur (Three-Tier Architecture)** mit zusätzlicher Aufteilung in **Microservices-orientierte Komponenten**.

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │   Web Client    │  │   Mobile App    │  │  Admin UI   │  │
│  │   (Angular/     │  │   (Optional)    │  │             │  │
│  │    React)       │  │                 │  │             │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────┘
                                │
                         HTTP/REST API
                                │
┌─────────────────────────────────────────────────────────────┐
│                     BUSINESS LAYER                          │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │            Spring Boot Application                      │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐ │ │
│  │  │ Controller  │  │  Service    │  │  Security       │ │ │
│  │  │   Layer     │  │   Layer     │  │  Component      │ │ │
│  │  └─────────────┘  └─────────────┘  └─────────────────┘ │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                          JPA/Hibernate
                                │
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐  │
│  │   H2 Database   │  │  PostgreSQL     │  │  File       │  │
│  │  (Development)  │  │  (Production)   │  │  Storage    │  │
│  └─────────────────┘  └─────────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Architekturprinzipien
- **Separation of Concerns:** Klare Trennung zwischen Präsentation, Geschäftslogik und Daten
- **Single Responsibility Principle:** Jede Komponente hat eine klar definierte Aufgabe
- **Dependency Inversion:** Abhängigkeiten zeigen von konkreten zu abstrakten Komponenten
- **Open/Closed Principle:** Erweiterbar für neue Funktionen, geschlossen für Änderungen

## 2. Komponentenarchitektur

### 2.1 Spring Boot Anwendungsarchitektur

```
com.example.calendar
├── CalendarApplication.java          # Main Application Class
├── config/                          # Configuration Classes
│   ├── WebConfig.java               # Web Configuration
│   ├── DatabaseConfig.java          # Database Configuration
│   └── SecurityConfig.java          # Security Configuration
├── controller/                      # REST Controllers
│   ├── AppointmentController.java   # Appointment REST API
│   ├── CalendarController.java      # Calendar Views API
│   └── UserController.java          # User Management API
├── service/                         # Business Logic Layer
│   ├── AppointmentService.java      # Appointment Business Logic
│   ├── CalendarService.java         # Calendar Business Logic
│   └── UserService.java             # User Business Logic
├── repository/                      # Data Access Layer
│   ├── AppointmentRepository.java   # Appointment Data Access
│   └── UserRepository.java          # User Data Access
├── model/                          # Domain Models
│   ├── Appointment.java            # Appointment Entity
│   ├── User.java                   # User Entity
│   └── dto/                        # Data Transfer Objects
│       ├── AppointmentDTO.java     # Appointment DTO
│       └── CreateAppointmentDTO.java
├── exception/                      # Exception Handling
│   ├── AppointmentNotFoundException.java
│   └── GlobalExceptionHandler.java
└── util/                          # Utility Classes
    └── DateTimeUtil.java          # Date/Time Utilities
```

### 2.2 Komponentenbeschreibung

#### **Controller Layer**
- **Verantwortlichkeit:** HTTP-Request-Handling, Validation, Response-Formatting
- **Pattern:** REST Controller Pattern
- **Technologie:** Spring Web MVC

#### **Service Layer**
- **Verantwortlichkeit:** Geschäftslogik, Transaktionsmanagement
- **Pattern:** Service Layer Pattern
- **Technologie:** Spring Service, @Transactional

#### **Repository Layer**
- **Verantwortlichkeit:** Datenpersistierung, CRUD-Operationen
- **Pattern:** Repository Pattern
- **Technologie:** Spring Data JPA

#### **Model Layer**
- **Verantwortlichkeit:** Datenmodellierung, Entitäten
- **Pattern:** Domain Model Pattern
- **Technologie:** JPA Entities, Hibernate

## 3. Datenarchitektur

### 3.1 Datenbankschema

```sql
-- Benutzer-Tabelle
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Termine-Tabelle
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    location VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indizes für Performance
CREATE INDEX idx_appointments_user_id ON appointments(user_id);
CREATE INDEX idx_appointments_start_datetime ON appointments(start_datetime);
CREATE INDEX idx_appointments_end_datetime ON appointments(end_datetime);
```

### 3.2 Entity-Relationship Diagram

```
┌─────────────────┐         ┌─────────────────────────┐
│      USER       │         │      APPOINTMENT        │
├─────────────────┤         ├─────────────────────────┤
│ id (PK)         │────────▶│ id (PK)                 │
│ username        │    1:n  │ title                   │
│ email           │         │ description             │
│ password_hash   │         │ start_datetime          │
│ created_at      │         │ end_datetime            │
│ updated_at      │         │ location                │
└─────────────────┘         │ user_id (FK)            │
                            │ created_at              │
                            │ updated_at              │
                            └─────────────────────────┘
```

## 4. API-Architektur

### 4.1 REST API Design

```
Base URL: http://localhost:8080/api/v1

┌─────────────────────────────────────────────────────────────┐
│                      REST API ENDPOINTS                     │
├─────────────────────────────────────────────────────────────┤
│ GET    /appointments                 # List all appointments│
│ GET    /appointments/{id}            # Get specific appointment│
│ POST   /appointments                 # Create new appointment│
│ PUT    /appointments/{id}            # Update appointment   │
│ DELETE /appointments/{id}            # Delete appointment   │
│                                                             │
│ GET    /appointments/search?date={date}  # Search by date   │
│ GET    /appointments/calendar?month={month}&year={year}     │
│                                                             │
│ GET    /users                        # List users          │
│ POST   /users                        # Create user         │
│ GET    /users/{id}                   # Get user details    │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Request/Response Format

```json
// POST /appointments - Request Body
{
    "title": "Team Meeting",
    "description": "Weekly team sync",
    "startDateTime": "2025-07-25T10:00:00",
    "endDateTime": "2025-07-25T11:00:00",
    "location": "Conference Room A"
}

// Response
{
    "id": 1,
    "title": "Team Meeting",
    "description": "Weekly team sync",
    "startDateTime": "2025-07-25T10:00:00",
    "endDateTime": "2025-07-25T11:00:00",
    "location": "Conference Room A",
    "userId": 1,
    "createdAt": "2025-07-24T15:30:00",
    "updatedAt": "2025-07-24T15:30:00"
}
```

## 5. Deployment-Architektur

### 5.1 Development Environment
```
┌─────────────────┐
│  Local Machine  │
│                 │
│ ┌─────────────┐ │
│ │Spring Boot  │ │
│ │Application  │ │
│ │Port: 8080   │ │
│ └─────────────┘ │
│ ┌─────────────┐ │
│ │H2 Database  │ │
│ │In-Memory    │ │
│ └─────────────┘ │
└─────────────────┘
```

### 5.2 Production Environment (Geplant)
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │    │  App Server 1   │    │  App Server 2   │
│                 │    │                 │    │                 │
│ ┌─────────────┐ │    │ ┌─────────────┐ │    │ ┌─────────────┐ │
│ │   Nginx     │◄──── │ │Spring Boot  │ │    │ │Spring Boot  │ │
│ │             │ │    │ │Port: 8080   │ │    │ │Port: 8080   │ │
│ └─────────────┘ │    │ └─────────────┘ │    │ └─────────────┘ │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                └───────────┬───────────┘
                                            │
                              ┌─────────────────────┐
                              │  PostgreSQL DB      │
                              │                     │
                              │ ┌─────────────────┐ │
                              │ │   Database      │ │
                              │ │   Port: 5432    │ │
                              │ └─────────────────┘ │
                              └─────────────────────┘
```

## 6. Sicherheitsarchitektur

### 6.1 Authentifizierung & Autorisierung
- **Strategie:** JWT-basierte Authentifizierung
- **Session Management:** Stateless (JWT Tokens)
- **Password Security:** BCrypt Hashing

### 6.2 Sicherheitskomponenten
```
┌─────────────────────────────────────────────────────────────┐
│                    SECURITY LAYER                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   JWT       │  │  CORS       │  │   Input Validation  │  │
│  │ Filter      │  │ Config      │  │   & Sanitization    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Password    │  │ HTTPS/TLS   │  │  Exception          │  │
│  │ Encryption  │  │ Encryption  │  │  Handling           │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 7. Performance & Skalierbarkeit

### 7.1 Performance-Optimierungen
- **Database Indexing:** Optimierte Indizes für häufige Abfragen
- **Connection Pooling:** HikariCP für Datenbankverbindungen
- **Caching:** Redis für häufig abgerufene Daten (zukünftig)

### 7.2 Monitoring & Logging
- **Application Monitoring:** Spring Boot Actuator
- **Logging:** Logback mit strukturierten Logs
- **Health Checks:** /actuator/health endpoint

## 8. Technologie-Stack Details

### 8.1 Backend-Technologien
```yaml
Core Framework:
  - Spring Boot: 3.1.0
  - Spring Web MVC: REST API
  - Spring Data JPA: Data Access
  - Spring Security: Authentication/Authorization

Database:
  - Development: H2 Database (In-Memory)
  - Production: PostgreSQL 15+
  - Migration: Flyway/Liquibase

Build & Dependency Management:
  - Maven: 3.9+
  - Java: 17 (LTS)

Testing:
  - JUnit 5: Unit Testing
  - Mockito: Mocking Framework
  - TestContainers: Integration Testing
```

### 8.2 Development Tools
```yaml
IDE: Visual Studio Code / IntelliJ IDEA
Version Control: Git
Repository: GitHub
CI/CD: GitHub Actions
Code Quality: SonarQube
API Documentation: OpenAPI/Swagger
```

## 9. Qualitätsattribute

### 9.1 Messbare Qualitätsziele
- **Performance:** < 2s Response Time für 95% der Requests
- **Availability:** 99% Uptime
- **Scalability:** Horizontal Skalierung auf bis zu 10 Instanzen
- **Maintainability:** Code Coverage > 80%
- **Security:** OWASP Top 10 Compliance

### 9.2 Architektur-Entscheidungen
| Entscheidung | Grund | Alternativen | Konsequenzen |
|-------------|-------|-------------|-------------|
| Spring Boot | Standard Java Framework, produktiv | Quarkus, Micronaut | Etabliertes Ökosystem |
| H2 → PostgreSQL | Entwicklung → Produktion | MySQL, MongoDB | Relationale Datenintegrität |
| REST API | Standardisierte Schnittstelle | GraphQL, gRPC | Einfache Integration |
| JWT Auth | Stateless, skalierbar | Session-based | Höhere Komplexität |

Diese Architektur bildet die Grundlage für ein stabiles, wartbares und skalierbares Terminkalender-System. 🏗️
