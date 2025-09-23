# Architektur-Dokument (arc42) – Terminkalender-System

> Version: 1.0 (Vollständige arc42-Ausarbeitung)  
> Status: In Arbeit  
> Autoren: Team SWTP1  
> Letzte Aktualisierung: 2025-09-22

---
## 1. Einleitung und Ziele
### 1.1 Aufgabenstellung
Die Terminkalender-Anwendung ermöglicht das Verwalten, Suchen und Visualisieren von Terminen über eine REST-basierte Webschnittstelle. Sie bietet CRUD-Funktionalität für Termine, Kalenderansichten (Monat/Woche), Such- und Filteroperationen, rudimentäre Erinnerungslogik sowie Benutzerregistrierung und Authentifizierung via JSON Web Token (JWT). Das System wurde im Rahmen eines universitären Softwaretechnik-Projektes entwickelt und legt Wert auf Erweiterbarkeit (z. B. zukünftige Notification-, Export- oder Kollaborationsmodule) und Sicherheit (Zugriffsschutz auf nutzerspezifische Daten). 

Die konkrete Implementierung erfolgt in Java 17 mithilfe von Spring Boot. Zentrale technische Artefakte liegen im Paket `de.swtp1.terminkalender` (siehe Quellstruktur unter `src/main/java/...`).

### 1.2 Qualitätsziele (Top 5 – vorläufig)
| Priorität | Qualitätsziel | Beschreibung | Metriken/Indikatoren |
|-----------|---------------|--------------|----------------------|
| 1 | Sicherheit | Schutz von Termindaten und Benutzerkonten | JWT-Integrität, OWASP Top 10 Checks |
| 2 | Wartbarkeit | Saubere Schichtung & geringe Kopplung | SonarQube Issues, Zyklomatische Komplexität |
| 3 | Erweiterbarkeit | Neue Termin-Funktionen ohne Architekturbruch | LOC pro Feature, Anzahl modifizierter Module |
| 4 | Performance | Antwortzeit < 2s (95% P95) | API-Metriken, Actuator Traces |
| 5 | Testbarkeit | Hohe Abdeckung kritischer Logik | Line/Branch Coverage, Mutation Score |

Die Feinjustierung (z. B. konkrete Performance-SLIs, Security-Testkatalog) steht noch aus und richtet sich nach Evaluationsphase und Nutzerfeedback.

### 1.3 Qualitätsbaum (Quality Tree)
Der Qualitätsbaum strukturiert die wichtigsten Qualitätsaspekte und ihre Unterziele als Orientierung für Design- und Implementierungsentscheidungen:
```
Qualität
 ├─ Sicherheit
 │   ├─ Authentizität (JWT Integrität)
 │   ├─ Vertraulichkeit (Zugriff nur Eigentümer)
 │   ├─ Geheimnisschutz (Secret Externalisierung)
 │   └─ Resilienz (Rate Limiting geplant)
 ├─ Wartbarkeit
 │   ├─ Geringe Kopplung (Layering)
 │   ├─ Lesbarkeit (Namenskonventionen)
 │   └─ Testbarkeit (DTO Trennung, Services isolierbar)
 ├─ Erweiterbarkeit
 │   ├─ Modularität (neue Controller/Services)
 │   ├─ Evolvierbares Datenmodell (Recurrence Platzhalter)
 │   └─ Crosscutting Isolierung (Security, Logging)
 ├─ Performance
 │   ├─ Antwortzeit (P95 <2s)
 │   ├─ Ressourcennutzung (kein Overhead durch Sessions)
 │   └─ Effiziente Queries (indizierbare Filter)
 └─ Beobachtbarkeit
   ├─ Logging (Strukturiert, korrelierbar)
   ├─ Health & Metrics (Actuator geplant)
   └─ Traceability (Audit Logging geplant)
```

### 1.3 Stakeholder
| Rolle | Interesse | Relevante Aspekte |
|-------|-----------|-------------------|
| Endbenutzer | Termine verwalten | Usability, Performance |
| Administrator | Betrieb, Monitoring | Logs, Health, Backups |
| Entwicklerteam | Weiterentwicklung | Codequalität, Tests |
| Product Owner | Wertsteigerung | Roadmap, Feature Velocity |
| Sicherheitsteam (zukünftig) | Schutzmaßnahmen | AuthN/Z, Audit Logs |

---
## 2. Randbedingungen
### 2.1 Technische Randbedingungen
Die folgenden technischen Rahmenbedingungen bestimmen Architektur- und Implementierungsentscheidungen:

- Programmiersprache: Java 17 (LTS) – Nutzung moderner Sprachfeatures ohne Abhängigkeit auf preview.
- Framework: Spring Boot (Spring Web, Spring Security, Spring Data JPA) – vereinfacht Inversion of Control, Security-Konfiguration und Persistenz.
- Datenbanken: H2 (Entwicklung) in Datei-/Memory-Mode (`application.yml`), PostgreSQL für Produktion (`application-prod.properties`). Persistenzschicht abstrahiert über JPA.
- Build: Maven (siehe `pom.xml`) – Standard-Plugins, Möglichkeit zur Integration von Plugins wie Flyway/JaCoCo künftig.
- Deployment: Aktuell lokaler Start via `mvn spring-boot:run`, Containerisierung vorbereitet über `Dockerfile` im Repository-Wurzelverzeichnis.
- Security-Secret: In `AuthService` hardcodiert generiertes JWT-Secret (Entwicklungsmodus) – muss produktiv externalisiert werden (ENV / Vault).

### 2.2 Organisatorische Randbedingungen
- Studentisches Projekt (SWTP1) (Zeitrahmen 4 Monate)
- Iterative Entwicklung (2‑Wochen Iterationen), keine formale SLA
- GitHub Repository als Single Source of Truth
- Architekturentscheidungen werden via ADRs dokumentiert (lightweight)

### 2.3 Konventionen
- Paketstruktur nach Layern (`controller`, `service`, `repository`, `entity`, `dto`, `config`, `util`). Dateien wie `AppointmentController.java`, `AppointmentService.java`, `AppointmentRepository.java` spiegeln diese Layerung.
- REST-Namenskonventionen (Plural-Ressourcen, Versionierung: `/api/v1/appointments`, `/api/v1/auth/...`).
- DTO-Präfixe: `*RequestDto`, `*ResponseDto` (z. B. `AppointmentRequestDto.java`, `AppointmentResponseDto.java`).
- Security: Filter + Konfiguration in `config` (`SecurityConfig.java`, `JwtAuthenticationFilter.java`).
- Logging derzeit unvereinheitlicht (mehrere `System.out.println` in `AppointmentService.java`) – Zielzustand: SLF4J + zentraler Logger.

### 2.4 Beschränkungen / Annahmen
- Keine verteilte Deployment-Topologie aktuell, Single-Node.
- Benutzerverwaltung minimal (keine Passwort-Policy Durchsetzung, kein E-Mail-Verifikationsprozess implementiert).
- Zeit- und Zeitzonenkonvertierung vereinfacht (`Europe/Berlin` als Default in `User.java`).

---
## 3. Kontextabgrenzung
### 3.1 Fachlicher Kontext (vereinfacht)
```
[Benutzer] --(Termine anlegen/verwalten / Authentifizieren)--> [Terminkalender-System]
  |                                                    |
  +----> (HTML/JS Seiten: `index.html`, `login.html`)  +----> Persistenz (H2/ Postgres)
```
Das System verarbeitet individuelle Termine mit optionalen Metadaten (Priorität, Kategorie, Farbe, Erinnerung, Wiederkehr). Nutzerinteraktion erfolgt gegen REST-Endpunkte (`AppointmentController`, `AuthController`, `CalendarController`).

Geplante Integrationen:
- E-Mail/Push-Benachrichtigungen (externes Notification-Modul)
- Export (iCalendar/ICS) für Synchronisation
- Regionale Feiertagslogik (Teile vorbereitet durch `Holiday`-Entity und `HolidayService` – Dateien nicht vollständig analysiert in dieser Revision)

### 3.2 Technischer Kontext
| Externes System | Richtung | Protokoll | Zweck | Status |
|-----------------|----------|----------|-------|--------|
| Browser (Static HTML/JS) | ↔ | HTTP/JSON | Frontend Zugriff | aktiv |
| H2 Console | ↔ | HTTP | Dev-Diagnose | aktiv |
| PostgreSQL | ← | JDBC | Produktionsspeicher | geplant |
| Feiertagsdaten (lokal Entity) | - | - | Wird aktuell intern gepflegt | aktiv |
| Benachrichtigungen (E-Mail) | → | SMTP/REST | Erinnerungen | geplant |

---
## 4. Lösungsstrategie
Die Lösungsstrategie leitet sich aus Einfachheit, Erweiterbarkeit und klaren Verantwortlichkeiten ab.

| Aspekt | Strategy / Begründung | Konkrete Dateien |
|--------|-----------------------|------------------|
| Architektur-Stil | Klassische Layer-Architektur zur Trennung von Web/API, Geschäftslogik, Persistenz | `controller/*`, `service/*`, `repository/*` |
| Sicherheit | Stateless Auth mit JWT zur horizontalen Skalierung ohne Session-Replikation | `AuthService.java`, `JwtAuthenticationFilter.java`, `SecurityConfig.java` |
| Persistenz | JPA zur schnellen Entwicklung; Entities kapseln Felder und Lifecycle | `Appointment.java`, `User.java`, `Holiday.java` |
| Validierung | Bean Validation für DTO/Entity Constraints | Annotationen in `Appointment.java`, `User.java`, `AppointmentRequestDto.java` |
| DTO / Mapping | Manuelles Mapping (geringe Komplexität) | `AppointmentService.java` (convert-Methoden) |
| Erinnerung / Scheduling | Geschäftslogik vorhanden, Trigger noch nicht implementiert | `AppointmentService#getUpcomingReminders()` |
| Wiederkehrende Events | Modellierung vorbereitet, Logik ausstehend | Felder in `Appointment.java` (`isRecurring`, `recurrenceType`) |
| Fehlerbehandlung | Lokale try/catch in Controllern, globaler Handler geplant | `AppointmentController.java`, `AuthController.java` |
| Konfiguration | Profile + Security/CORS Konfiguration | `application.yml`, `application-prod.properties`, `SecurityConfig.java` |
| Beobachtbarkeit | Geplant: Actuator Integration | (noch nicht vorhanden) |
| Deployment | Containerisierung vorbereitet | `Dockerfile` |

---
## 5. Bausteinsicht
### 5.1 Übersicht (Level 1)
```
Client (Browser / HTTP Tool)
   -> Controller Layer (z. B. AppointmentController.java,
            AuthController.java,
            CalendarController.java)
    -> Service Layer (AppointmentService.java,
          AuthService.java,
          CalendarService.java)
     -> Repository Layer (AppointmentRepository.java,
               UserRepository.java,
               HolidayRepository.java)
      -> Datenbank (H2 / PostgreSQL)

Security-Pipeline: HttpSecurity (SecurityConfig.java) + JwtAuthenticationFilter.java
```

### 5.2 Layer & Verantwortlichkeiten (Fließtext)
Der Controller-Layer (Dateien in `controller/`) kapselt ausschließlich HTTP-spezifische Belange: Request-Parameter, Statuscodes, einfache Validierungen. Beispiel: `AppointmentController.java` bündelt Filterung, Pagination und Suchoperationen für Termine; `AuthController.java` stellt Login/Registrierung/Token-Validierung zur Verfügung. 

Der Service-Layer (Dateien in `service/`) implementiert Geschäftsregeln wie Terminüberschneidungsprüfung (`AppointmentService#checkForOverlappingAppointments`), Validierung von Zeitlogik (`validateAppointmentTime`) sowie Authentifizierungsprozesse (`AuthService#login`, `AuthService#register`). Mapping zwischen Entities und DTOs erfolgt manuell innerhalb der Services – diese Entscheidung reduziert externe Abhängigkeiten und bleibt flexibel für spätere Einführung eines Mappers (z. B. MapStruct). 

Das Repository-Layer (Dateien in `repository/`) kapselt Persistenzzugriffe. `AppointmentRepository.java` enthält mehrere spezialisierte JPQL-Queries (`findUserAppointmentsInDateRange`, `findOverlappingAppointments`, `searchUserAppointments`). `UserRepository.java` (nicht vollständig ausgelesen in dieser Revision, aber im Projekt enthalten) stellt Benutzerabfragen bereit. 

### 5.3 Zentrale Klassen (Auswahl mit Kontext)
| Klasse | Rolle | Relevante Methoden | Bemerkung |
|--------|------|--------------------|-----------|
| `AppointmentService.java` | Geschäftslogik Termine | `createAppointment`, `updateAppointment`, `getUpcomingReminders` | Enthält Debug-Logging via `System.out` |
| `AuthService.java` | Authentifizierung & JWT | `login`, `register`, `generateToken` | Secret derzeit im Code generiert |
| `JwtAuthenticationFilter.java` | HTTP Filter | `doFilterInternal` | Setzt SecurityContext bei gültigem Token |
| `SecurityConfig.java` | Security-Konfiguration | `filterChain` | Definiert Public/Protected Pfade |
| `AppointmentRepository.java` | Datenzugriff | Diverse @Query Methoden | Performance-Potential durch Indizes |
| `AppointmentController.java` | API Endpoints | `getAllAppointments`, `createAppointment` | Kombiniert mehrere Filterstrategien |

### 5.4 Persistenzmodell (Detail) 
Die wichtigsten Entities liegen in `entity/`:
- `Appointment.java`: Enthält Termin-Attribute und Erweiterungen (Priorität, Erinnerung, Wiederholung, Kategorie, Farbe). Lifecycle via `@PrePersist`, `@PreUpdate`. 
- `User.java`: Enthält Benutzerinformationen inklusive Kleinst-Rollenmodell (`UserRole` Enum), Sicherheitsattribute (Login-Versuche, Konto-Lock) und benutzerbezogene Einstellungen (Standard-Erinnerungszeit, Zeitzone). 
- `Holiday.java`: (Datei vorhanden – Annahme: Modellierung von Feiertagen; Integration für Filterung geplanter Logik). 

Die Beziehungen sind aktuell indirekt (Appointments referenzieren `userId` statt einer `@ManyToOne`-Relation), was Kopplung reduziert, aber auf Kosten von Typensicherheit/Referenzintegrität (kein FK-Constraint auf Objekt-Ebene) – zukünftige Verfeinerung möglich.

### 5.5 Sicherheitsdurchlauf (konkreter Ablauf)
1. Anfrage trifft ein (z. B. `GET /api/v1/appointments`).
2. `JwtAuthenticationFilter#doFilterInternal` prüft `Authorization` Header. Falls kein gültiges `Bearer`-Token vorliegt → Weiterleitung ohne Authentifizierung.
3. Bei gültigem Token wird Nutzerkennung (`username`) extrahiert (`AuthService#extractUsername`) und im SecurityContext abgelegt.
4. Controller ruft Service auf; `AppointmentService#getCurrentUser()` nutzt `SecurityUtils.getCurrentUsername()` (Datei in `util/` – Annahme) zur Ermittlung des aktuellen Nutzers und validiert Ownership bei Abfragen/Updates.
5. Antwort wird als DTO serialisiert (Mapping innerhalb Service).

### 5.6 Querschnittliche Aspekte innerhalb Bausteinen
- Validierung: Annotationen in `Appointment.java`, `User.java`, DTOs (`AppointmentRequestDto.java`).
- Fehlerbehandlung: In Controllern lokal (`try/catch`), fehlender zentraler Handler ist Risiko (siehe Kapitel 11).
- Erweiterbarkeit: Neue Funktionsdomänen (z. B. Export) können als zusätzliche Controller-/Service-Paare ergänzt werden, ohne bestehende Pakete zu verletzen.

### 5.6 Hauptpakete und Verantwortlichkeiten (Konsolidiert)
| Paket | Verantwortung |
|-------|---------------|
| `controller` | REST Endpoints (Appointment, Auth, Calendar, Debug, Holiday, Database) |
| `service` | Geschäftslogik (Terminregeln, Validierung, Erinnerungssuche) |
| `repository` | JPA Zugriff & Custom Queries |
| `entity` | Persistente Domänenobjekte |
| `dto` | Request/Response Transferobjekte |
| `config` | Security + CORS + Initialisierung |
| `util` | Hilfsklassen (Security, Formatierung – TBD) |

### 5.7 Wichtige Klassen (Auszug)
| Klasse | Rolle |
|--------|------|
| `AppointmentService` | CRUD, Konsistenz- & Überschneidungsprüfung, Reminder-Funktion |
| `AuthService` | Token-Ausstellung & Validierung |
| `JwtAuthenticationFilter` | Extrahiert/prüft JWT aus Header |
| `SecurityConfig` | Security-Regeln & Filterkette |
| `AppointmentController` | Termine API (Filter, Suche, Paginierung) |
| `CalendarService` | Aggregationen (Monat/Woche) |

### 5.8 Persistenzmodell (vereinfacht)
```
User (1) ---- (n) Appointment
Holiday (autonom, referenziert nicht User direkt)
```
Felder wie `reminderMinutes`, `recurring`, `recurrenceType`, `category`, `colorCode` erweitern Basismodel.

### 5.9 Querschnitt: Sicherheitsdurchlauf

### 5.10 Level 2 Bausteine (Detail ausgewählter Services)
| Baustein | Innere Struktur | Beschreibung | Erweiterungspunkt |
|----------|-----------------|--------------|------------------|
| AppointmentService | Validierung, Mapping, OverlapCheck | Zentrale Geschäftslogik Termine | Recurrence Expansion Hook |
| AuthService | Credential Prüfung, Token Builder | Authentifizierung / Token Erstellung | Secret Provider Austausch |
| CalendarService | Aggregationslogik (Tag/Monat) | Gruppiert Termine für Views | Neue Zeiträume (Quartal) |
| HolidayService | Lookup von Feiertagen | Filter-/Info-Unterstützung | Externe API Integration |
| JwtAuthenticationFilter | Header Parsing, Token Validation | Security Einstiegspunkt | Weitere Tokenverfahren |
| DataInitializer | Seed Daten (Dev) | Entwicklungsunterstützung | Abschaltbar in Prod |

### 5.11 Sequenzbeziehungen (Textuelle Beschreibung)
1. Login: Client → AuthController → AuthService (User Lookup, Passwortprüfung) → Token zurück → Client speichert.
2. Authentifizierte Abfrage: Client sendet Bearer Token → Filter validiert → Controller → Service → Repository → DTO Antwort.
3. Termin-Erstellung mit Overlap: Service lädt potentielle Konflikte (`AppointmentRepository.findOverlappingAppointments`) → bei Treffern Exception (409) → Controller mappt zu HTTP Response.
4. Reminder Polling: Client fragt `/appointments/reminders` → Service berechnet Zeitfenster (jetzt, +60min) → Repository Abfrage → Filterung nach Erinnerung → Rückgabe Liste.

### 5.12 Abgrenzung nicht implementierter Bausteine
| Platzhalter | Status | Begründung |
|-------------|--------|-----------|
| Scheduler (Spring) | offen | Polling zunächst ausreichend |
| NotificationAdapter | offen | Erst nach Scheduler Nutzen |
| ExportService (ICS) | offen | Geringe Priorität initial |
| AuditLogService | offen | Mehrwert nach Rollout höher |
| RoleAuthorization | teilweise (Enum) | Feature-Tiefe aktuell gering |
1. Request → `JwtAuthenticationFilter`
2. Token extrahieren und validieren via `AuthService`
3. SecurityContext setzen
4. Controller verarbeitet Anfrage

---
## 6. Laufzeitsicht (Szenarien)
### 6.1 Termin erstellen (`POST /api/v1/appointments`)
1. `AppointmentController#createAppointment()` validiert Eingabe (`@Valid AppointmentRequestDto`).
2. `AppointmentService#createAppointment()` ruft `getCurrentUser()` auf und verhindert Fremdzuordnungen.
3. Zeitlogik via `validateAppointmentTime()` (Start vor Ende, nicht in Vergangenheit).
4. Überschneidungsprüfung über `AppointmentRepository#findOverlappingAppointments()`.
5. Speicherung via `appointmentRepository.save()` – Entity Lifecycle setzt Timestamps.
6. Rückgabe als `AppointmentResponseDto`.

### 6.2 Termin aktualisieren (`PUT /api/v1/appointments/{id}`)
1. Controller delegiert an `updateAppointment()`.
2. Ownership-Prüfung: Termin gehört aktuellem Benutzer? (`existingAppointment.getUserId()` vs. Auth User ID).
3. Erneute Überschneidungsprüfung ohne sich selbst (`checkForOverlappingAppointmentsExcludingCurrent`).
4. Persistenz & DTO-Rückgabe.

### 6.3 Erinnerungsabruf (`GET /api/v1/appointments/reminders`)
1. Controller ruft `AppointmentService#getUpcomingReminders()`.
2. Zeitraum: Jetzt bis +60 Minuten (Hardcoded im Service – Parameterisierung offen).
3. Filter: Nur Termine mit `reminderMinutes` != null/0 und deren Erinnerungszeit <= jetzt < Erinnerungszeit + 1 Minute.
4. Rückgabe: Liste `AppointmentResponseDto`.

### 6.4 Authentifizierung (`POST /api/v1/auth/login`)
1. `AuthController#login()` ruft `AuthService#login()`.
2. E-Mail Lookup, Passwort-Hash-Prüfung via `PasswordEncoder`.
3. Sperrlogik bei Fehlversuchen (`loginAttempts`, `accountLocked`).
4. JWT-Erstellung (`generateToken()` mit Claims: userId, username, role).
5. Rückgabe `AuthResponseDto` inkl. Embedded `UserDto`.

### 6.5 Token Validierung (`GET /api/v1/auth/validate`)
Filter greift nicht (Whitelist in `JwtAuthenticationFilter#shouldNotFilter`), Controller parst Token und ruft `AuthService#validateToken()`.

### 6.6 Kalender Monatsansicht (`GET /api/v1/calendar/month`)
`CalendarController` (Datei vorhanden – Detailanalyse ausstehend) delegiert an `CalendarService` für Aggregation gruppierter Termine pro Tag.

### 6.7 Fehlerfall Termin nicht gefunden
`findAppointmentById()` liefert leere Optional → Controller erzeugt 404. Einheitliche Fehlerstruktur für alle Controller fehlt (siehe Risiken).

---
## 7. Verteilungssicht
Die Anwendung wird gegenwärtig in einer einfachen Entwicklungsumgebung betrieben: Eine einzelne Spring‑Boot‑Instanz mit eingebetteter H2-Datenbank sowie lokalen statischen HTML/JavaScript-Seiten. Aus architektureller Sicht existiert damit eine klassische Monolith-Topologie ohne externe Nebenkomponenten wie Message Broker oder Caching Layer. Für das Zielbild Produktion ist vorgesehen, die Anwendung containerisiert zu betreiben und mit einer relationalen MySQL-Instanz (Azure Database for MySQL) beziehungsweise – als frühere Option – PostgreSQL zu verbinden. Vor einen später möglichen Lastverteiler (Load Balancer) kann ein leichtgewichtiger Reverse Proxy (z. B. Nginx) treten, der TLS terminiert und statische Ressourcen effizient ausliefert. Ein vorhandenes `Dockerfile` bildet bereits die Grundlage für Container-Builds; ein kontinuierlicher Auslieferungsprozess (CI/CD) wurde jedoch noch nicht definiert und bleibt ausdrücklich Teil späterer Iterationen.

---
## 8. Querschnittliche Konzepte
Die Authentifizierung basiert bewusst auf einem stateless Ansatz mit JSON Web Tokens, die in einem Sicherheitsfilter (`JwtAuthenticationFilter`) entgegengenommen, validiert und anschließend im Security Context hinterlegt werden. Eine darüber hinausgehende Autorisierung im Sinne feingranularer Rollenprüfung ist momentan nicht implementiert; zwar existiert ein elementares Rollenmodell als Enumeration in der Benutzer-Entität, es wird jedoch noch nicht zur Durchsetzung differenzierter Zugriffsbeschränkungen verwendet. Validierung von Eingabedaten geschieht deklarativ über Bean‑Validation Annotationen an Entitäten und DTOs, wodurch Konsistenz- und Pflichtfeldprüfungen früh im Verarbeitungsfluss greifen. Die Trennung zwischen externen Repräsentationen und Domänenobjekten erfolgt strikt: Request- und Response‑DTOs werden manuell in den Services gemappt. Diese Entscheidung minimiert Abhängigkeiten von Mapping-Frameworks und bleibt aufgrund der vergleichsweise geringen Objektkomplexität vertretbar.

### 8.1 Security
JWT Stateless Auth, zukünftige Ergänzung: Secret Rotation, Rollenbasierte Checks (`@PreAuthorize`), optionale Rate Limiting Schicht (Bucket4j / Redis).

### 8.2 Validation
Bean Validation (JSR 380), zentrale Fehlerformatierung demnächst via globalem Handler; Ziel: einheitliches Fehlerobjekt (code, message, fieldErrors[]).

### 8.3 Logging & Observability
Geplant: SLF4J + strukturierte Felder (userId, requestId). Actuator für Health/Metrics/Info. Perspektive: OpenTelemetry Export (OTLP) → Collector.

### 8.4 Fehlerbehandlung
Aktuell Controller-lokal; geplanter `@ControllerAdvice` kapselt Mapping DomainException → HTTP Code (409 Overlap, 400 Validation, 404 NotFound).

### 8.5 Konfiguration & Secrets
Profile (`dev`, `prod`); Secrets via ENV (JWT, DB). Später: Vault / KeyVault Option. Konsistenz: keine Secrets im Repo.

### 8.6 Internationalisierung (i18n)
Nicht im Scope. Fehlermeldungen Englisch/Deutsch gemischt → später Vereinheitlichung.

### 8.7 Caching
Bewusst nicht aktiv. Evaluierung nach Messung Hot Paths (z. B. Termin-Listen). Kandidat: Spring Cache + Redis.

### 8.8 Scheduling
Noch nicht implementiert. Geplant: `@Scheduled` Job für Reminder Scans (→ Notification Adapter). Entkopplung per Service Interface.

### 8.9 Erweiterbarkeit DTO / API Versionierung
Namenskonvention `/api/v1`. Für Breaking Changes: Parallel-Betrieb `/api/v2` + Deprecation Header.

### 8.10 Security Hardening (Geplant)
- HTTP Security Headers (CSP, HSTS) via Filter.
- Token Lifetime Reduktion + Refresh Token Konzept (optional).
- Audit Trail (CREATE/UPDATE/DELETE Termine).

Fehler werden derzeit vorwiegend lokal in Controllern behandelt; ein globaler Exception Handler ist geplant, um Vereinheitlichung von Fehlerrückgaben und Logikredundanzen zu erreichen. Die Protokollierung verwendet stellenweise noch direkte Konsolenausgaben (`System.out`), was langfristig durch einen konsistenten SLF4J/Logback Ansatz mit strukturierbaren Feldern ersetzt wird. Konfigurationen differenzieren Entwicklungs- und Produktionsaspekte über Profile; sicherheitsrelevante Secrets (z. B. das JWT-Schlüsselmaterial) sind aktuell noch nicht externalisiert und bilden damit eine erkennbare technische Schuld. Internationalisierung sowie UI-nahe Lokalisierung sind bewusst kein Bestandteil des aktuellen Scopes. Ebenfalls nicht umgesetzt ist ein Caching-Layer – erste Performanceziele lassen sich ohne zusätzliche Komplexität erfüllen, sodass eine spätere Evaluierung (z. B. Redis für häufige Lesezugriffe auf Terminlisten) ausreichend ist. Ein dedizierter Scheduler für Erinnerungen ist noch nicht vorhanden; Polling-Logik ersetzt vorerst jede pushbasierte Benachrichtigung. Felder zur Unterstützung wiederkehrender Termine existieren bereits im Datenmodell, ohne dass Expansion oder Serienlogik implementiert wäre. Sicherheits-Härtungen wie Rate Limiting, Audit Logging oder eine differenziertere Ressourcen-Autorisierung sind als mittel- bis langfristige Ausbaustufen vorgesehen. Die Persistenzabstraktion stützt sich auf Spring Data JPA mit gezielt formulierten Query-Methoden – ein Schritt, der Entwicklungsbeschleunigung vor Modellstabilität priorisiert. Abschließend bleibt das Handling des Token-Secrets in der Anwendungslaufzeit ein bewusst markierter Verbesserungsbereich, dessen Anpassung (ENV-Injektion, Rotation) vor einem produktiven Deployment erfolgen muss.

---
## 9. Architekturentscheidungen (ADRs light)
| ID | Entscheidung | Kontext | Status | Konsequenz |
|----|-------------|---------|--------|------------|
| ADR-1 | JWT + Stateless | Skalierbarkeit, Einfachheit | aktiv | Kein Server-Session-Invalidation |
| ADR-2 | Spring Data JPA | Schnelle Entwicklung | aktiv | Abhängigkeit ORM, Lazy Pitfalls |
| ADR-3 | Layered Arch | Überschaubare Domäne | aktiv | Zusätzliche Abstraktion |
| ADR-4 | H2 für Dev | Schnelle Iteration | aktiv | Divergenz zu Prod-DB beachten |
| ADR-5 | Einfache DTO-Mapping manuell | Kleine Codebasis | aktiv | Später Mapper-Framework möglich |

(Detail-ADRs können bei wachsender Komplexität ausgelagert werden.)

### 9.1 ADR Verzeichnis
Die folgenden Entscheidungen sind als einzelne Dateien im Ordner `docs/architecture/adr/` dokumentiert:
- ADR-0001: Layered Architecture (`ADR-0001-layered-architecture.md`)
- ADR-0002: JWT-basierte Authentifizierung (`ADR-0002-jwt-authentication.md`)
- ADR-0003: Manuelles DTO Mapping (`ADR-0003-manual-dto-mapping.md`)
- ADR-0004: Datenbankstrategie H2 → Azure MySQL (Option PostgreSQL offen) (`ADR-0004-database-strategy-h2-postgres-mysql.md`)
- ADR-0005: Vertagung Recurrence Expansion (`ADR-0005-recurrence-feature-deferral.md`)

Weitere potenzielle zukünftige ADRs: Caching-Einführung, Notification-Architektur, Scheduler-Strategie.

---
## 10. Qualitätsanforderungen (Szenarien – Draft)
Die wichtigsten (priorisierten) Qualitätsszenarien sind formalisiert nach Schema: Stimulus – Quelle – Umgebung – Artefakt – Reaktion – Messkriterium.

| ID | Qualität | Stimulus (Quelle) | Umgebung | Betroffenes Artefakt | Erwartete Reaktion | Mess-/Kriterium |
|----|----------|------------------|----------|----------------------|--------------------|-----------------|
| Q1 | Performance | Benutzer fordert 50 Termine (GET /appointments?range) an | Normalbetrieb, DB mit 5k Terminen | REST API / Service / Repository | Antwort vollständig geliefert | P95 < 2000 ms |
| Q2 | Sicherheit | Request mit ungültigem/abgelaufenem JWT | Normalbetrieb | Security Layer | Zugriff verweigert ohne interne Details | 401, kein Stacktrace im Body |
| Q3 | Wartbarkeit | Hinzufügen Feld „priorityLabel“ zu AppointmentResponse | Entwicklungsphase | DTO + Mapping Service | Änderung lokal begrenzt | ≤ 4 Dateien geändert |
| Q4 | Zuverlässigkeit | DB Neustart während Lesezugriff | Einzelinstanz, kein Cache | Repository Layer | Fehler propagiert kontrolliert | 503 oder 500, keine Teilantwort |
| Q5 | Testbarkeit | Unit-Test überschneidender Termine | Build Pipeline | AppointmentService | Klar deterministisches Ergebnis | Branch Coverage Methode ≥ 90% |

Geplante Erweiterung (nach 5-Tage-Fenster): Szenario für horizontale Skalierung (Load Balancer), Logging-Korrelation, Reminder-Pünktlichkeit.

---
## 11. Risiken & Technische Schulden
Das Projekt trägt eine Reihe klar identifizierter Risiken und technischer Schulden, die in der aktuellen Ausbaustufe akzeptiert, gleichzeitig jedoch priorisiert adressiert werden sollen. Die fehlende globale Fehlerbehandlung führt zu inkonsistenten Rückgabeformaten und erhöhtem Aufwand im Frontend, weshalb ein zentraler Exception Handler mit standardisiertem Fehlerobjekt eingeführt werden muss. Uneinheitliches Logging – insbesondere verbleibende direkte Konsolenausgaben – erschwert sowohl das Filtern als auch das Korrelation von Ereignissen; die Migration zu strukturiertem SLF4J Logging mit klaren Korrelationsmerkmalen (z. B. Trace‑IDs) reduziert dieses Risiko.

Besonders sicherheitsrelevant ist das aktuell hardcodierte JWT-Secret, dessen Verbleib im Code ein Deployment in produktionsähnliche Umgebungen ohne vorgelagerte Externalisierung ausschließt; eine Rotationsstrategie und Konfiguration über Umgebungsvariablen sind zwingend. Die fehlende Rollen-basierten Autorisierung beschränkt die Zugriffskontrolle de facto auf eine binäre Unterscheidung zwischen authentifizierten und nicht authentifizierten Anfragen und verhindert spätere Funktionsdifferenzierungen (z. B. administrative Operationen). Zusätzlich existiert keine Auditierung sensibler Änderungen (Terminänderungen, fehlgeschlagene Logins), wodurch forensische Nachvollziehbarkeit eingeschränkt bleibt. Ein Scheduler zur proaktiven Auslösung von Erinnerungen fehlt – der funktionale Umfang ist aktuell auf das klientenseitige Polling reduziert, was die Zuverlässigkeit der Zustellung schwächt. Ebenso ist die Unterstützung wiederkehrender Termine nur rudimentär: Felder im Modell ohne zugehörige Logik lassen das Feature halbfertig erscheinen und bergen Integrationsrisiken für spätere Erweiterungen.

Im Testbereich erhöht das Defizit an Integrationstests im Sicherheitskontext die Gefahr verdeckter Regressionsfehler bei Änderungen an der Filterkette oder Konfiguration. Performance-seitig fehlt ein Cache-Layer; zwar genügt die gegenwärtige Lastsituation der direkten Datenbankabfrage, doch ab einer höheren Leseintensität könnten Wiederholungsanfragen Engpässe erzeugen. Zusammengenommen entstehen daraus priorisierte Maßnahmenpakete: (1) Vereinheitlichung von Fehler- und Logging-Struktur, (2) Externalisierung und Sicherung kryptographischer Secrets, (3) Einführung eines minimalen Rollen-/Autorisierungskonzeptes, (4) Ausbau wiederkehrender Terminlogik auf konsistente Semantik, und (5) Erweiterung des automatisierten Testspektrums – insbesondere für sicherheitsrelevante Endpunkte.


---
## 12. Glossar
| Begriff | Definition |
|--------|-----------|
| Termin (Appointment) | Zeitlich abgegrenztes Ereignis mit optionalen Meta-Daten |
| Reminder | Vorabbenachrichtigung X Minuten vor Start |
| Recurring | Kennzeichen für wiederkehrendes Ereignis |
| DTO | Data Transfer Object zwischen API und Domäne |
| JWT | JSON Web Token für Authentifizierung |

---
## 13. Anhänge / Offene Punkte
Dieser Abschnitt hält temporäre Platzhalter und noch nicht vollständig integrierte Fragestellungen fest. Er dient als Puffer, bevor Themen in die regulären arc42 Kapitel überführt oder durch ADRs formalisiert werden. Aktuell offen sind insbesondere die endgültige Persistenzstrategie für wiederkehrende Termine (Materialisierung versus dynamische Expansion), die Ausgestaltung eines erweiterten Rollenmodells für administrative Operationen, die Entscheidung über das künftige Hosting-Paradigma (Azure App Service oder Container-Plattform) sowie die Auswahl eines Observability-Stacks (Prometheus/Grafana gegenüber nativen Azure Monitor Mitteln). Ebenfalls ungeklärt ist der genaue Umfang eines möglichen ICS-Exportes (Einzeltermine, Serien, Einbettung von Kategorien) und ob Multi-Tenancy jenseits des Nutzerkonzeptes mittelfristig ein Ziel sein soll. Diese Punkte bleiben explizit markiert, um spätere Architekturentscheidungen zu bündeln statt schleichend in Implementierungen einzufließen.
---
## 14. Teststrategie (ausführlich)
Ziel der Teststrategie ist es, funktionale Korrektheit, Sicherheitsaspekte und qualitative Ziele (Performance, Wartbarkeit) risikobasiert abzusichern. Der Umfang ist auf einen studienprojektangemessenen Rahmen (4 Monate) skaliert und priorisiert kritischste Domänenlogik.

### 14.1 Testpyramide (Soll-Bild)
```
        End-to-End (API)   ~10–15 Szenarien (REST, Auth, Termine, Fehlerfälle)
      Integration (Slice)  ~25 Tests (Security-Filter, Repo-Queries, JSON Mapping)
    Unit (Service/Util)    ~80+ Tests (Geschäftsregeln, Validierung, Zeitlogik)
```

### 14.2 Testarten & Tools
| Ebene | Ziel | Framework / Tool | Beispiele |
|-------|------|------------------|-----------|
| Unit | Geschäftslogik isoliert | JUnit 5, Mockito | Überschneidungsprüfung, Reminder-Berechnung |
| Repository | Korrekte Query-Abbildung | Spring Boot Test + H2 | `findOverlappingAppointments` Szenarien |
| Security-Filter | JWT Pfade / Whitelists | Spring Security Test | Zugriffsschutz `/appointments` vs. `/auth/login` |
| Integration (API) | Endpunkte & Serialisierung | Spring MockMvc / RestAssured | CRUD + Fehler 401/404/409 |
| Migration/Schema (optional) | Flyway Validierung | Flyway test harness | (geplant) |
| Performance Smoke | Einfache Latenz-Checks | JMeter / k6 (leicht) | 50 GET Requests Batch |

### 14.3 Abdeckungsziele (Richtwerte)
- Line Coverage Gesamt: ≥ 70%
- Branch Coverage kritischer Service-Methoden (`createAppointment`, `updateAppointment`, `checkForOverlappingAppointments`): ≥ 85%
- Mutation Score (PIT, optional): ≥ 60% (Stretch)

### 14.4 Priorisierte Unit-Testfälle (Auszug)
| Bereich | Testfall |
|---------|----------|
| Terminzeit | Start==Ende → invalid; Start < Jetzt → invalid; Ende vor Start → invalid |
| Überschneidung | Teilweise Überlappung, voller Einschluss, angrenzend (kein Konflikt) |
| Reminder | Erinnerung genau im 60-Minuten Fenster; außerhalb – excluded |
| Security | Ungültiger Token → Kein SecurityContext |
| Ownership | Fremde Termin-ID → 404/Access Denied |

### 14.5 Integration/APIs
- Tests für: `POST /auth/register`, `POST /auth/login`, `GET /appointments`, Paginierung/Filter-Kombination, `PUT /appointments/{id}`, Fehlerfälle (409 Conflict bei Überlappung).
- Negative Tests: Unautorisierter Zugriff, veraltetes Token, falsches Format (Bean Validation 400).

### 14.6 Testdatenstrategie
- Nutzung von Builder-Hilfen (Test Utility) für `Appointment` und DTOs.
- H2 In-Memory reset pro Testklasse (Transactional + Rollback) oder Flyway baseline.
- Konstante Zeitzone `Europe/Berlin` für deterministische Zeitlogik.

### 14.7 Automatisierung & Pipeline (geplant)
1. `mvn verify` führt Unit + Integration Tests aus.
2. JaCoCo Coverage Report generieren (`target/site/jacoco`).
3. (Optional) Statischer Scan (SpotBugs / OWASP Dependency Check) – Roadmap.

### 14.8 Kurzfristige (7-Tage) Test-Maßnahmen
1. Ergänze Unit-Tests für Überschneidungslogik (alle Randfälle).
2. Integrationstest: Unautorisierter Zugriff `/api/v1/appointments` → 401.
3. Integrationstest: Gültiges Login + Termin anlegen + Abruf.
4. Test für Whitelist `/api/v1/auth/validate` (ohne Token erreichbar?).
5. Repository-Test: `findUserAppointmentsInDateRange` (Leer / Nicht-Leer Szenario).

### 14.9 Risiken im Testbereich
- Fehlende Tests für Security-Kontext können Regressionen verschleiern.
- Kein globaler Exception Handler → uneinheitliche Fehlerfälle schwer testbar.
- Keine Lasttests → Performance-Annahmen unvalidiert.

### 14.10 Tool-/Lib-Erweiterungen (Vorschlag)
`pom.xml` Ergänzungen: `spring-boot-starter-test` (vorhanden), `mockito-inline`, optional `rest-assured`, `jacoco-maven-plugin`.

### 14.11 Out-of-Scope (Phase 1)
- Mutation Testing verpflichtend
- Vollständige Performance-/Lastsuite
- Chaos / Resilience Tests

---
## 17. Roadmap & Evolutionsplan
Der Evolutionsplan unterscheidet kurzfristige (7 Tage) Maßnahmen zur Stabilisierung und mittel-/langfristige (bis 4 Monate Gesamtprojektlaufzeit) Erweiterungen. Jede Maßnahme ist mit Ziel, grobem Aufwand (T-Shirt Größe) und primärem Qualitätsbeitrag versehen.

### 17.1 Kurzfristig (≤ 7 Tage)
| Maßnahme | Ziel | Aufwand | Qualitätsschwerpunkt |
|----------|------|--------|----------------------|
| Externalisierung JWT Secret | Sicherheit erhöhen | S | Sicherheit |
| Globaler Exception Handler (`@ControllerAdvice`) | Einheitliche Fehlerstruktur | S | Wartbarkeit/Testbarkeit |
| SLF4J Logging + Entfernen `System.out` | Beobachtbarkeit verbessern | S | Wartbarkeit |
| Unit Tests Überschneidungslogik | Fehlerprävention | S | Testbarkeit |
| Integrationstest Auth + CRUD Flow | Regressionsschutz | S | Zuverlässigkeit |
| H2 Console Abschalten im Prod Profil | Angriffsfläche reduzieren | XS | Sicherheit |

### 17.2 Nächste Iterationen (8–30 Tage)
| Feature / Thema | Ziel | Aufwand | Bemerkung |
|-----------------|------|--------|-----------|
| Rollen-/Autorisierung (Basis) | Differenzierte Zugriffe | M | `ROLE_USER` / `ROLE_ADMIN` |
| Recurrence Logik implementieren | Serien verwalten | M | Expansion + Exceptions |
| Reminder Scheduler (Spring Scheduling) | Proaktive Benachrichtigung | M | Polling reduzieren |
| Export (ICS Einzeltermin) | Interoperabilität | S | Start mit Download Endpoint |
| Actuator Integration | Health/Info Metrics | S | Basis Observability |
| Secret Handling (ENV + Rotation Konzept Dokument) | Sicherheit | S | Ergänzt ADR |

### 17.3 Mittelfristig (31–60 Tage)
| Thema | Ziel | Aufwand | Hinweis |
|-------|------|--------|--------|
| ICS Serienexport | Komplettierung Export | M | Abhängigkeit Recurrence |
| Caching (Optional) | Performance Hot Reads | M | Nur nach Messung |
| Audit Logging (Änderungen) | Nachvollziehbarkeit | M | Neue Tabelle `audit_log` |
| Notification Adapter (E-Mail Stub) | Erinnerungskanal | M | Ports & Adapter einführen |
| Rate Limiting (Filter / Bucket4j) | Abwehr Brute Force | S | Security Fokus |

### 17.4 Langfristig (61–120 Tage; restliche Projektlaufzeit)
| Thema | Ziel | Aufwand | Hinweis |
|-------|------|--------|--------|
| Multi-Calendar / Kategorien modulieren | Produktwert erhöhen | L | Mögliche Aggregationstabelle |
| Team-Sharing (Invite Tokens) | Kollaboration | L | Neue Entitäten (Share, Permission) |
| Volltextsuche (Elasticsearch optional) | Suchkomfort | L | erst bei Bedarf |
| Erweiterter Observability Stack | Betriebsreife | M | Metrics + Tracing |
| Mobile UI / SPA Migration | UX Verbesserung | L | separater Client |

### 17.5 Architektur-Prinzipien für Evolution
1. Zuerst Messen (Observability) vor Optimieren.
2. Sicherheitsaspekte früh (Shift-Left) – Secrets, AuthZ, Input.
3. Feature Flags für riskante Erweiterungen (Recurrence, Export Serien).
4. Konsistente DTO-Versionierung (Backward Compatibility gewährleisten).
5. Dokumentation via ADR bei jeder strukturellen Änderung > M Aufwand.

### 17.6 Risiken Evolutionsplan
- Feature-Streuung ohne priorisierte Metriken → Gegenmaßnahmen: Quartalsziele definieren.
- Technische Schuld (Secret, Logging) könnte verschleppt werden → Maßnahmenblock 7-Tage strikt einplanen.
- Recurrence Komplexität (Zeitzonen, Exception-Instanzen) → Scope klein halten (keine komplexen Regeln initial).

---
## 18. Zukünftige Komponenten (Zielbild Skizze)
| Komponente | Beschreibung | Schnittstelle | Architektur-Notiz |
|------------|--------------|--------------|-------------------|
| Notification Service | Versand Erinnerungen (E-Mail / später Push) | REST / Event | Als separater Adapter, optional extern skalieren |
| Export Module | ICS Generierung | REST Endpoint `/export/ics` | Reiner Outbound Use-Case |
| Scheduler | Zeitgesteuerte Jobs (Reminder Scan) | Spring Scheduling | Entkoppelt von Request-Fluss |
| Audit Logging | Persistenz Nutzeraktionen | DB Tabelle `audit_log` | Asynchron (Event Listener) |
| Observability Stack | Metrics, Traces, Logs | Actuator, OpenTelemetry | Instrumentierung früh beginnen |
| Authorization Layer | Rollen & Policies | Spring Security Method Security | Schrittweise Aktivierung |

---
## 22. Änderungen in dieser Version
| Bereich | Änderung |
|--------|----------|
| Versionierung | Update auf 1.0 (Vollständigkeit) |
| Quality Tree | Kapitel 1.3 hinzugefügt |
| Bausteine | Level-2 Bausteine + Sequenz (5.10/5.11) |
| Crosscutting | Strukturierte Unterkapitel 8.x |
| Erweiterungen | Future Components + Roadmap konsolidiert |
| Risikoanalyse | Priorisierte Risiken erweitert |
| Konsistenz | Kapitelnummern bereinigt, Änderungslog verschoben |

---
## 15. Security Threat Model (Kurzfassung)
Ziel: Fokus auf die wesentlichen schützenswerten Assets und kurzfristig adressierbare Risiken im 5-Tage-Fenster. Methode: STRIDE-light.

### 15.1 Schutzgüter (Assets)
| Asset | Beschreibung | Sensitivität |
|-------|--------------|--------------|
| Termindaten | Titel, Zeiten, optional Beschreibung | Mittel (personenbezogen) |
| Benutzerdaten | E-Mail, Name, Hash | Hoch |
| JWT Token | Authentifizierungsnachweis | Hoch |
| Passwort-Hashes | BCrypt Hashes | Kritisch |
| Konfiguration (Secrets) | JWT Secret (derzeit im Code generiert) | Kritisch |

### 15.2 Angriffsflächen
| Oberfläche | Vektor | Aktueller Zustand | Geplante Maßnahme |
|------------|--------|------------------|-------------------|
| Auth Endpoints `/api/v1/auth/*` | Brute Force | Login Attempts Zähler, kein Rate Limit | Rate Limit (nach Zeitfenster) |
| JWT Handling | Token Theft / Replay | Keine Rotation, Secret In-Memory | Secret Externalisierung ENV |
| Appointment API | Unauth. Zugriff | SecurityConfig schützt, Whitelist OK | Tests ausbauen |
| Debug / H2 Console | Information Disclosure | H2 Console offen (Dev) | Prod deaktivieren |
| Input Felder (Beschreibung) | Stored XSS Risiko (bei späterem UI Render) | Sanitisierung ungeklärt | Encoding im UI, Validierung Regeln |

### 15.3 Bedrohungen (Auswahl nach STRIDE)
| Kategorie | Beispiel | Relevanz | Kurz-Maßnahme |
|-----------|----------|----------|---------------|
| Spoofing | Gestohlener JWT wird wiederverwendet | Hoch | Kürzere Expiry + Secret Rotation Plan |
| Tampering | Manipulation von Requests (Termin anderer User) | Mittel | Ownership-Prüfung (bereits in Service) Tests hinzufügen |
| Repudiation | Aktionen nicht nachvollziehbar | Mittel | Audit Logging (Postponed) |
| Information Disclosure | Zugriff auf fremde Termine | Hoch | Systematische Ownership-Tests |
| DoS (leicht) | Viele Requests gegen /auth/login | Mittel | Rate Limit (TODO) |
| Elevation of Privilege | User nutzt Admin-Funktion | Niedrig (keine Admin Features) | Rollen-Prüfung vorbereiten |

### 15.4 Kurzfristige Maßnahmen (innerhalb 5 Tage realistisch)
1. JWT Secret über ENV Variable (`JWT_SECRET`) konfigurierbar machen.
2. Security Test: Unauth Zugriff auf protected Endpoint → 401 sicherstellen.
3. Whitelist Überprüfung für statische Ressourcen (Regressionstest hinzufügen).
4. H2 Console in PROD Profil deaktivieren.
5. Dokumentation: Empfohlene Token Lifetime & Rotation Zyklus (auch wenn Rotation noch nicht implementiert).

### 15.5 Out-of-Scope (markiert für Roadmap)
- Audit Trail / Änderungslog.
- Rate Limit Implementierung.
- Rollenbasierte Autorisierung (Admin / Moderator).
- Content Security Policy / UI-spezifische XSS Abwehr.

### 15.6 Bewertung Rest-Risiko
Nach Umsetzung kurzfristiger Maßnahmen verbleibt primär Risiko durch fehlendes Rate Limiting und fehlende Auditierbarkeit; akzeptiert für Lern-/Projektkontext, markiert für Phase 2.

---
## 16. Deployment & Operations (Kompakt / Platzhalter)
Dieser Abschnitt ist bewusst allgemein gehalten. Konkrete Cloud-/Infra-Details (Azure MySQL Provisionierung, CI/CD Pipeline) werden später ausgearbeitet. Ziel: Rahmen definieren und Integrationspunkte im Code/Dokument markieren.

### 16.1 Umgebungen (geplant)
| Umgebung | Zweck | DB Ziel | Besonderheiten | Status |
|----------|------|--------|----------------|--------|
| DEV | Lokale Entwicklung | H2 (File/In-Memory) | Schneller Start, Reset möglich | aktiv |
| TEST (optional) | Integrationstests (später) | H2 oder Test-MySQL | Automatisierbar via Pipeline | geplant |
| STAGE (optional) | Vorab-Deployment | Azure MySQL (Shared) | Smoke Tests | offen |
| PROD (Hosting) | Veröffentlichung | Azure Database for MySQL | Backups, Monitoring | geplant |

### 16.2 Build & Deployment (High-Level)
| Schritt | Beschreibung | Tool / Platzhalter |
|--------|--------------|--------------------|
| Build | `mvn clean package` erstellt Fat Jar | Maven |
| Container (optional) | Docker Image (Basis: eclipse-temurin:17-jre) | Dockerfile (vorhanden) |
| Konfiguration | Profile `dev`, `prod` mit separaten Properties | `application.yml`, `application-prod.properties` |
| Secrets | ENV Variablen (`JWT_SECRET`, `DB_PASSWORD`) | .env / Azure App Settings (später) |
| Deployment PROD | Jar oder Container auf Azure (App Service / Container Apps) | Platzhalter |

### 16.3 Geplante Ressourcen (Azure – Platzhalter)
| Komponente | Kandidat | Notiz |
|------------|----------|-------|
| Runtime | Azure App Service (Java) ODER Container App | Entscheidung offen |
| Datenbank | Azure Database for MySQL Flexible Server | Größe/Billing noch offen |
| Monitoring | Azure Monitor / Logs (später) | Noch nicht konfiguriert |
| Backup | MySQL automatisches Backup | Standard-Retention prüfen |

### 16.4 Konfigurationsparameter (Beispiele)
| Key | Beschreibung | DEV Wert (Beispiel) | PROD (Platzhalter) |
|-----|-------------|---------------------|-------------------|
| `SPRING_PROFILES_ACTIVE` | Aktiviertes Profil | `dev` | `prod` |
| `JWT_SECRET` | Signatur-Secret | dev-temp | ${SECRET} |
| `DB_URL` | JDBC Verbindung | `jdbc:h2:file:./data/terminkalender` | `jdbc:mysql://<host>:3306/<db>` |
| `DB_USER` | DB Benutzer | `sa` | `${DB_USER}` |
| `DB_PASSWORD` | Passwort | leer | Secret Store |

### 16.5 Betrieb (Runbook – Platzhalter)
| Aktion | Kurzschritte | Status |
|--------|--------------|--------|
| Start DEV | `mvn spring-boot:run` | aktiv |
| Health Check | (Nach Actuator) GET `/actuator/health` | geplant |
| Log Zugriff | STDOUT → Konsole | aktiv |
| Backup Überprüfung | MySQL Backup-Log prüfen | geplant |
| Secret Rotation | Neuer `JWT_SECRET` setzen, Rolling Restart | offen |

### 16.6 Skalierung (Vorläufig)
- Horizontal: Mehrere Instanzen hinter Load Balancer (später) – Token stateless → kompatibel.
- Vertikal: Größeres App Service Plan / Container Ressourcen.
- Datenbank: Skalierung durch MySQL Tier (vCores, Storage). Monitoring noch nicht spezifiziert.

### 16.7 Offene Punkte (Deployment)
- Auswahl Hosting-Modell (App Service vs. Container) noch nicht getroffen.
- Kein automatischer CI/CD Prozess definiert (GitHub Actions Platzhalter möglich).
- Actuator/Health Endpoints noch nicht integriert.
- Keine Migrationsskripte (Flyway) → Blocker vor PROD.

> Hinweis: Dieser Abschnitt ist bewusst schlank und dient als Ankerpunkte-Liste. Ausarbeitung sobald Infrastruktur priorisiert wird.

---
## 17. Performance und Kapazitätsannahmen
Die Performancebetrachtung fußt auf bewussten, explizit dokumentierten Annahmen, um spätere Abweichungen messbar und steuerbar zu machen. Kurzfristig wird mit bis zu fünfzig gleichzeitig aktiven Benutzern gerechnet, während insgesamt einige hundert registrierte Konten erwartet werden. Schreiboperationen (Erstellen und Aktualisieren von Terminen) treten deutlich seltener auf als Lesevorgänge; der typische Zugriff besteht aus dem Abruf gefilterter Terminlisten und Kalenderansichten. Daraus leitet sich eine Lese-dominierte Lastcharakteristik mit einem Verhältnis von ungefähr siebzig Prozent read zu dreißig Prozent write ab. Die initiale Datenmenge bleibt mit einigen tausend Terminen überschaubar, sodass einfache Indexierung auf Start- und Endzeit sowie Benutzerkennung eine ausreichende Anfrage-Performance sicherstellen kann.

Zentrale potenzielle Engpässe liegen in der Überschneidungsprüfung und in zeitfensterbezogenen Erinnerungsabfragen. Beide Bereiche kombinieren Datumsfilter mit Benutzerkontext und profitieren deshalb von zusammengesetzten Indizes. Ein Problemfeld könnte entstehen, falls Abfragen große Zeiträume ohne Pagination erfassen; dem wird durch eine verbindliche Paginierungsvorgabe entgegengewirkt. Caching wird bewusst aufgeschoben, um Premature Optimization zu vermeiden. Stattdessen sollen gezielte Messpunkte – etwa einfache Zeitmessungen der Überschneidungslogik – frühes Feedback liefern, sobald Latenzen jenseits definierter Schwellen (beispielsweise hundert Millisekunden für den reinen Overlap-Check) überschritten werden. Die horizontale Skalierbarkeit wird durch die stateless Sicherheitsarchitektur vorbereitet, auch wenn eine Mehrinstanzkonfiguration kurzfristig nicht erforderlich ist. Auslastungsspitzen in Schreiboperationen (Importe oder Massenänderungen) sind im aktuellen Funktionsumfang nicht vorgesehen und werden als spätere Erweiterung betrachtet. Der vorläufige Zielkorridor für Antwortzeiten liegt bei unter zwei Sekunden für fünfundneunzig Prozent der Anfragen unter Normalbedingungen. Abweichungen hiervon lösen zunächst Analyse und Metrikverfeinerung aus, bevor strukturelle Optimierungen (Caching, Datenbank-Tuning oder Query-Refactoring) implementiert werden.

Langfristige Maßnahmen (Read Replicas, differenzierte Materialisierung wiederkehrender Termine, stärker granulare Indizes) werden erst evaluiert, wenn gemessene, nicht nur vermutete Engpässe vorliegen. Diese Zurückhaltung reduziert Komplexität und hält die Architektur bewusst schlank.

## 18. Recurrence und Reminder Design
Das Datenmodell enthält bereits Felder zur Beschreibung wiederkehrender Ereignisse, ohne dass deren funktionale Expansion in konkrete Terminausprägungen erfolgt. Diese bewusste Vertagung verhindert, dass eine früh getroffene, möglicherweise unpassende Implementierung langfristig hohen Migrationsaufwand erzeugt. Drei prinzipielle Umsetzungsansätze wurden abgewogen: eine vollständige Vorwegausmaterialisierung aller Instanzen einer Serie, eine rein dynamische Berechnung beim Zugriff sowie ein hybrides Roll-Vorwärts-Modell, das nur einen definierten Zeitraum im Voraus persistiert. Die vollständige Materialisierung scheitert früh am Speicherwachstum bei langlaufenden Serien, während eine rein dynamische Berechnung komplexer Filterlogik und Abgrenzungsfälle höhere Implementierungs- und Testkosten verursacht. Das hybride Modell bietet langfristig eine balancierte Option, wird jedoch erst nach einer ersten Nutzungsphase weiterverfolgt.

Kurzfristig bleibt Wiederholung in der Schnittstelle passiv: Serienattribute werden zwar akzeptiert oder gespeichert, entfalten jedoch keine funktionale Wirkung; sie dienen damit als stabile Evolutionsfläche. Das Reminder-Konzept stützt sich derweil auf ein zeitfensterorientiertes Pull-Prinzip. Clients fragen Termine mit anstehender Erinnerung in einem relativ kurzen Zeithorizont ab. Diese Gestaltungsentscheidung vermeidet die vorzeitige Einführung eines Server-Schedulers oder externer Event-Infrastruktur und reduziert Fehlerquellen in der frühen Projektphase. Eine spätere Migration auf servergesteuerte Benachrichtigungen (durch periodische Aufgaben oder Ereigniswarteschlangen) bleibt durch die saubere Kapselung der entsprechenden Service-Methoden möglich. Risiken der Aufschubstrategie – insbesondere die Gefahr inkonsistenter Semantik bei späterer Aktivierung – werden durch eine klare Dokumentation des aktuellen Nicht-Verhaltens minimiert.

## 19. Roadmap und Nicht-Ziele
Der unmittelbare Zeithorizont fokussiert auf Maßnahmen, die Betriebssicherheit und Nachvollziehbarkeit verbessern, ohne den Funktionsumfang wesentlich auszuweiten. Dazu zählen die Einführung grundlegender Observability (Aktivierung von Health und Metriken), die Vereinheitlichung des Loggings, die Durchsetzung einer konsistenten Paginierung sowie die Absicherung sicherheitsrelevanter Basispfade über automatisierte Tests. Anschließend folgt eine Phase der gezielten Qualitätsanreicherung: Integration von Migrationsskripten für die Produktionsdatenbank, Vorbereitung auf die Externalisierung sensibler Secrets und Ausarbeitung der persistenten Strategie für wiederkehrende Termine. Mittelfristig rückt eine verbesserte Erinnerungsmechanik in den Fokus, die serverseitige Auslöser oder Push-Benachrichtigungen integrieren könnte. Weiterhin geplant sind strukturierte Logs, verfeinerte Rollen- und Berechtigungsmodelle, optional eine Event-getriebene Erweiterung für Benachrichtigungen sowie die schrittweise Einführung eines standardisierten Observability-Stacks mit Metriken, Tracing und Visualisierung.

Gleichzeitig sind bestimmte potenziell umfangreiche Erweiterungen explizit keine kurzfristigen Ziele. Dazu gehören eine Mandantenfähigkeit über das bestehende Benutzerkonzept hinaus, eine stark granularisierte Freigabe- oder Teilen-Funktionalität für Termine, native mobile Applikationen, komplexe Synchronisation mit Drittplattformen sowie automatisierte Skalierungsstrategien. Diese bewusste Ausklammerung verhindert eine Aufweichung des Projektfokus und schützt das Zeitbudget vor Feature-Diffusion.

## 20. Priorisierte Risiken (kontextualisiert)
Eine priorisierte Betrachtung der Hauptrisiken kombiniert deren potenzielle Auswirkung mit einer groben Eintrittswahrscheinlichkeit, um eine abgestützte Reihenfolge für Minderungsmaßnahmen abzuleiten. Die fehlende Beobachtbarkeit stellt das dominante Risiko dar, weil sie jeden weiteren Verbesserungsprozess verzögert: Ohne Metriken oder strukturierte Logs bleiben Engpässe und Fehlerursachen spekulativ. Unmittelbar danach rangieren die Performanceimplikationen der Terminüberschneidungsprüfung; solange deren Laufzeitverhalten nicht empirisch erfasst wird, kann eine steigende Nutzungsintensität unbemerkt zu Antwortzeitspitzen führen. Gleichwertig gewichtet ist das strukturelle Risiko der späteren Nachrüstung von Wiederholungslogik, die das bestehende Modell anfällig für semantische Brüche macht, falls früh Entscheidungen getroffen werden, die Annahmen über Datenmenge oder Filtermuster verletzen.

Sicherheitsbezogene Basisthemen – insbesondere die Externalisierung kryptographischer Geheimnisse und eine Mindestabsicherung gegen automatisierte Login-Versuche – erhalten eine erhöhte, aber etwas nachgelagerte Priorität, da sie bei einem internen oder testnahen Betrieb geringere unmittelbare Expositionsflächen besitzen. Operative Risiken wie ungehinderter Datenbankwachstum ohne Archivierungsstrategie oder fehlende Auditierbarkeit bleiben beobachtete Hintergrundrisiken, deren Dringlichkeit mit steigender Funktionstiefe zunimmt. Die Kategorisierung ermöglicht eine sequentielle Abarbeitung: Zuerst Transparenz herstellen (Monitoring, Logging), dann strukturelle Korrektheit absichern (Overlap und Recurrence Pfade), anschließend sicherheitsbezogene Härtung und schließlich betriebliche Resilienz (Backups, Migration, Skalierung).

## 21. Längen- und Qualitätsprüfung
Mit der vorliegenden Konsolidierung wurde der dokumentarische Umfang bewusst auf zusammenhängende Prosa ausgerichtet, um Redundanz und Kontextbrüche zu verringern. Tabellen verbleiben nur dort, wo sie die Vergleichbarkeit strukturierter Kriterien erhöhen (Qualitätsszenarien, vereinzelte Konfigurationsübersichten) und nicht durch narrativen Text denselben Mehrwert erzielen würden. Der Gesamtkorpus bewegt sich voraussichtlich im anvisierten Spektrum einer mittleren zweistelligen Seitenzahl bei Standard-Layoutierung (DIN A4, elf bis zwölf Punkt Grundschrift), wobei bewusst knappe Formulierungen in den frühen Kapiteln die Lesbarkeit langer späterer Abschnitte kompensieren. Qualitätssicherung stützt sich zunächst auf manuelle Prüfung stilistischer und semantischer Kohärenz und kann nach Aktivierung automatischer Werkzeuge (Link-Prüfungen, statische Analyse der Quellverweise) weiter formalisiert werden. Erfolgreiche Weiterentwicklung des Dokuments misst sich daran, dass neue Entscheidungen primär als eigenständige ADRs landen und nicht diffuse Anpassungen verteilter Abschnitte erzwingen. Damit bleibt Evolution nachvollziehbar und die Dokumentation verringerte langfristig das Risiko konzeptioneller Erosion.

---
Ziel: Ausreichende Absicherung der Kern-Geschäftslogik (Terminvalidierung, Überschneidungen, Auth), ohne Overengineering. Fokus: Stabilität und Nachweis für Qualitätsziele binnen 5 Tagen.

### 14.1 Testpyramide (geplant)
| Ebene | Ziel | Beispiele | Tooling |
|-------|------|-----------|---------|
| Unit | Geschäftsregeln & Helfer isoliert | `AppointmentService.validateAppointmentTime`, `checkForOverlappingAppointments` | JUnit 5, Mockito |
| Integration (Repository) | JPA Queries korrekt | `AppointmentRepository.findOverlappingAppointments` | Spring Test + H2 |
| API (Slice) | HTTP Verhalten, Statuscodes | POST/PUT/GET /appointments | MockMvc |
| Security (gezielt) | Zugriffsschutz | Zugriff ohne Token → 401 | MockMvc + Test JWT |
| (Optional) End-zu-Ende | Smoke gegen laufende Instanz | GET /health (nach Actuator) | manuell / Script |

### 14.2 Priorisierte Testfälle (Must-Have)
1. Termin-Erstellung: Ablehnen wenn Start > Ende.
2. Termin-Erstellung: Ablehnen wenn Start in Vergangenheit.
3. Überschneidung: Zwei Termine im selben Zeitraum → Fehler.
4. Auth: Aufruf /api/v1/appointments ohne Token → 401.
5. Repository: `findOverlappingAppointments` liefert erwartete Anzahl.
6. Erinnerung: `getUpcomingReminders` filtert korrekt (Positiv & Negativfall).

### 14.3 Abdeckungsziel (realistisch)
- Core Geschäftslogik (`AppointmentService`): >80% Branch Coverage.
- Auth (`AuthService` Token-Generierung + ungültiger Login): Basisfälle.
- Repositories: Kritische Queries min. 1 Happy + 1 Edge.

### 14.4 Definition of Done (Test-bezogen)
| Kriterium | Beschreibung |
|-----------|--------------|
| Build Grün | `mvn test` ohne Fehler |
| Kern-Use-Cases getestet | Termin CRUD + Auth + Überschneidung |
| Kein ungetesteter kritischer Zweig | Entscheidungszweige in Validierung abgedeckt |
| Wiederholbarkeit | Tests unabhängig vom Ausführungszeitpunkt |

### 14.5 Nicht im Scope (bewusst ausgelassen für diese Iteration)
- Last-/Performance Tests
- Vollständige Security Penetration
- UI E2E Tests
- Migrationstests (erst nach Flyway-Einführung)

### 14.6 Technische Hilfsmittel (geplant)
- Test-Daten-Fabrik Methoden in Testklassen (anstelle komplexer Builder)
- H2 In-Memory für Repository Tests
- Später: Testcontainers (PostgreSQL) – außerhalb 5-Tage-Zeitraum

### 14.7 Risiken Teststrategie
| Risiko | Auswirkung | Mitigation |
|--------|------------|------------|
| Zeitlimit → geringe Tiefe | Eckenfälle unentdeckt | Fokus auf Kernregeln + spätere Erweiterung |
| Fehlende Actuator Endpoints | Weniger technische Health-Checks | Nachpriorisieren nach Kern-Tests |
| Kein Scheduler Test | Erinnerung nur Pull getestet | Scheduler erst nach Implementierung |

---
- Offene Frage: Rollenmodell (USER/ADMIN/MODERATOR?) – Zugriff auf Admin-Endpunkte definieren.
- Offene Frage: ICS-Export Format / Interface – REST Endpoint oder Datei-Download?
- Offene Frage: Persistenzstrategie für Recurrence (Materialization vs. On-the-fly Berechnung).
- Offene Frage: Monitoring (Spring Actuator aktivieren, Metriken definieren: Request Latenz, Fehlerquote, DB-Latenz).
- Offene Frage: Multi-Tenancy (Ist Mandantenfähigkeit ein zukünftiges Ziel?).

> Bitte Feedback zu: Priorisierung Risiken, Ergänzung weiterer Laufzeitszenarien (z. B. Login Fehlversuch). Nach Einarbeitung: Anhebung auf Version 0.3.
