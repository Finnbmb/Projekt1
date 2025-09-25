# Projekt1 - Terminkalender
SWT Projekt 1 Terminkalender - **Azure MySQL Production Ready**

## 🚀 Schnellstart

### Production (Azure MySQL - Standard)
```bash
mvn spring-boot:run
```
**App startet automatisch mit Azure MySQL Datenbank!**

### Alternative: Lokale Docker-Umgebung
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
- **Database**: Azure MySQL Flexible Server (Production)
- **Authentication**: JWT with HS512
- **Build**: Maven
- **Containerization**: Docker & Docker Compose
- **Monitoring**: Spring Boot Actuator

## 📊 Monitoring & Database Access

- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Azure Database Viewer**: http://localhost:8080/azure-database-viewer.html
- **Database API**: http://localhost:8080/database/view

## 🐳 Docker Commands (Optional)

```bash
# Build & Start (für lokale MySQL Tests)
docker-compose up --build

# Stop
docker-compose down

# Remove volumes (⚠️ löscht Daten)
docker-compose down -v
```

## 🔧 Debug & API Interfaces

- **Main App**: http://localhost:8080/
- **Debug Interface**: http://localhost:8080/debug-interface.html
- **API Testing**: http://localhost:8080/appointment-test.html
- http://localhost:8080/appointment-test.html
