# Projekt1 - Terminkalender
SWT Projekt 1 Terminkalender

## 🚀 Schnellstart

### Entwicklung (H2 Datenbank)
```bash
mvn spring-boot:run
```

### Produktion (MySQL mit Docker)
```bash
# Projekt bauen
mvn clean package

# Mit Docker Compose starten
docker-compose up -d

# Logs anzeigen
docker-compose logs -f app
```

## 🛠️ Technologie-Stack

- **Backend**: Java 17, Spring Boot 3.1.0
- **Database**: H2 (Development), MySQL (Production)
- **Build**: Maven
- **Containerization**: Docker & Docker Compose
- **Monitoring**: Spring Boot Actuator

## 📊 Monitoring & Health Checks

- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **H2 Console** (Dev): http://localhost:8080/h2-console
- **phpMyAdmin** (Prod): http://localhost:8081

## 🐳 Docker Commands

```bash
# Build & Start
docker-compose up --build

# Stop
docker-compose down

# Remove volumes (⚠️ löscht Daten)
docker-compose down -v
```

## 🔧 Debug Interfaces

- http://localhost:8080/debug-interface.html
- http://localhost:8080/appointment-test.html
