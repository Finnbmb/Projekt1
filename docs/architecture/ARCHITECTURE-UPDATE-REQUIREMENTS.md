# 📋 Architektur-Dokumentation: Update-Anforderungen

## 🚨 **Kritische Unterschiede zwischen Dokumentation und aktuellem Projekt**

### **1. 🎨 Frontend-Technologie**

#### **📄 Dokumentation sagt:**
- "Angular Frontend" (allgemein erwähnt)
- Grundlegende Angular-Erwähnung ohne Details

#### **✅ Tatsächlicher Zustand:**
- **Angular 20.3.x** (modernste Version)
- **Standalone Components** (kein app.module.ts)
- **Signal-basiertes State Management**
- **Material Design 20.x**
- **Reactive Forms**
- **HTTP Interceptors**
- **Modern App Structure** mit separatem frontend/ Ordner

#### **🔧 Update erforderlich:**
```markdown
## Frontend-Architektur (Angular 20)

### Moderne Angular-Features
- **Standalone Components**: Keine Module, direkte Imports
- **Signal-API**: Reactive State Management ohne RxJS Subjects
- **Control Flow**: @if, @for statt *ngIf, *ngFor  
- **Modern Routing**: funktionsbasierte Guards
- **Material Design 20**: Neueste UI-Komponenten

### Komponentenstruktur
```
frontend/src/app/
├── components/          # Login, Dialoge
├── services/           # Auth, Appointment, Calendar, Holiday
├── models/             # TypeScript Interfaces
├── guards/             # Auth Guard
├── interceptors/       # HTTP Auth Interceptor
├── appointments/       # Terminverwaltung
├── calendar-view/      # Kalenderansicht
└── dashboard/          # Dashboard
```
```

---

### **2. 🏗️ Backend-Features**

#### **📄 Dokumentation sagt:**
- Grundlegende Spring Boot Erwähnung
- JWT Authentication
- MySQL Datenbank

#### **✅ Tatsächlicher Zustand:**
- **Spring Boot 3.1.0** (neueste Features)
- **Umfassende Admin-Tools** (nicht dokumentiert!)
- **Deutsche Feiertage-Integration**
- **System-Monitoring mit Actuator**
- **Azure MySQL Unterstützung**
- **Layered Architecture** mit strikten Patterns

#### **🔧 Update erforderlich:**
```markdown
## Backend-Erweiterungen

### Admin-Tools Suite
- **Admin-Dashboard**: Zentrale Verwaltungsoberfläche
- **System-Monitoring**: Real-time Metriken und Health Checks
- **User-Management**: Benutzerverwaltung mit Aktivierung/Deaktivierung
- **Database-Viewer**: Direkter Datenbankzugriff über Web-Interface
- **Holiday-Viewer**: Deutsche Feiertage-Integration
- **Debug-Interfaces**: API-Testing und Entwickler-Tools

### Feiertage-System
- **Automatische Generierung**: Deutsche Feiertage 2024-2030
- **Bundesland-spezifisch**: Unterstützung für alle 16 Bundesländer
- **REST-API**: `/api/v1/holidays/*` Endpunkte
- **Kalender-Integration**: Feiertage in Kalenderansicht

### Monitoring & Observability
- **Spring Boot Actuator**: Health, Metrics, Info, Env
- **Custom Health Checks**: Database, System Resources
- **Logging**: Strukturierte Logs mit verschiedenen Levels
- **JVM Metrics**: Memory, GC, Threads
```

---

### **3. 🗄️ Datenbank-Schema**

#### **📄 Dokumentation sagt:**
- Einfache Termin-Tabelle
- Basic User Management

#### **✅ Tatsächlicher Zustand:**
- **Erweiterte Appointment-Felder**: Priority, Category, Reminder, Recurrence
- **User-Status-Management**: Activated, Created/Updated Timestamps
- **Holiday-Tabellen**: Feiertage mit Bundesland-Zuordnung
- **JPA-Relationships**: Proper Foreign Keys und Constraints

#### **🔧 Update erforderlich:**
```markdown
## Erweiterte Datenbank-Struktur

### Appointments Tabelle
```sql
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date_time DATETIME NOT NULL,
    end_date_time DATETIME NOT NULL,
    location VARCHAR(255),
    user_id BIGINT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    category VARCHAR(100),
    reminder_minutes INT DEFAULT 15,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Holidays Tabelle
```sql
CREATE TABLE holidays (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    federal_state VARCHAR(50),
    is_national BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Users Erweitert
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    activated BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```
```

---

### **4. 🔗 API-Endpunkte**

#### **📄 Dokumentation sagt:**
- Basic CRUD für Termine
- Login/Logout

#### **✅ Tatsächlicher Zustand:**
- **Umfassende REST-API** mit über 20 Endpunkten
- **Calendar-Views API**: Month, Week, Day Views
- **Holiday-API**: Deutsche Feiertage
- **Admin-API**: User Management
- **Monitoring-API**: Health Checks, Metrics

#### **🔧 Update erforderlich:**
```markdown
## Vollständige API-Dokumentation

### Appointment Endpoints
- GET    /api/v1/appointments
- POST   /api/v1/appointments
- PUT    /api/v1/appointments/{id}
- DELETE /api/v1/appointments/{id}
- GET    /api/v1/appointments/today
- GET    /api/v1/appointments/upcoming

### Calendar Endpoints
- GET /api/v1/calendar/month/{year}/{month}
- GET /api/v1/calendar/week?date={date}
- GET /api/v1/calendar/day?date={date}

### Holiday Endpoints
- GET /api/v1/holidays/{year}
- GET /api/v1/holidays/{year}/{federalState}
- GET /api/v1/holidays/check?date={date}&federalState={state}

### Admin Endpoints
- GET    /api/v1/admin/users
- PUT    /api/v1/admin/users/{id}/activate
- PUT    /api/v1/admin/users/{id}/deactivate
- DELETE /api/v1/admin/users/{id}

### Monitoring Endpoints
- GET /actuator/health
- GET /actuator/metrics
- GET /actuator/info
- GET /actuator/env
```

---

### **5. 🚀 Deployment & Installation**

#### **📄 Dokumentation sagt:**
- Basic Docker Setup
- Simple MySQL Connection

#### **✅ Tatsächlicher Zustand:**
- **Multi-Environment Setup**: Dev (H2) + Prod (Azure MySQL)
- **Frontend + Backend**: Dual-Port Setup (4200 + 8080)
- **Docker Compose**: Vollständige Container-Orchestrierung
- **Azure Cloud Ready**: Production-ready für Azure

#### **🔧 Update erforderlich:**
```markdown
## Moderne Deployment-Architektur

### Multi-Tier Setup
- **Frontend**: http://localhost:4200 (Angular Dev Server)
- **Backend**: http://localhost:8080 (Spring Boot)
- **Database**: H2 (Dev) / Azure MySQL (Prod)

### Container-Orchestrierung
```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports: ["8080:8080"]
    depends_on: ["mysql"]
  
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: terminkalender
    volumes: ["mysql_data:/var/lib/mysql"]
  
  frontend:
    build: ./frontend
    ports: ["4200:4200"]
    depends_on: ["app"]
```

### Azure Cloud Integration
- **Azure MySQL Flexible Server**
- **App Service für Backend**
- **Static Web Apps für Frontend**
- **Application Insights für Monitoring**
```

---

### **6. 🔐 Security & Authentication**

#### **📄 Dokumentation sagt:**
- Basic JWT Implementation
- Simple Spring Security

#### **✅ Tatsächlicher Zustand:**
- **Advanced JWT Handling**: Refresh Tokens, Secure Storage
- **Role-based Authorization**: USER/ADMIN Rollen
- **CORS Configuration**: Multi-Origin Support
- **Password Security**: BCrypt mit Salt

#### **🔧 Update erforderlich:**
```markdown
## Erweiterte Sicherheitsarchitektur

### JWT-Implementation
```java
@Component
public class JwtUtils {
    private final String SECRET_KEY = "terminkalender-secret-key";
    
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("role", userDetails.getAuthorities())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();
    }
}
```

### Authorization Levels
- **PUBLIC**: Login, Register
- **USER**: Eigene Termine verwalten
- **ADMIN**: Alle Termine, User Management, System Admin

### Frontend Security
```typescript
// HTTP Interceptor für Authorization
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('terminkalender_token');
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next(req);
};
```
```

---

## 📝 **Sofortige Handlungsempfehlungen**

### **1. Architektur-Diagramme aktualisieren**
- **Neue Frontend-Architektur** (Angular 20 + Standalone Components)
- **Erweiterte Backend-Services** (Admin-Tools, Monitoring)
- **Vollständige API-Landschaft** darstellen

### **2. Kapitel ergänzen**
- **Admin-Tools Dokumentation** (fehlt komplett)
- **Feiertage-System** (deutsche Feiertage)
- **Monitoring & Observability** (Actuator)
- **Multi-Environment Setup** (Dev/Prod)

### **3. Technische Details präzisieren**
- **Angular 20 Features** dokumentieren
- **Spring Boot 3.1 Neuerungen** einbauen
- **Azure MySQL Integration** beschreiben
- **Docker Compose Setup** detaillieren

### **4. API-Dokumentation vervollständigen**
- **OpenAPI/Swagger Integration** erwähnen
- **Alle 20+ Endpunkte** dokumentieren
- **Request/Response Beispiele** hinzufügen

---

## ✅ **Fazit**

Die Architektur-Dokumentation ist **ca. 70% veraltet** und muss in folgenden Bereichen **umfassend aktualisiert** werden:

1. **Frontend**: Angular 20 mit modernen Features
2. **Backend**: Erweiterte Admin-Tools und Monitoring
3. **API**: Vollständige Endpunkt-Dokumentation
4. **Deployment**: Multi-Environment und Container-Setup
5. **Security**: Erweiterte JWT und Autorisierung

**Empfehlung**: Systematische Überarbeitung der Dokumentation basierend auf dem aktuellen Code-Stand, um sie für Entwickler, Administratoren und Stakeholder wieder nutzbar zu machen.