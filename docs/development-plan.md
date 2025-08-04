# Entwicklungsplan - Terminkalender Projekt

## 📅 Projekt-Roadmap

### **Phase 1: Grundlagen & Setup** ✅ (Abgeschlossen)
**Zeitraum:** KW 30 (Juli 2025)  
**Status:** ✅ Fertig

- [x] Projekt-Setup mit Spring Boot
- [x] Git Repository erstellen und konfigurieren
- [x] Grundlegende Projektstruktur
- [x] Maven-Konfiguration
- [x] Erste REST-API Endpoint
- [x] Dokumentations-Framework aufsetzen

### **Phase 2: Datenmodell & Grundfunktionalität** 🔄 (Aktuell)
**Zeitraum:** KW 31-32 (Juli/August 2025)  
**Status:** 🔄 In Arbeit

#### **Sprint 2.1: Datenmodell implementieren**
- [ ] JPA Entities für User und Appointment erstellen
- [ ] Repository-Schicht implementieren
- [ ] H2 Database Setup und Konfiguration
- [ ] Grundlegende CRUD-Operationen

#### **Sprint 2.2: Service-Schicht entwickeln**
- [ ] AppointmentService implementieren
- [ ] UserService implementieren
- [ ] Geschäftslogik und Validierung
- [ ] Exception Handling

#### **Sprint 2.3: REST API vervollständigen**
- [ ] Alle CRUD-Endpoints implementieren
- [ ] Request/Response DTOs
- [ ] Input-Validierung
- [ ] Error Handling

**Deliverables:**
- Vollständige CRUD-API für Termine
- Datenbankschema implementiert
- Unit Tests für Service-Schicht

### **Phase 3: Erweiterte Funktionalität** 📋 (Geplant)
**Zeitraum:** KW 33-34 (August 2025)

#### **Sprint 3.1: Kalenderansichten**
- [ ] MonthView API entwickeln
- [ ] WeekView API entwickeln
- [ ] DayView API entwickeln
- [ ] Kalender-Service implementieren

#### **Sprint 3.2: Such- und Filterfunktionen**
- [ ] Volltext-Suche implementieren
- [ ] Datum-Filter
- [ ] Ort-Filter
- [ ] Erweiterte Suchkriterien

#### **Sprint 3.3: Terminkonflikt-Management**
- [ ] Überschneidungsprüfung
- [ ] Konflikt-Resolution
- [ ] Alternative Terminvorschläge

**Deliverables:**
- Kalender-API mit Monats-/Wochenansicht
- Such- und Filterfunktionalität
- Konfliktmanagement

### **Phase 4: Frontend & UI** 🎨 (Geplant)
**Zeitraum:** KW 35-36 (August/September 2025)

#### **Sprint 4.1: Frontend-Setup**
- [ ] Frontend-Framework wählen (React/Angular/Vue)
- [ ] Projekt-Setup und Build-Pipeline
- [ ] API-Client generieren

#### **Sprint 4.2: Grundlegende UI-Komponenten**
- [ ] Terminliste-Komponente
- [ ] Termin-Formular
- [ ] Kalender-Widget
- [ ] Navigation und Layout

#### **Sprint 4.3: Erweiterte UI-Features**
- [ ] Drag & Drop für Termine
- [ ] Responsive Design
- [ ] Dark Mode
- [ ] Accessibility (a11y)

**Deliverables:**
- Funktionsfähige Webanwendung
- Responsive Design
- Benutzerfreundliche Oberfläche

### **Phase 5: Sicherheit & Performance** 🔒 (Geplant)
**Zeitraum:** KW 37-38 (September 2025)

#### **Sprint 5.1: Authentifizierung & Autorisierung**
- [ ] JWT-basierte Authentifizierung
- [ ] User Registration/Login
- [ ] Password Hashing (BCrypt)
- [ ] Role-based Access Control

#### **Sprint 5.2: Performance-Optimierung**
- [ ] Database Indexing
- [ ] Query-Optimierung
- [ ] Caching-Strategie (Redis)
- [ ] Connection Pooling

#### **Sprint 5.3: Sicherheit**
- [ ] Input Sanitization
- [ ] CORS-Konfiguration
- [ ] HTTPS-Setup
- [ ] Security Headers

**Deliverables:**
- Sichere Benutzerauthentifizierung
- Performance-optimierte Anwendung
- Security Best Practices implementiert

### **Phase 6: Testing & Quality Assurance** 🧪 (Geplant)
**Zeitraum:** KW 39-40 (September/Oktober 2025)

#### **Sprint 6.1: Unit Testing**
- [ ] Service-Tests implementieren
- [ ] Repository-Tests
- [ ] Controller-Tests
- [ ] Test Coverage > 80%

#### **Sprint 6.2: Integration Testing**
- [ ] API-Integration Tests
- [ ] Database-Integration Tests
- [ ] End-to-End Tests
- [ ] TestContainers Setup

#### **Sprint 6.3: Performance & Load Testing**
- [ ] Performance Benchmarks
- [ ] Load Testing (JMeter)
- [ ] Memory Profiling
- [ ] Database Performance Testing

**Deliverables:**
- Comprehensive Test Suite
- Performance Benchmarks
- Quality Metrics Dashboard

### **Phase 7: Deployment & Documentation** 🚀 (Geplant)
**Zeitraum:** KW 41-42 (Oktober 2025)

#### **Sprint 7.1: Deployment Pipeline**
- [ ] CI/CD Pipeline (GitHub Actions)
- [ ] Docker-Container
- [ ] Production Database Setup (PostgreSQL)
- [ ] Monitoring & Logging

#### **Sprint 7.2: Finale Dokumentation**
- [ ] API-Dokumentation (OpenAPI/Swagger)
- [ ] Deployment Guide
- [ ] User Manual
- [ ] Developer Documentation

#### **Sprint 7.3: Präsentationsvorbereitung**
- [ ] Demo-Environment Setup
- [ ] Präsentationsfolien
- [ ] Live-Demo vorbereiten
- [ ] Code-Review und Cleanup

**Deliverables:**
- Production-ready Application
- Complete Documentation
- Deployment Pipeline
- Präsentation

## 📊 Meilensteine

| Meilenstein | Datum | Beschreibung | Status |
|-------------|-------|-------------|---------|
| **M1: Projekt-Setup** | KW 30 | Git, Spring Boot, Dokumentation | ✅ |
| **M2: MVP Backend** | KW 32 | CRUD API, Database | 🔄 |
| **M3: Erweiterte API** | KW 34 | Kalender, Suche, Filter | 📋 |
| **M4: Frontend MVP** | KW 36 | Grundlegende UI | 📋 |
| **M5: Security & Auth** | KW 38 | Authentifizierung | 📋 |
| **M6: Testing Complete** | KW 40 | Alle Tests implementiert | 📋 |
| **M7: Production Ready** | KW 42 | Deployment, Dokumentation | 📋 |

## 🎯 Definition of Done

### **Feature-Level DoD:**
- [ ] Funktionalität implementiert und getestet
- [ ] Unit Tests geschrieben (Coverage > 80%)
- [ ] Integration Tests vorhanden
- [ ] Code Review durchgeführt
- [ ] Dokumentation aktualisiert
- [ ] API-Dokumentation erweitert

### **Sprint-Level DoD:**
- [ ] Alle User Stories abgeschlossen
- [ ] Keine kritischen Bugs
- [ ] Performance-Tests bestanden
- [ ] Security-Scan durchgeführt
- [ ] Demo-fähige Version
- [ ] Git-Branch merged

### **Release-Level DoD:**
- [ ] Alle Features funktional
- [ ] End-to-End Tests erfolgreich
- [ ] Performance-Benchmarks erfüllt
- [ ] Security-Audit bestanden
- [ ] Dokumentation vollständig
- [ ] Deployment erfolgreich

## 📋 Backlog Priorisierung

### **High Priority (Must Have)**
1. **CRUD Operations für Termine**
2. **Benutzer-Management**
3. **Kalender-Ansichten (Monat/Woche)**
4. **Such- und Filterfunktionen**
5. **REST API Vollständigkeit**

### **Medium Priority (Should Have)**
6. **Frontend Web-Interface**
7. **Authentifizierung & Autorisierung**
8. **Terminkonflikt-Erkennung**
9. **Performance-Optimierung**
10. **Comprehensive Testing**

### **Low Priority (Could Have)**
11. **Mobile App**
12. **Email-Benachrichtigungen**
13. **Kalender-Export (iCal)**
14. **Advanced Reporting**
15. **Multi-Tenancy**

## 🔧 Technische Schulden & Refactoring

### **Aktuelle technische Schulden:**
- Vereinfachte Controller-Implementierung
- Fehlende Input-Validierung
- Minimale Error-Handling
- Keine Authentifizierung

### **Geplante Refactoring-Aktivitäten:**
- **KW 33:** Controller-Schicht erweitern und validieren
- **KW 35:** Exception-Handling framework implementieren
- **KW 37:** Security-Refactoring für Authentication
- **KW 39:** Performance-Optimierung und Code-Cleanup

## 📈 Risiko-Management

### **Identifizierte Risiken:**
1. **Zeitdruck bei Frontend-Entwicklung** (Hoch)
   - *Mitigation:* Früh mit UI-Prototyping beginnen
2. **Komplexität der Terminkonflikt-Logik** (Mittel)
   - *Mitigation:* Einfache Algorithmen zuerst implementieren
3. **Database Performance bei vielen Terminen** (Mittel)
   - *Mitigation:* Frühzeitige Performance-Tests
4. **Integration verschiedener Komponenten** (Niedrig)
   - *Mitigation:* Continuous Integration Pipeline

### **Kontingenz-Pläne:**
- **Plan B für Frontend:** Falls Zeit knapp wird, nur REST API mit Swagger UI
- **Plan B für Authentication:** Basic Authentication statt JWT
- **Plan B für Performance:** Einfache Implementierung ohne Caching

## 📞 Team & Verantwortlichkeiten

### **Rollen & Verantwortlichkeiten:**
- **Backend Development:** Hauptfokus
- **Frontend Development:** Sekundärer Fokus
- **Database Design:** Integriert in Backend
- **Documentation:** Kontinuierlich
- **Testing:** Test-Driven Development
- **DevOps:** Einfache CI/CD Pipeline

### **Review & Meeting Schedule:**
- **Sprint Planning:** Zu Beginn jeder Phase
- **Daily Standups:** Bei Bedarf (Solo-Projekt)
- **Sprint Review:** Ende jeder Phase
- **Retrospective:** Nach größeren Meilensteinen

Dieser Entwicklungsplan bietet eine strukturierte Herangehensweise für das Uni-Projekt mit klaren Meilensteinen und realistischen Zeitplänen. 📅
