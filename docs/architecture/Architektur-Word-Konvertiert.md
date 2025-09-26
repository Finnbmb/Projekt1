![](docs/images/media/image1.png){width="6.102083333333334in"
height="0.7430555555555556in"}

**Projekt I**

**SoSe 25 Gruppe 12**

**Softwarearchitektur**

**Terminkalender-System mit Admin-Suite**

Alexander Madel 5213591

Blendi Feka 5379019

Finn Beimborn 5448674

Ouail El Ghouti 5503014

Mhamed Hadiki 5518616

Amin Amjahid 5460814

# Inhaltsverzeichnis {#inhaltsverzeichnis .TOC-Heading}

[Abbildungsverzeichnis
[V](#abbildungsverzeichnis)](#abbildungsverzeichnis)

[Tabellenverzeichnis [VI](#tabellenverzeichnis)](#tabellenverzeichnis)

[1. Einleitung [1](#einleitung)](#einleitung)

[1.1 Qualitätsziele [1](#qualitätsziele)](#qualitätsziele)

[1.2 Stakeholder [2](#stakeholder)](#stakeholder)

[2. Randbedingungen [2](#randbedingungen)](#randbedingungen)

[2.1 Technische Randbedingungen
[2](#technische-randbedingungen)](#technische-randbedingungen)

[2.2 Organisatorische Randbedingungen
[3](#organisatorische-randbedingungen)](#organisatorische-randbedingungen)

[3. Außensicht [4](#außensicht)](#außensicht)

[3.1 Batch-Benutzerschnittstellen
[4](#batch-benutzerschnittstellen)](#batch-benutzerschnittstellen)

[3.2 Nachbarsysteme [4](#nachbarsysteme)](#nachbarsysteme)

[4. Innensicht [5](#innensicht)](#innensicht)

[4.1 Komponentensicht [6](#komponentensicht)](#komponentensicht)

[4.1.1 Kontextabgrenzung [6](#kontextabgrenzung)](#kontextabgrenzung)

[4.1.2 Bausteinsicht [8](#bausteinsicht)](#bausteinsicht)

[4.2 Betreibersicht [10](#betreibersicht)](#betreibersicht)

[4.2.1 Übersicht der wichtigsten Betriebsaufgaben
[10](#übersicht-der-wichtigsten-betriebsaufgaben)](#übersicht-der-wichtigsten-betriebsaufgaben)

[4.2.2 Systemvoraussetzungen und Installation
[11](#systemvoraussetzungen-und-installation)](#systemvoraussetzungen-und-installation)

[4.2.3 Betrieb und Systemstart
[11](#betrieb-und-systemstart)](#betrieb-und-systemstart)

[4.2.4 Mehrfachinstallation und Koexistenz
[11](#mehrfachinstallation-und-koexistenz)](#mehrfachinstallation-und-koexistenz)

[4.2.5 Betriebsressource [12](#betriebsressource)](#betriebsressource)

[4.2.6 Backup, Recovery und Notfallmaßnahmen
[12](#backup-recovery-und-notfallmaßnahmen)](#backup-recovery-und-notfallmaßnahmen)

[4.2.7 Monitoring und Updates
[12](#monitoring-und-updates)](#monitoring-und-updates)

[4.3 Erstellungssicht [12](#erstellungssicht)](#erstellungssicht)

[4.3.1 Programmquellen [13](#programmquellen)](#programmquellen)

[4.3.2 Statische und dynamische Bibliotheken
[13](#statische-und-dynamische-bibliotheken)](#statische-und-dynamische-bibliotheken)

[4.4 Physische Sicht [13](#physische-sicht)](#physische-sicht)

[4.4.1 Verteilung auf Zielsysteme
[13](#verteilung-auf-zielsysteme)](#verteilung-auf-zielsysteme)

[4.4.2 Genutzte physische Geräte
[14](#genutzte-physische-geräte)](#genutzte-physische-geräte)

[4.4.3 Ausführbare Programme
[14](#ausführbare-programme)](#ausführbare-programme)

[4.5 Laufzeitsicht [14](#laufzeitsicht)](#laufzeitsicht)

[4.5.1 Beispielhafter Ablauf: Termin-Erstellung
[15](#beispielhafter-ablauf-termin-erstellung)](#beispielhafter-ablauf-termin-erstellung)

[5. Übergreifende Aspekte
[17](#übergreifende-aspekte)](#übergreifende-aspekte)

[5.1 Qualitätsmerkmale [17](#qualitätsmerkmale)](#qualitätsmerkmale)

[5.1.1 Benutzerfreundlichkeit und Wartbarkeit
[17](#benutzerfreundlichkeit-und-wartbarkeit)](#benutzerfreundlichkeit-und-wartbarkeit)

[5.1.2 Zuverlässigkeit [17](#zuverlässigkeit)](#zuverlässigkeit)

[5.1.3 Leistungsfähigkeit und Effizienz
[17](#leistungsfähigkeit-und-effizienz)](#leistungsfähigkeit-und-effizienz)

[5.1.4 Sicherheit [18](#sicherheit)](#sicherheit)

[6. Architekturentscheidungen
[18](#architekturentscheidungen)](#architekturentscheidungen)

[6.1 Einleitung [18](#einleitung-1)](#einleitung-1)

[6.2 Authentifizierung und Sicherheit
[18](#authentifizierung-und-sicherheit)](#authentifizierung-und-sicherheit)

[6.2.1 Umsetzungsbeispiele (A&S)
[19](#umsetzungsbeispiele-as)](#umsetzungsbeispiele-as)

[6.3 Datenbanktechnologie
[20](#datenbanktechnologie)](#datenbanktechnologie)

[6.3.1 Umsetzungsbeispiele (DB-Technologie)
[21](#umsetzungsbeispiele-db-technologie)](#umsetzungsbeispiele-db-technologie)

[7. Querschnittskonzepte
[22](#querschnittskonzepte)](#querschnittskonzepte)

[7.1 Sicherheit (JWT mit Spring Security)
[22](#sicherheit-jwt-mit-spring-security)](#sicherheit-jwt-mit-spring-security)

[7.1.1 Zweck [22](#zweck)](#zweck)

[7.1.2 Kernkonzept [22](#kernkonzept)](#kernkonzept)

[7.1.3 Beispielcode [23](#beispielcode)](#beispielcode)

[7.1.4 Best Practices [24](#_Toc209788227)](#_Toc209788227)

[7.2 Fehler- und Eingabebehandlung
[25](#fehler--und-eingabebehandlung)](#fehler--und-eingabebehandlung)

[7.2.1 Zweck [25](#zweck-1)](#zweck-1)

[7.2.2 Kernkonzept [25](#kernkonzept-1)](#kernkonzept-1)

[7.2.3 Beispielcode [25](#beispielcode-1)](#beispielcode-1)

[7.2.4 Best Practices [27](#best-practices)](#best-practices)

# Abbildungsverzeichnis

[Abbildung 1 Fachlicher Kontext [6](#_Toc209788233)](#_Toc209788233)

[Abbildung 2 Technischer Kontext [7](#_Toc209788234)](#_Toc209788234)

[Abbildung 3 Bausteinsicht [8](#_Toc209788235)](#_Toc209788235)

[Abbildung 4 Ablauf Terminerstellung
[15](#_Toc209788236)](#_Toc209788236)

# Tabellenverzeichnis

[Tabelle 1 Qualitätsziele [2](#_Toc209788237)](#_Toc209788237)

[Tabelle 2 Stakeholder [2](#_Toc209788238)](#_Toc209788238)

[Tabelle 3 Technische Randbedingungen
[3](#_Toc209788239)](#_Toc209788239)

[Tabelle 4 Betriebsaufgaben [11](#_Toc209788240)](#_Toc209788240)

# Einleitung

Dieses Architekturdokument beschreibt die grundlegende Struktur und die
wichtigsten Entwurfsentscheidungen der Terminkalender-Anwendung. Ziel
ist es, allen Projektbeteiligten insbesondere Entwicklern, Betreibern
und Entscheidern einen umfassenden Überblick über die Architektur, die
Komponenten und deren Zusammenspiel zu geben.

Das Dokument richtet sich an Softwareentwickler, Systemarchitekten,
Administratoren und alle, die an der Weiterentwicklung, dem Betrieb oder
der Wartung des Systems beteiligt sind. Es erläutert die fachlichen und
technischen Grundlagen, die den Aufbau und das Verhalten der Anwendung
bestimmen.

Der Aufbau des Dokuments folgt bewährten Architekturstandards und
gliedert sich in verschiedene Sichten: Nach einer Einleitung werden kurz
die Außensicht, die Komponentensicht, die Betreibersicht, die
Erstellungssicht, die physische Sicht und die Laufzeitsicht ausführlich
behandelt. Tabellen und Diagramme unterstützen das Verständnis der
Zusammenhänge. Die verwendeten Notationen orientieren sich an
etablierten Standards wie UML und den Empfehlungen für
Softwarearchitekturdokumentation.

Die Terminkalender-Anwendung ist ein webbasiertes System zur Verwaltung
von Terminen, Benutzern und Kalenderansichten. Sie ist modular
aufgebaut, skalierbar und für den Einsatz in unterschiedlichen
Umgebungen konzipiert. Die Architektur legt besonderen Wert auf
Wartbarkeit, Erweiterbarkeit und Sicherheit.

## Qualitätsziele

  -------------------------------------------------------------------------------------
   **Priorität**   **Qualitätsziel**     **Beschreibung**     **Metriken/Indikatoren**
  --------------- ------------------- ---------------------- --------------------------
         1            Sicherheit      Schutz von Termindaten JWT-Integrität, OWASP Top
                                        und Benutzerkonten           10 Checks

         2            Wartbarkeit      Saubere Schichtung &      SonarQube Issues,
                                         geringe Kopplung    Zyklomatische Komplexität

         3          Erweiterbarkeit   Neue Termin-Funktionen  LOC pro Feature, Anzahl
                                      ohne Architekturbruch     modifizierter Module

         4            Performance     Antwortzeit \< 2s (95%   API-Metriken, Actuator
                                               P95)                    Traces

         5            Testbarkeit         Hohe Abdeckung       Line/Branch Coverage,
                                         kritischer Logik          Mutation Score
  -------------------------------------------------------------------------------------

  : []{#_Toc209788237 .anchor}Tabelle 1 Qualitätsziele

## Stakeholder

  -----------------------------------------------------------------------
  **Rolle**           **Interesse**           **Relevante Aspekte**
  ------------------- ----------------------- ---------------------------
  Endbenutzer         Termine verwalten       Usability, Performance

  Administrator       Betrieb, Monitoring     Logs, Health, Backups

  Entwicklerteam      Weiterentwicklung       Codequalität, Tests
  -----------------------------------------------------------------------

  : []{#_Toc209788238 .anchor}Tabelle 2 Stakeholder

# Randbedingungen

## Technische Randbedingungen

  ---------------------------------------------------------------------------
  **Aspekt**               **Details**
  ------------------------ --------------------------------------------------
  **Backend-Framework**    Spring Boot 3.1.0 mit Java 17 (LTS) --
                           Spring Web, Spring Security, Spring Data JPA,
                           Spring Boot Actuator für Monitoring

  **Frontend-Framework**   Angular 20.3.x mit TypeScript -- Standalone 
                           Components, Signal-API, Material Design 20.x,
                           Reactive Forms, HTTP Interceptors

  **Datenbanken**          H2 (Entwicklung, datei-basiert), Azure MySQL 
                           Flexible Server (Produktion) mit JPA/Hibernate
                           automatischer Schema-Generierung

  **Build-Tools**          Maven 3.9+ (Backend), npm/Angular CLI (Frontend)
                           -- Separater Build-Prozess für Frontend/Backend

  **Deployment**           Dual-Server: Angular Dev Server (Port 4200) +
                           Spring Boot (Port 8080), Docker Compose Setup,
                           Azure Cloud Integration verfügbar

  **Admin-Features**       Umfassende Admin-Suite: Dashboard, System-
                           Monitoring, User-Management, Database-Viewer,
                           Deutsche Feiertage, Debug-Interfaces

  **Security & Auth**      JWT mit HS512 Signierung, BCrypt Passwort-
                           Hashing, rollenbasierte Autorisierung (USER/ADMIN),
                           CORS-Konfiguration für Development
  ---------------------------------------------------------------------------

  : []{#_Toc209788239 .anchor}Tabelle 3 Technische Randbedingungen

Technisch wird die Anwendung mit Angular 20.x im Frontend und Spring Boot 3.1.0 im
Backend umgesetzt. Das Frontend nutzt moderne Standalone Components ohne Module und
Signal-basiertes State Management. Die Datenhaltung erfolgt relational mit H2 
(Entwicklung) und Azure MySQL (Produktion). Die Kommunikation zwischen Frontend 
und Backend erfolgt über REST-APIs mit JWT-Authentifizierung. Das Backend bietet 
eine umfassende Admin-Suite mit System-Monitoring, User-Management und Debug-Tools. 
Der Betrieb erfolgt dual-server (4200 + 8080) oder containerisiert mit Docker.

## Organisatorische Randbedingungen

Organisatorisch wurde in einem kleinen Team, bestehend aus 5 Personen
gearbeitet; Quellcodeverwaltung liefen auf einer zentralen
Git‑Plattform. Die Hauptsächliche Kommunikation untereinander und die
Verwaltung der Dokumentationen lief über einen Discord Server, Welcher
zu Beginn des Projekts erstellt wurde. Kurzfristige Abstimmungen und
weiterer Austausch lief über eine WhatsApp Gruppe. Des Weiteren wurde
anfangs ein Projektplan erstellt, der grobe Vorgaben/ Ziele enthalten
hat.

# Außensicht

Die Außensicht beschreibt das System aus der Perspektive der Nutzer und
der Nachbarsysteme. Im Zentrum stehen drei Hauptbereiche: die Terminverwaltung,
die Admin-Suite und das System-Monitoring. Die Terminverwaltung umfasst die 
Erstellung, Bearbeitung, Verschiebung und Löschung von Terminen über eine 
moderne Angular-Oberfläche mit interaktiven Kalenderansichten (Monat/Woche/Tag).
Die Admin-Suite bietet umfassende Verwaltungsfunktionen für Systemadministratoren,
einschließlich User-Management, System-Monitoring und Database-Viewer.
Das erweiterte Datenmodell umfasst Entitäten für Benutzer, Termine, deutsche 
Feiertage und Kategorien, ergänzt durch Prioritäten, Erinnerungen und 
Wiederholungsoptionen.

Die Funktionen des Systems umfassen drei Hauptanwendungsbereiche:

**1. Endbenutzer-Funktionen (Port 4200 - Angular Frontend):**
- Benutzerregistrierung und JWT-basierte Anmeldung
- Interaktive Kalenderansichten (Monat/Woche/Tag)
- CRUD-Operationen für Termine mit erweiterten Eigenschaften
- Deutsche Feiertage-Integration nach Bundesländern
- Responsive Design für Mobile und Desktop

**2. Administrator-Funktionen (Port 8080 - Admin-Suite):**
- System-Dashboard mit Live-Statistiken
- User-Management (Aktivierung/Deaktivierung, Löschung)
- Database-Viewer für direkten Datenbankzugriff
- System-Monitoring mit JVM-Metriken und Health Checks
- Debug-Interfaces für API-Testing

**3. System-Integration:**
- REST-API mit über 20 Endpunkten
- Deutsche Feiertage-API (alle 16 Bundesländer)
- Spring Boot Actuator für Monitoring
- JWT-Authentifizierung mit rollenbasierter Autorisierung
Kategorien. Diese Use Cases sind in Kap. 2.4 der Spezifikation
aufgeführt und durch ein Use-Case-Diagramm visualisiert. Die
Benutzeroberfläche, einschließlich der Dialoglandkarte, Mockups und
Navigationsmöglichkeiten, ist ebenfalls Bestandteil der Spezifikation
(Kap. 4). Nachbarsysteme spielen nur eine untergeordnete Rolle:
Hauptsächlich handelt es sich um optionale E-Mail-Dienste, die für
Funktionen wie den Passwort-Reset genutzt werden können (Spezifikation,
Kap. 5). Im später folgenden Kapitel (4.1.1 Kontextabgrenzung) folgen
ausführliche Beschreibungen des fachlichen- und technischen Kontexts.

## Batch-Benutzerschnittstellen

Für das System sind derzeit keine Batch-Benutzerschnittstellen
vorgesehen. Alle Interaktionen erfolgen über die Weboberfläche oder
automatisiert über Rest-APIs. Typische Batch-Funktionen wie periodische
Import-/ Export-Jobs, Massenverarbeitung oder nächtliche Batch-Läufe
sind im aktuellen Projektkontext nicht vorgesehen und daher nicht
Bestandteil der Architektur.

## Nachbarsysteme

Nachbarsysteme spielen in der aktuellen Ausbaustufe nur eine
untergeordnete Rolle. Optional ist ein externer E‑Mail‑Dienst für den
Passwort‑Reset vorgesehen. Weitere Integrationen (z. B.
Push‑Benachrichtigungen) sind bewusst nicht Teil dieser Ausarbeitung und
werden in der Spezifikation lediglich als Perspektive erwähnt.

# Innensicht

Die Innensicht beschreibt die technische Gliederung und Umsetzung des
Systems. Die Anwendung folgt einer klassischen
Drei-Schichten-Architektur. In der Präsentationsschicht wird eine
Angular Single Page Application eingesetzt. Sie bildet die Schnittstelle
zum Benutzer, verarbeitet dessen Eingaben und kommuniziert
ausschließlich über eine REST-Schnittstelle mit dem Backend. Das Backend
basiert auf Spring Boot und enthält Controller, die HTTP-Anfragen
entgegennehmen, Services, welche die Geschäftslogik kapseln, sowie
Sicherheits- und Fehlerbehandlungskomponenten. Für die Datenhaltung
kommt Spring Data JPA in Kombination mit einer MySQL-Datenbank zum
Einsatz.

Der Betrieb der Anwendung erfolgt containerisiert. Sowohl das Backend
als auch die Datenbank werden in Docker-Containern betrieben. Der Start
und Stopp der Anwendung erfolgt über Docker Compose.

Die Erstellungssicht beschreibt die technische Basis der Entwicklung.
Das Backend wird in Java mit Spring Boot entwickelt und mit Maven
gebaut. Das Frontend basiert auf TypeScript und Angular und wird mit npm
kompiliert. So wird eine gleichbleibende Qualität der Software
sichergestellt.

In der physischen Sicht zeigt sich das System als eine Zusammensetzung
aus einem Webbrowser, der als Client dient, einem Spring Boot Backend,
das in einem Container ausgeführt wird, und einer MySQL-Datenbank, die
in einem separaten Container betrieben wird. Diese einfache, aber
robuste Systemlandschaft kann bei Bedarf durch einen Load Balancer oder
durch Container-Orchestrierung mit Kubernetes ergänzt werden, um
Skalierbarkeit und Ausfallsicherheit zu erhöhen.

Die Laufzeitsicht lässt sich am besten anhand von Sequenzdiagrammen
erläutern. In der Spezifikation sind exemplarisch die Use Cases
Anmeldung (UC2), Termin-Erstellung (UC3) und Passwort zurücksetzen (UC7)
dokumentiert. Sie verdeutlichen, wie Benutzeraktionen durch das Frontend
an das Backend übermittelt werden, dort validiert und weiterverarbeitet
werden und schließlich in der Persistenz Schicht gespeichert oder
verändert werden. Erfolgs- und Fehlerpfade werden ebenfalls
berücksichtigt, sodass der Ablauf für die Entwickler und Architekten
nachvollziehbar bleibt.

## Komponentensicht

### Kontextabgrenzung

#### Fachlicher Kontext

<figure>
<img src="docs/images/media/image2.png"
style="width:6.3in;height:3.94722in"
alt="Ein Bild, das Diagramm, Text, Entwurf, Plan enthält. KI-generierte Inhalte können fehlerhaft sein." />
<figcaption><p><span id="_Toc209788233" class="anchor"></span>Abbildung
1 Fachlicher Kontext</p></figcaption>
</figure>

Fachlich betrachtet interagieren Benutzer über den Web‑Browser mit einer
Kalenderoberfläche, um Termine anzulegen, zu ändern, zu verschieben, zu
suchen und zu löschen. Persistente Daten, Benutzer, Termine und
Kategorien werden im Backend validiert und in der Datenbank gespeichert.
Technisch betrachtet ist der Browser der einzige externe Client. Er
kommuniziert über HTTPS mit dem Spring‑Boot‑Dienst. Das Backend kapselt
sämtliche Zugriffe auf MySQL. Direkte Datenbankverbindungen von außen
sind ausgeschlossen. Die Grenze des Systems verläuft damit entlang der
REST‑Schnittstelle des Backends: alles dahinter (Geschäftslogik,
Persistenz, Security) ist Teil des Systems, alles davor (Client‑Geräte,
Netzwerkinfrastruktur) gehört zum Umfeld.

#### Technischer Kontext

<figure>
<img src="docs/images/media/image3.png"
style="width:6.58194in;height:2.95455in"
alt="Ein Bild, das Diagramm, Plan, Entwurf, Text enthält. KI-generierte Inhalte können fehlerhaft sein." />
<figcaption><p><span id="_Toc209788234" class="anchor"></span>Abbildung
2 Technischer Kontext</p></figcaption>
</figure>

Das technische Kontextdiagramm zeigt die Einbettung des Terminkalenders
in seine Umgebung. Der Nutzer greift über ein Endgerät mit einem
Webbrowser auf das System zu. Die Kommunikation erfolgt dabei über das
Internet mittels HTTPS.

Innerhalb des Systems ist das Angular-Frontend für die Darstellung der
Benutzeroberfläche und die Interaktion mit dem Anwender verantwortlich.
Es verarbeitet Benutzereingaben, führt erste Validierungen durch und
kommuniziert ausschließlich über REST-Schnittstellen mit dem Backend.

Das Spring-Boot-Backend stellt die Geschäftslogik, die
Authentifizierungs- und Autorisierungsmechanismen sowie die
Schnittstellen für die Termin- und Benutzerverwaltung bereit. Es ist
zudem für die zentrale Fehlerbehandlung und die Sicherheit (z. B.
JWT-Token, Rollenprüfung, Passwort-Hashing) zuständig.

Alle Daten wie Benutzerdaten, Termine und Kategorien werden in einer
relationalen MySQL-Datenbank persistiert. Das Backend greift über
JPA/Hibernate auf die Datenbank zu.

Durch diese klare Trennung in Browser-Frontend, Backend-Services und
Datenbank entsteht eine übersichtliche Systemarchitektur, die sich
leicht erweitern, warten und skalieren lässt.

#### Lösungsstrategie

Die Lösungsstrategie folgt der Trennung der Zuständigkeiten. Die
Angular‑SPA übernimmt die Darstellung und Interaktion, das
Spring‑Boot‑Backend bündelt Geschäftslogik und Sicherheitsmechanismen
und die relationale Datenbank gewährleistet Konsistenz und
Transaktionen. Sicherheit wird durch ein stateless
Authentifizierungsverfahren mit JWT und rollenbasierter Autorisierung
umgesetzt; Passwörter werden mit BCrypt gehasht. Datenqualität wird
serverseitig über DTO‑Validierungen und Geschäftsregeln (z. B. „Endzeit
muss nach Startzeit liegen") sichergestellt. Die Benutzeroberfläche ist
bewusst reduziert gestaltet. Häufige Aktionen (Termin erstellen/ändern)
sind ohne Kontextwechsel erreichbar, Such‑ und Filterfunktionen sind in
der Kalenderansicht integriert. Die Strategie begünstigt horizontale
Skalierung und reduziert Kopplung zwischen Client und Server.

### Bausteinsicht

<figure>
<img src="docs/images/media/image4.png"
style="width:6.85012in;height:5in"
alt="Ein Bild, das Text, Diagramm, Screenshot, Plan enthält. KI-generierte Inhalte können fehlerhaft sein." />
<figcaption><p><span id="_Toc209788235" class="anchor"></span>Abbildung
3 Bausteinsicht</p></figcaption>
</figure>

#### Verfeinerungsebene 0

Auf der obersten Ebene wird das Gesamtsystem als Terminkalender-System
betrachtet. Es umfasst alle Komponenten, die zur Bereitstellung der
Anwendung notwendig sind: die Benutzerschnittstelle im Browser, die
fachliche Logik im Backend sowie die persistente Speicherung in der
Datenbank.

#### Verfeinerungsebene 1

In der nächsten Verfeinerungsebene zeigt sich eine klare Aufteilung in
drei Hauptbausteine plus Admin-Suite:

**Frontend (Angular 20.x - Port 4200):** Als moderne Single-Page-Application 
mit Standalone Components realisiert, nutzt Signal-basiertes State Management 
und Material Design 20.x. Bietet interaktive Kalenderansichten, Terminverwaltung 
und JWT-basierte Authentifizierung. Kommuniziert über HTTP-Interceptors mit 
dem Backend.

**Backend (Spring Boot 3.1.0 - Port 8080):** Implementiert die fachliche Logik
mit layered Architecture (Controller, Service, Repository). Bietet REST-APIs 
für Termine, Benutzer und Feiertage sowie umfassende Admin-Funktionen. Integriert
Spring Boot Actuator für Monitoring und Health Checks.

**Admin-Suite (integriert in Backend):** Umfassende Web-basierte Verwaltungstools
einschließlich System-Dashboard, User-Management, Database-Viewer, System-Monitoring
und Debug-Interfaces. Speziell für Administratoren entwickelt.

**Datenbank (H2/Azure MySQL):** Persistiert Benutzer, Termine, deutsche Feiertage
und Systemdaten. H2 für Entwicklung, Azure MySQL Flexible Server für Produktion.
JPA/Hibernate für automatische Schema-Generierung.

#### Verfeinerungsebene 2 (Frontend)

Das moderne Angular 20.x Frontend nutzt Standalone Components ohne traditionelle Module.
Die interne Struktur ist wie folgt organisiert:

**Standalone Components (Ohne Module):** Jede Komponente importiert Dependencies
direkt und ist eigenständig. Wichtige Komponenten sind:
- `DashboardComponent`: Haupt-Dashboard mit Terminübersicht und Live-Statistiken
- `CalendarViewComponent`: Interaktive Kalenderansichten (Monat/Woche/Tag) mit Feiertagen
- `AppointmentFormComponent`: Termin-Erstellung/-Bearbeitung mit Validierung
- `LoginComponent`: JWT-basierte Authentifizierung mit Fehlerbehandlung
- `AppointmentsComponent`: Terminliste mit Such- und Filterfunktionen

**Signal-basierte Services:** Moderne Services nutzen Angular Signals für
reaktives State Management ohne komplexe RxJS-Streams:
- `AuthService`: JWT-Management, Benutzer-Status mit Signals
- `AppointmentService`: CRUD-Operationen, Signal-basierte Datenhaltung
- `CalendarService`: Kalender-API-Integration, Datums-Utilities
- `HolidayService`: Deutsche Feiertage-API mit Bundesland-Filter

**HTTP-Interceptors:** Funktionsbasierte Interceptors für automatische JWT-Token-
Injection und Fehlerbehandlung bei allen API-Calls.

**Funktionsbasiertes Routing:** Moderne Route Guards als Funktionen (nicht Klassen)
mit Lazy Loading für Performance-Optimierung.

**Material Design 20.x:** Vollständige Integration der neuesten Material Design
Komponenten mit Angular CDK für erweiterte UI-Features (Drag & Drop ready).

**TypeScript Models:** Typsichere Interfaces für API-Kommunikation im `models/`
Ordner (User, Appointment, Holiday, CalendarEvent).

Diese moderne Architektur eliminiert das Modul-System und nutzt die neuesten
Angular-Features für bessere Performance und Entwicklererfahrung.
Darstellung, Services um die Logik und Kommunikation mit dem Backend,
Module sorgen für Ordnung, Routing für die Navigation und gegebenenfalls
das State-Management für eine konsistente Zustandsverwaltung. Damit
entsteht eine übersichtliche Architektur, die sowohl wartbar als auch
erweiterbar ist.

#### Verfeinerungsebene 2 (Backend - Spring Boot 3.1.0)

Das Backend folgt einer strikten Layered Architecture mit erweiterten Admin-Funktionen:

**Controller-Layer (REST-Endpunkte):** Über 20 REST-Endpunkte in verschiedenen Controllern:
- `AuthController`: JWT-basierte Authentifizierung (/api/v1/auth/*)
- `AppointmentController`: CRUD-Operationen für Termine (/api/v1/appointments/*)
- `CalendarController`: Kalenderansichten (/api/v1/calendar/*)
- `HolidayController`: Deutsche Feiertage-API (/api/v1/holidays/*)
- `AdminController`: User-Management für Administratoren (/api/v1/admin/*)
- `ActuatorEndpoints`: System-Monitoring (/actuator/*)

**Service-Layer (Geschäftslogik):** Umfassende Business Logic mit strikter Kapselung:
- `AppointmentService`: Terminverwaltung, Konfliktprüfung, CRUD-Operationen
- `AuthService`: JWT-Generierung, Passwort-Hashing (BCrypt), Rollen-Management
- `CalendarService`: Kalenderansichten, Datums-Berechnungen
- `HolidayService`: Deutsche Feiertage (alle 16 Bundesländer), automatische Generierung
- `AdminService`: User-Management, System-Statistiken

**Repository-Layer (Datenzugriff):** Spring Data JPA Repositories mit Query Methods:
- `UserRepository`: Benutzer-CRUD, Aktivierungsstatus-Management
- `AppointmentRepository`: Termine mit erweiterten Filtern und Queries
- `HolidayRepository`: Feiertage mit Bundesland-spezifischen Abfragen
- Alle Repositories erweitern JpaRepository für automatische CRUD-Operationen

**Admin-Suite Integration:** Web-basierte Admin-Tools direkt im Backend:
- System-Dashboard (HTML-Interface)
- Database-Viewer für direkten DB-Zugriff
- User-Management mit Aktivierung/Deaktivierung
- System-Monitoring mit JVM-Metriken
- Debug-Interfaces für API-Testing

**Datenbank-Integration:** Multi-Environment Database Setup:
- H2 (Entwicklung): Datei-basiert mit automatischer Initialisierung
- Azure MySQL (Produktion): Flexible Server mit JPA Schema-Generierung
- Automatische Feiertage-Generierung beim ersten Start (2024-2030)

Durch diese hierarchische Struktur -- vom Gesamtsystem über die
Hauptbausteine bis hin zur detaillierten Whitebox-Sicht des Backends --
wird eine klare Trennung der Verantwortlichkeiten erreicht. Dies
erleichtert nicht nur die Verständlichkeit der Architektur, sondern
sorgt auch für Wartbarkeit, Erweiterbarkeit und eine saubere Umsetzung
der definierten Qualitätsziele.

## Betreibersicht

Die Betreibersicht beschreibt alle Aspekte rund um Installation,
Betrieb, Wartung, Notfallmaßnahmen und Kompatibilität der
Terminkalender-Anwendung. Sie richtet sich an Administratoren, Betreiber
und Support-Teams und stellt sicher, dass das System zuverlässig, sicher
und effizient betrieben werden kann.

### Übersicht der wichtigsten Betriebsaufgaben

  -----------------------------------------------------------------------
  Aufgabe                 Beschreibung            Tools/Interface
  ----------------------- ----------------------- -----------------------
  Installation            Dual-Setup: Frontend   npm start (Port 4200)
                          (Angular) + Backend     + mvn spring-boot:run
                          (Spring Boot)           (Port 8080)

  System-Monitoring       Live-Überwachung von    Admin-Dashboard:
                          JVM, DB, Health-Status  /admin-dashboard.html
                                                 /system-monitoring.html

  User-Management         Benutzer aktivieren,    User-Management:
                          deaktivieren, löschen   /user-management.html

  Database-Management     Direkter DB-Zugriff,    Database-Viewer:
                          Daten einsehen/ändern   /azure-database-viewer.html

  Debug & Testing         API-Testing, System-    Debug-Interfaces:
                          Debugging, Logs         /debug-interface.html

  Backup                  Automatische DB-Dumps,  mysqldump (Produktion)
                          H2-Dateien (Dev)        H2-Export (Entwicklung)
                          Sicherung der           
                          Konfiguration           

  Restore                 Backup einspielen,      Admin/Operator
                          Anwendung neu starten   

  Monitoring              Systemressourcen &      Operator
                          Health-Checks           
                          überwachen              

  Update                  Neue Version bauen,     Administrator
                          einspielen, testen      

  Notfallmaßnahmen        Neustart, Fehleranalyse Operator
  -----------------------------------------------------------------------

  : []{#_Toc209788240 .anchor}Tabelle 4 Betriebsaufgaben

**Detaillierte Beschreibung der Betriebsaufgaben:**

**Installation und Deployment:** Die Terminkalender-Anwendung verwendet ein Dual-Server-Setup, bei dem Frontend und Backend als separate Services betrieben werden. Das Angular-Frontend läuft standardmäßig auf Port 4200 und wird über `npm start` gestartet, während das Spring Boot Backend auf Port 8080 über `mvn spring-boot:run` ausgeführt wird. Diese Architektur ermöglicht eine klare Trennung der Verantwortlichkeiten und erleichtert die Wartung. Für die Produktionsumgebung kann alternativ ein containerisiertes Setup mit Docker Compose verwendet werden, das beide Services orchestriert und dabei automatisch die Datenbank-Initialisierung und Netzwerkkonfiguration übernimmt.

**System-Monitoring und Überwachung:** Das integrierte Admin-Dashboard bietet eine umfassende Live-Überwachung aller kritischen Systemkomponenten. Über die Weboberfläche `/admin-dashboard.html` haben Administratoren Zugriff auf Real-time-Metriken der Java Virtual Machine (JVM), einschließlich Speicherverbrauch, Garbage Collection-Statistiken und Thread-Status. Zusätzlich werden Datenbankverbindungen, API-Response-Zeiten und allgemeine Health-Checks überwacht. Das System nutzt Spring Boot Actuator-Endpunkte, um detaillierte Metriken bereitzustellen, die sowohl über die Web-Oberfläche als auch programmatisch abgerufen werden können. Für kritische Systemereignisse werden automatische Warnungen generiert, und historische Daten ermöglichen die Analyse von Performance-Trends.

**User-Management und Benutzerverwaltung:** Die zentrale Benutzerverwaltung erfolgt über das spezialisierte Interface `/user-management.html`, das Administratoren vollständige Kontrolle über alle Benutzerkonten bietet. Zu den verfügbaren Funktionen gehören das Aktivieren und Deaktivieren von Benutzerkonten, die Zuweisung und Änderung von Rollen (USER/ADMIN), das Zurücksetzen von Passwörtern und die vollständige Löschung von Benutzerkonten einschließlich aller zugehörigen Daten. Das Interface bietet Such- und Filterfunktionen für große Benutzerbasen, Bulk-Operationen für effiziente Verwaltung und detaillierte Benutzerstatistiken. Alle Verwaltungsaktionen werden protokolliert und können über Audit-Logs nachvollzogen werden.

**Database-Management und Datenverwaltung:** Der integrierte Database-Viewer unter `/azure-database-viewer.html` ermöglicht direkten, webbasierten Zugriff auf die Produktionsdatenbank ohne zusätzliche Clients oder Tools. Administratoren können Tabellendaten einsehen, einfache Abfragen ausführen, Datenintegrität prüfen und bei Bedarf direkte Korrekturen vornehmen. Das Interface unterstützt sowohl H2-Entwicklungsdatenbanken als auch Azure MySQL-Produktionsinstanzen und bietet Funktionen wie Datenexport, Tabellenstatistiken und Indexanalyse. Sicherheitsfeatures wie rollenbasierte Zugriffskontrolle und Audit-Logging sorgen dafür, dass alle Datenbankzugriffe nachvollziehbar bleiben.

**Debug-Tools und API-Testing:** Das umfassende Debug-Interface `/debug-interface.html` stellt Entwicklern und Administratoren wichtige Werkzeuge für Systemdiagnose und API-Testing zur Verfügung. Es bietet eine interaktive REST-API-Konsole zum Testen aller verfügbaren Endpunkte, automatische JWT-Token-Verwaltung für authentifizierte Anfragen und detaillierte Request/Response-Logs. Zusätzlich sind System-Diagnosefunktionen integriert, die Informationen über Konfiguration, Umgebungsvariablen, aktive Profile und Datenbankverbindungen bereitstellen. Performance-Monitoring-Tools zeigen Response-Zeiten, Error-Rates und Throughput-Statistiken an, während Log-Viewer Zugriff auf Anwendungslogs in Echtzeit ermöglichen.

### Systemvoraussetzungen und Installation

**Backend-Anforderungen:**
- Java 17+ (LTS empfohlen)
- Maven 3.9+
- H2 (automatisch) oder Azure MySQL Flexible Server

**Frontend-Anforderungen:**
- Node.js 18+ (LTS empfohlen)
- npm 9+ oder Angular CLI 17+
- Moderne Browser (Chrome 90+, Firefox 88+, Safari 14+, Edge 90+)

**Unterstützte Betriebssysteme:** Windows 10/11, macOS 12+, Linux (Ubuntu 20.04+)

**Installation (Dual-Server Setup):**

1. **Repository klonen:**
   ```bash
   git clone https://github.com/Finnbmb/Projekt1.git
   cd Projekt1
   ```

2. **Backend starten (Terminal 1):**
   ```bash
   mvn spring-boot:run
   # Läuft auf http://localhost:8080
   ```

3. **Frontend starten (Terminal 2):**
   ```bash
   cd frontend
   npm install
   npm start
   # Läuft auf http://localhost:4200
   ```

**Zugriffspunkte nach Installation:**
- **Hauptanwendung:** http://localhost:4200 (Angular Frontend)
- **Admin-Dashboard:** http://localhost:8080/admin-dashboard.html
- **System-Monitoring:** http://localhost:8080/system-monitoring.html
- **User-Management:** http://localhost:8080/user-management.html
- **Database-Viewer:** http://localhost:8080/azure-database-viewer.html
- **API-Testing:** http://localhost:8080/debug-interface.html

**Automatische Initialisierung:** Beim ersten Start werden H2-Datenbank und 
deutsche Feiertage (2024-2030) automatisch generiert.

### Betrieb und Systemstart

Das Starten und Anhalten des Systems ist sowohl für Entwicklung als auch
Produktion klar geregelt. Während der Entwicklung wird meist \`mvn
spring-boot:run\` verwendet, was schnelles Testen und Hot-Reload
ermöglicht. Im Produktivbetrieb wird das gebaute JAR mit \`java -jar
\...\` gestartet, was Stabilität und Portabilität gewährleistet. Das
Anhalten erfolgt durch das Beenden des Prozesses im Terminal, per
Systemdienst oder -- bei Containern -- mit \`docker-compose down\`.

### Mehrfachinstallation und Koexistenz

Die Anwendung kann mehrfach auf einem Rechner installiert und betrieben
werden, sofern unterschiedliche Ports und Datenbanknamen verwendet
werden. Dies ermöglicht den parallelen Betrieb mehrerer, unterschiedlich
parametrierter Instanzen, etwa für Test- und Produktivumgebungen. Auch
der parallele Betrieb mit anderen Java-Anwendungen oder Webservern ist
problemlos möglich, solange Port-Konflikte vermieden werden. MySQL kann
gemeinsam mit anderen Anwendungen genutzt werden, sofern
unterschiedliche Datenbanken verwendet werden. Bei parallelem Betrieb
mit anderen Webservern (z.B. Apache, Tomcat) ist auf unterschiedliche
Ports und ggf. eine Reverse Proxy-Konfiguration zu achten.

### Betriebsressource

Für den stabilen Betrieb sind ausreichend Festplattenspeicher für
Datenbank, Backups und Logdateien sowie genügend Arbeitsspeicher und
CPU-Ressourcen erforderlich. Für E-Mail-Benachrichtigungen wird ein
SMTP-Server benötigt.

### Backup, Recovery und Notfallmaßnahmen

Regelmäßige Backups der MySQL-Datenbank werden empfohlen, etwa durch
tägliche Dumps mit \`mysqldump\`. Auch die Konfigurationsdateien sollten
regelmäßig gesichert werden. Im Notfall kann das letzte Backup mit
\`mysql\` oder einem Datenbank-Tool wieder eingespielt werden. Die
Integrität der Backups wird regelmäßig überprüft und Test-Restores
werden durchgeführt, um die Wiederherstellbarkeit sicherzustellen.

Im Falle von Zwischenfällen sind verschiedene Maßnahmen vorgesehen:
Fällt ein App-Server aus, übernimmt der Load Balancer das Routing auf
verbleibende Instanzen. Bei Ausfall der Datenbank empfiehlt sich ein
Failover-Cluster oder ein Backup-Server. Redundante Netzwerkverbindungen
und Monitoring sorgen für zusätzliche Ausfallsicherheit. Bei Ausfall
einzelner Software-Komponenten werden diese gezielt neu gestartet, wobei
Logs und Monitoring-Daten zur Fehleranalyse herangezogen werden.

### Monitoring und Updates

**Integrierte Admin-Suite für Monitoring:**
Das System bietet eine umfassende Web-basierte Admin-Suite für Monitoring und Verwaltung:

- **System-Monitoring Dashboard** (/system-monitoring.html): Live-Anzeige von
  JVM-Metriken (Memory, GC, Threads), Database-Status, HTTP-Anfragen und System-Health
- **Admin-Dashboard** (/admin-dashboard.html): Zentrale Übersicht mit Service-Karten,
  Live-Statistiken und Quick-Links zu allen Admin-Tools
- **User-Management** (/user-management.html): Benutzerkonten verwalten, aktivieren/
  deaktivieren, Rollen zuweisen
- **Database-Viewer** (/azure-database-viewer.html): Direkter Web-Zugriff auf 
  Datenbanktabellen für Wartung und Troubleshooting

**Spring Boot Actuator Integration:**
alle Anfragen, Fehler und Systemereignisse protokolliert und bei Bedarf
an zentrale Systeme wie den ELK-Stack weitergeleitet werden. Regelmäßige
Updates der Abhängigkeiten, die Überwachung auf Sicherheitslücken (z.B.
mit Dependabot) und eine konsequente Zugriffskontrolle sorgen für einen
sicheren Betrieb.

## Erstellungssicht

Die Erstellungssicht beschreibt die Struktur und Organisation der
Softwareartefakte, aus denen das Laufzeitsystem der
Terminkalender-Anwendung gebaut wird. Sie gibt einen Überblick über alle
technischen Einheiten wie Quellcode, Bibliotheken, Skripte, Build-Files
und ausführbare Programme. Zudem wird die Verteilung dieser Artefakte
auf die Zielsysteme erläutert.

### Programmquellen

**Backend (Spring Boot 3.1.0):**
Der Backend-Quellcode befindet sich im GitHub Repository unter `src/main/java/de/swtp1/terminkalender/`:

- `controller/`: REST-API-Controller (Auth, Appointment, Calendar, Holiday, Admin)
- `service/`: Business Logic Services (Appointment, Auth, Calendar, Holiday, Admin)
- `repository/`: Spring Data JPA Repositories für Datenzugriff
- `entity/`: JPA-Entitäten (User, Appointment, Holiday)
- `dto/`: Data Transfer Objects für API-Requests/Responses
- `config/`: Konfigurationsklassen (Security, CORS, Database)
- `util/`: Utility-Klassen (SecurityUtils, DateUtils)

**Frontend (Angular 20.x):**
Der Frontend-Quellcode befindet sich unter `frontend/src/app/`:

- `components/`: Wiederverwendbare UI-Komponenten (Login, Dialoge)
- `services/`: Angular Services für API-Kommunikation und State Management
- `models/`: TypeScript Interfaces für Datenmodelle
- `guards/`: Route Guards für Authentifizierung
- `interceptors/`: HTTP-Interceptors für JWT-Token-Handling
- `appointments/`: Terminverwaltungs-Komponente
- `calendar-view/`: Kalenderansichts-Komponente
- `dashboard/`: Dashboard-Komponente

**Admin-Tools (Static HTML/CSS/JS):**
Web-basierte Admin-Interfaces unter `src/main/resources/static/`:

- `admin-dashboard.html`: Zentrale Admin-Übersicht
- `system-monitoring.html`: System-Monitoring mit Live-Metriken
- `user-management.html`: Benutzerverwaltung
- `azure-database-viewer.html`: Database-Viewer
- `debug-interface.html`: API-Testing-Interface

- config: Konfigurationen und Security

### Statische und dynamische Bibliotheken

Die Anwendung nutzt zahlreiche externe Bibliotheken, die über Maven im
\`pom.xml\` verwaltet werden. Dazu zählen u.a.:

- Spring Boot Starter (Web, Data JPA, Security)

- MySQL Connector

- JWT (JSON Web Token)

- Lombok

- Logback

- JUnit, Mockito (Test)

Diese Bibliotheken werden beim Build automatisch eingebunden und als
Teil des ausführbaren JARs ausgeliefert. Sie definiert alle
Abhängigkeiten, Plugins und Build-Prozesse. Der Build erzeugt ein
ausführbares JAR im Verzeichnis \`target/\`.

## Physische Sicht

### Verteilung auf Zielsysteme

Die Softwareartefakte werden wie folgt verteilt:

\- App-Server: Enthält das JAR-File, Konfigurationsdateien und ggf.
Skripte

\- Datenbank-Server: MySQL-Installation, ggf. Initialisierungsskripte

\- Monitoring-Server: (Optional) Monitoring-Tools und Konfigurationen

Die Verteilung kann lokal, auf mehreren Servern oder in der Cloud
erfolgen. Die Erstellungssicht stellt sicher, dass alle notwendigen
Artefakte für einen erfolgreichen Build und Betrieb bereitgestellt
werden.

### Genutzte physische Geräte

Die physische Sicht beschreibt die reale Hardware- und Netzwerkstruktur,
auf der die Terminkalender-Anwendung betrieben wird. Sie konzentriert
sich auf die genutzten Geräte und deren Zusammenspiel, ohne dabei
bereits in die Details der Softwareverteilung oder Prozesssteuerung zu
gehen.

Das System nutzt folgende physische Ressourcen:

- Server/Computer: Für die Ausführung der Anwendung (App-Server) und der
  Datenbank (MySQL-Server). Diese können als physische oder virtuelle
  Maschinen bereitgestellt werden und sind oft gemeinsam mit anderen
  Systemen im Einsatz (z.B. in einer Cloud- oder
  Virtualisierungsumgebung).

- Netzwerkverbindungen: Ethernet, WLAN oder VPN zur Kommunikation
  zwischen App-Server, Datenbank und ggf. Monitoring-System. Die
  Leitungen werden gemeinsam mit anderen Systemen genutzt, sind aber für
  die Verfügbarkeit und Performance kritisch.

- Bildschirme: Für Administratoren und Nutzer, die auf die Weboberfläche
  zugreifen. Die Anwendung selbst benötigt keine dedizierten Bildschirme
  auf dem Server.

[Deploymentdiagramm erstellen!]{.mark}

- Drucker: Werden nur benötigt, wenn Ausdrucke von Terminen oder Listen
  erfolgen sollen. Die Anwendung kann mit beliebigen, im Netzwerk
  verfügbaren Druckern zusammenarbeiten.

### Ausführbare Programme

Das Hauptartefakt ist das JAR-File, z.B.
\`terminkalender-0.0.1-SNAPSHOT.jar\`, das auf jedem Zielsystem mit Java
17+ ausgeführt werden kann. Für den Produktivbetrieb kann das JAR als
Systemdienst oder in einem Docker-Container betrieben werden.

## Laufzeitsicht

Zur Laufzeit zeigt sich das System als Abfolge klarer Schritte zwischen
Browser, Backend und Datenbank. Wenn ein Benutzer sich anmeldet, sendet
das Frontend die Anmeldedaten an einen REST-Endpunkt des Backends. Dort
werden die Daten validiert und die Zugangsdaten gegen die gespeicherten
Passwort-Hashes geprüft. Bei Erfolg erzeugt das Backend ein kurzlebiges
JWT und gibt es an den Browser zurück. Anschließende Anfragen führen das
Token im Authorization-Header mit; das Backend verifiziert das Token,
ermittelt daraus Identität und Rollen und führt die gewünschte Operation
aus.

Bei der Erstellung eines Termins löst der Nutzer die Aktion in der
Kalenderoberfläche aus. Das Frontend überträgt die Termindaten als JSON.
Der zuständige Controller validiert die Nutzlast, die Domänenschicht
prüft Geschäftsregeln (u. a. die zeitliche Konsistenz) und schreibt den
Datensatz über JPA in die relationale Datenbank. Die Antwort enthält den
gespeicherten Termin; das Frontend aktualisiert daraufhin die Ansicht
ohne Seiten-Reload. Fehlerfälle -- z. B. ungültige Zeitintervalle oder
fehlende Pflichtfelder -- werden zentral abgefangen und als konsistente
JSON-Fehlermeldungen zurückgegeben.

Der Passwort-Reset beginnt mit der Anforderung eines Reset-Links. Das
Backend erzeugt ein Einmal-Token, persistiert dessen Gültigkeit und
adressiert den Versand an den konfigurierten E-Mail-Dienst. Über den
Link setzt der Benutzer ein neues Passwort, das nach Validierung gehasht
in der Datenbank gespeichert wird. Timeouts, Wiederholungen bei
transienten Fehlern und strukturierte Protokollierung sorgen dafür, dass
die Abläufe robust bleiben und im Betrieb nachvollzogen werden können.

### Beispielhafter Ablauf: Termin-Erstellung

<figure>
<img src="docs/images/media/image5.png"
style="width:6.10208in;height:2.86806in"
alt="Ein Bild, das Text, Screenshot, Schrift enthält. KI-generierte Inhalte können fehlerhaft sein." />
<figcaption><p><span id="_Toc209788236" class="anchor"></span>Abbildung
4 Ablauf Terminerstellung</p></figcaption>
</figure>

Das Zusammenspiel der Komponenten ist klar definiert: Der Web-Client
sendet HTTP-Anfragen an die Controller des Backends. Diese validieren
die Daten und delegieren die Geschäftslogik an die Service-Klassen. Die
Services greifen über die Repositories auf die Datenbank zu.
Sicherheits- und Fehlerbehandlungskomponenten sind als
Querschnittsfunktionen integriert und sorgen für einen robusten und
sicheren Ablauf. Beispielhaft sei die Termin-Erstellung genannt: Der
Benutzer sendet eine Anfrage, der Controller prüft und leitet weiter,
der Service prüft auf Kollisionen und speichert den Termin, die Antwort
wird an den Client zurückgegeben. Ebenso läuft die Authentifizierung ab:
Die Zugangsdaten werden geprüft, ein JWT-Token wird erstellt und für
weitere Anfragen genutzt.

Die Architektur ist so gestaltet, dass sie leicht erweiterbar ist. Neue
Komponenten wie Benachrichtigungsdienste, externe Kalenderintegration
oder zusätzliche Entitäten können problemlos ergänzt werden. Die klare
Trennung der Schichten und die Nutzung etablierter Frameworks sorgen für
Wartbarkeit, Testbarkeit und Skalierbarkeit der Anwendung. Die
Komponentensicht bildet damit das stabile Fundament für die gesamte
Systemarchitektur.

# Übergreifende Aspekte

## Qualitätsmerkmale

Die Qualität der Terminkalender-Anwendung wird durch verschiedene
Merkmale geprägt, die sowohl technische als auch nutzerorientierte
Anforderungen abdecken. Dieses Kapitel betrifft die ursprünglichen
Qualitätsziele welche unter den nichtfunktionalen Anforderungen aus der
Spezifikation (Kapitel 2.3) zu finden sind.

### Benutzerfreundlichkeit und Wartbarkeit

Ein zentrales Qualitätsziel des Systems ist die Benutzerfreundlichkeit.
Durch die Gliederung des Frontends in Komponenten, Services, Routing und
Module können Nutzer intuitiv zwischen verschiedenen Kalenderansichten
navigieren und Termine effizient verwalten. Das optionale State
Management sorgt für konsistente Einstellungen wie Filter, während die
Umsetzung als Single-Page-Application schnelle Reaktionen ohne
Seitenneuladen ermöglicht. So entsteht eine flüssige und leicht
verständliche Nutzungserfahrung.

Ein weiteres wichtiges Ziel ist die Wartbarkeit. Die klare
Schichtenarchitektur im Backend trennt Controller, Services und
Repositories, sodass Änderungen in einer Schicht keine Auswirkungen auf
andere haben. Auch die modulare Struktur im Frontend unterstützt
Erweiterungen und Anpassungen. Auf diese Weise bleibt das System gut
pflegbar, testbar und flexibel erweiterbar.

### Zuverlässigkeit

Die Zuverlässigkeit und Konsistenz des Systems werden durch das
optionale State Management unterstützt. Es stellt sicher, dass Zustände
wie Filtereinstellungen oder der aktuell eingeloggte Benutzer zentral
verwaltet und konsistent allen relevanten Komponenten zur Verfügung
gestellt werden. Damit wird verhindert, dass widersprüchliche Zustände
entstehen.

### Leistungsfähigkeit und Effizienz

Schließlich spielen die Performance und Skalierbarkeit eine wichtige
Rolle. Durch die stateless Architektur des Spring-Boot-Backends können
mehrere Instanzen parallel betrieben und über einen Webserver verteilt
werden. Die Auslagerung der Datenhaltung in eine MySQL-Datenbank sorgt
für eine stabile Grundlage bei wachsendem Datenvolumen, während das
Frontend als Single-Page-Application dem Nutzer eine reaktive und
performante Bedienung ermöglicht.

### Sicherheit

Ein wesentliches Qualitätsziel ist außerdem die Sicherheit. Im Backend
ist dies insbesondere in der Service- und Controller-Schicht verankert,
wo Authentifizierungs- und Autorisierungsmechanismen (z. B. JWT)
umgesetzt werden. So wird gewährleistet, dass nur berechtigte Nutzer
Zugriff auf sensible Funktionen und Daten erhalten.

# Architekturentscheidungen

## Einleitung

Bei der Wahl der Technologien wurden Stabilität und langfristige
Wartbarkeit berücksichtigt. Für das Backend fiel die Wahl auf Java mit
Spring Boot, da es ein stabiles Framework mit einem großen Ökosystem
ist. Für das Frontend wurde Angular ausgewählt, da es eine klare
Struktur und einheitliche Entwicklungsparadigmen bietet. Die Daten
werden in einer relationalen MySQL-Datenbank gespeichert, die sowohl
robust als auch weit verbreitet ist. Für die Authentifizierung wurde JWT
gewählt, da dieses Verfahren einfach, stateless und leicht skalierbar
ist. Die Schnittstellen sind als REST-Endpunkte realisiert, da sie
leicht verständlich und mit einer Vielzahl von Clients kompatibel sind.
Für das Deployment wird Docker eingesetzt, optional ergänzt durch
Kubernetes, um eine flexible Skalierung zu ermöglichen.

## Authentifizierung und Sicherheit

Die Anwendung implementiert ein umfassendes Sicherheitskonzept mit mehreren Ebenen:

**JWT-basierte Authentifizierung:** Stateless JWT (JSON Web Tokens) mit HS512-Signierung
ermöglichen horizontale Skalierbarkeit. Tokens enthalten Benutzer-ID, Username und Rolle,
werden im Frontend sicher gespeichert und automatisch via HTTP-Interceptor übertragen.

**Rollenbasierte Autorisierung:** Zwei Rollen (USER, ADMIN) mit unterschiedlichen
Berechtigungen. USER können nur eigene Termine verwalten, ADMIN haben zusätzlich
Zugriff auf User-Management und Admin-Tools.

**Passwort-Sicherheit:** BCrypt-Hashing mit Salt für alle Passwörter. Sichere
Passwort-Reset-Funktionalität mit temporären Tokens.

**Frontend-Sicherheit:** Angular HTTP-Interceptors für automatische JWT-Übertragung,
Route Guards für geschützte Bereiche, XSS-Schutz durch Angular's eingebaute Sanitization.

**Backend-Sicherheit:** Spring Security mit CSRF-Schutz, CORS-Konfiguration für
Cross-Origin-Requests, Input-Validierung auf allen API-Endpunkten.

**Admin-Bereich-Sicherheit:** Separate Autorisierung für Admin-Tools, Session-basierte
Zugriffskontrolle für sensitive Funktionen wie Database-Viewer.

### Umsetzungsbeispiele (A&S)

#### Security-Konfiguration

Der Einstiegspunkt für die Sicherheitslogik ist eine
Konfigurationsklasse. Hier wird festgelegt, welche Endpunkte frei
erreichbar sind (z. B. /auth/login), und dass alle anderen Anfragen
authentifiziert sein müssen.

#### Codebeispiel (Moderne Security-Konfiguration)

```java
@Configuration
@EnableWebSecurity
@Order(2) // Niedrigere Priorität als H2SecurityConfig
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Actuator Endpoints für Monitoring
                .requestMatchers("/actuator/**").permitAll()
                // Database Controller für direkten DB-Zugriff
                .requestMatchers("/database/**").permitAll()
                // Debug API für Testing
                .requestMatchers("/debug-api/**").permitAll()
                // Auth Endpunkte
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Holiday API für öffentlichen Zugriff
                .requestMatchers("/api/v1/holidays/**").permitAll()
                // Statische Ressourcen und HTML-Files
                .requestMatchers("/*.html").permitAll()
                .requestMatchers("/static/**", "/css/**", "/js/**").permitAll()
                // Alle anderen Endpunkte erfordern Authentifizierung
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

#### JWT-Generierung

Beim erfolgreichen Login wird ein signiertes JWT erstellt und an den
Client zurückgegeben

*public String generateToken(String username) {*

*return Jwts.builder()*

*.setSubject(username)*

*.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h*

*.signWith(SignatureAlgorithm.HS256, secretKey)*

*.compact();*

}

#### Autorisierung im Controller

```java
// Rollenbasierte Autorisierung für Admin-Funktionen
@DeleteMapping("/appointments/{id}")
@PreAuthorize("hasRole('ADMIN') or @appointmentService.isOwner(#id, authentication.name)")
public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
    appointmentService.delete(id);
    return ResponseEntity.noContent().build();
}

@GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(adminService.getAllUsers());
}
```

## Deutsche Feiertage-Integration

Ein besonderes Feature der Anwendung ist die vollständige Integration deutscher
Feiertage für alle 16 Bundesländer:

**Automatische Generierung:** Beim ersten Systemstart werden automatisch deutsche
Feiertage für die Jahre 2024-2030 generiert, einschließlich beweglicher Feiertage
wie Ostern, Pfingsten und regionaler Feiertage.

**Bundesland-spezifische Feiertage:** Unterstützung für alle 16 deutschen Bundesländer
mit ihren spezifischen Feiertagen (z.B. Heilige Drei Könige in Bayern, Reformationstag
in verschiedenen Ländern).

**REST-API für Feiertage:**
- `GET /api/v1/holidays/{year}`: Alle Feiertage eines Jahres
- `GET /api/v1/holidays/{year}/{federalState}`: Bundesland-spezifische Feiertage
- `GET /api/v1/holidays/check?date={date}&federalState={state}`: Prüfung ob Datum ein Feiertag ist

**Kalender-Integration:** Feiertage werden automatisch in allen Kalenderansichten
(Monat/Woche/Tag) angezeigt mit spezieller Hervorhebung und Tooltips.

**Admin-Interface:** Separate HTML-Oberfläche (/holiday-viewer.html) für die
Verwaltung und Anzeige der Feiertage mit Filter- und Suchfunktionen.

## Multi-Environment Datenbank-Strategie

Die Anwendung unterstützt verschiedene Datenbank-Umgebungen für optimale
Entwicklungs- und Produktions-Workflows:

**Entwicklungsumgebung (H2):** Datei-basierte H2-Datenbank für lokale Entwicklung
mit automatischer Schema-Generierung und Testdaten-Initialisierung.

**Produktionsumgebung (Azure MySQL):** Azure MySQL Flexible Server für
skalierbare, hochverfügbare Produktion mit automatischen Backups.

**Automatische Schema-Migration:** JPA/Hibernate generiert automatisch das
Datenbankschema basierend auf den Entity-Klassen.

**Erweiterte Datenstrukturen:**
- Appointments: Titel, Beschreibung, Start/Ende, Ort, Priorität, Kategorie, Erinnerungen
- Users: Erweiterte Felder für Status, Rollen, Aktivierung
- Holidays: Deutsche Feiertage mit Bundesland-Zuordnung
- Audit-Felder: Created/Updated Timestamps für alle Entitäten

### Umsetzungsbeispiele (DB-Technologie)

# Entwicklungsumgebung (H2)
spring:
  datasource:
    url: jdbc:h2:file:./data/terminkalender
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# Produktionsumgebung (Azure MySQL)
spring:
  datasource:
    url: jdbc:mysql://dbotk25.mysql.database.azure.com:3306/terminkalender?useSSL=true&serverTimezone=UTC
    username: fbmb16
    password: PalKauf91
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel ist erforderlich")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Startdatum/-zeit ist erforderlich")
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @NotNull(message = "Enddatum/-zeit ist erforderlich")
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    private String location;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "reminder_minutes")
    private Integer reminderMinutes = 15;
}

*public interface AppointmentRepository*

*extends JpaRepository\<Appointment, Long\> {*

*List\<Appointment\> findByTitleContaining(String keyword);*

*}*

# Querschnittskonzepte

## Sicherheit (JWT mit Spring Security)

### Zweck

Schutz der Benutzerkonten und Termindaten durch ein Token-basiertes
Verfahren.

### Kernkonzept

Nutzer authentifizieren sich beim Login mit Benutzername/E-Mail und
Passwort.

Nach erfolgreicher Anmeldung erstellt das Backend ein signiertes JWT,
das Benutzerinformationen und Rollen enthält.

Das Token wird vom Client in jedem Request im Header (Authorization:
Bearer \<token\>) mitgeschickt.

Spring Security validiert das Token vor jeder Anfrage und prüft die
Berechtigungen anhand von Rollen.

Tokens sind zeitlich begrenzt, abgelaufene oder manipulierte Tokens
führen zu HTTP 401.

### Beispielcode

*// AuthController.java*

*\@PostMapping(\"/login\")*

*public ResponseEntity\<?\> login(@RequestBody LoginRequest req) {*

*User user = authService.verifyCredentials(req.username, req.password);*

*if (user != null) {*

*String token = jwtUtil.generateToken(user); // enthält User-ID +
Rollen*

*return ResponseEntity.ok(new JwtResponse(token));*

*}*

*return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();*

*}*

#### Token-Prüfung bei jedem Request

*// JwtFilter.java*

*\@Override*

*protected void doFilterInternal(HttpServletRequest request,
HttpServletResponse response, FilterChain chain) {*

*String header = request.getHeader(\"Authorization\"); // \"Bearer
\<token\>\"*

*if (header != null && header.startsWith(\"Bearer \")) {*

*String token = header.substring(7);*

*if (jwtUtil.validateToken(token)) {*

*UsernamePasswordAuthenticationToken auth =*

*new UsernamePasswordAuthenticationToken(userDetails, null, roles);*

*SecurityContextHolder.getContext().setAuthentication(auth);*

*}*

*}*

*chain.doFilter(request, response);*

*}*

*// SecurityConfig.java*

*\@Override*

*protected void configure(HttpSecurity http) throws Exception {*

*http.csrf().disable()*

*.authorizeRequests()*

*.antMatchers(\"/auth/\*\*\").permitAll()*

*.antMatchers(\"/appointments/\*\*\").hasRole(\"USER\")*

*.anyRequest().authenticated()*

*.and()*

*.addFilterBefore(jwtFilter,
UsernamePasswordAuthenticationFilter.class);*

*}*

1.  Secrets nicht im Code hinterlegen, sondern über Umgebungsvariablen
    verwalten.

2.  Kommunikation ausschließlich über HTTPS.

3.  Refresh-Tokens getrennt absichern.

## Fehler- und Eingabebehandlung

### Zweck

Robuste Eingabevalidierung, konsistente Fehlermeldungen und Schutz vor
Informationslecks.

### Kernkonzept

Eingaben werden doppelt geprüft: im Frontend (erste Validierung) und im
Backend (verbindliche Validierung).

Fehler werden serverseitig global abgefangen und in standardisierter
JSON-Struktur zurückgegeben.

Nutzer erhalten verständliche Fehlermeldungen, Entwickler erhalten Logs
mit Details.

### Beispielcode

*// AppointmentDTO.java*

*public class AppointmentDTO {*

*\@NotNull(message = \"Titel darf nicht leer sein\")*

*\@Size(min = 3, max = 100)*

*private String title;*

*\@FutureOrPresent(message = \"Startzeit darf nicht in der Vergangenheit
liegen\")*

*private LocalDateTime start;*

*\@Future(message = \"Endzeit muss nach der Startzeit liegen\")*

*private LocalDateTime end;*

*}*

#### Globale Fehlerbehandlung

*// GlobalExceptionHandler.java*

*\@ControllerAdvice*

*public class GlobalExceptionHandler {*

*\@ExceptionHandler(MethodArgumentNotValidException.class)*

*public ResponseEntity\<?\>
handleValidationErrors(MethodArgumentNotValidException ex) {*

*List\<String\> errors = ex.getBindingResult().getFieldErrors()*

*.stream()*

*.map(FieldError::getDefaultMessage)*

*.collect(Collectors.toList());*

*return ResponseEntity.badRequest().body(Map.of(\"error\", \"Validation
failed\", \"details\", errors));*

*}*

*\@ExceptionHandler(Exception.class)*

*public ResponseEntity\<?\> handleGeneralError(Exception ex) {*

*log.error(\"Unexpected error\", ex);*

*return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)*

*.body(Map.of(\"error\", \"Internal server error\"));*

*}*

*}*

*{*

*\"error\": \"Validation failed\",*

*\"details\": \[*

*\"Titel darf nicht leer sein\",*

*\"Endzeit muss nach der Startzeit liegen\"*

*\]*

}

*this.http.post(\'/api/appointments\', appointment).subscribe({*

*next: res =\> this.showSuccess(\"Termin gespeichert!\"),*

*error: err =\> this.showError(err.error.details.join(\", \"))*

*});*

### Best Practices

1.  Fehlermeldungen für Nutzer kurz und verständlich halten.

2.  Keine technischen Details an den Client weitergeben.

3.  Sensible Daten niemals ins Log schreiben.

4.  Einheitliche Fehler-Response-Objekte für alle API-Endpunkte.
