1.	Einleitung
Dieses Architekturdokument beschreibt die grundlegende Struktur und die wichtigsten Entwurfsentscheidungen der Terminkalender-Anwendung. Ziel ist es, allen Projektbeteiligten insbesondere Entwicklern, Betreibern und Entscheidern einen umfassenden Überblick über die Architektur, die Komponenten und deren Zusammenspiel zu geben.
Das Dokument richtet sich an Softwareentwickler, Systemarchitekten, Administratoren und alle, die an der Weiterentwicklung, dem Betrieb oder der Wartung des Systems beteiligt sind. Es erläutert die fachlichen und technischen Grundlagen, die den Aufbau und das Verhalten der Anwendung bestimmen.
Der Aufbau des Dokuments folgt bewährten Architekturstandards und gliedert sich in verschiedene Sichten: Nach einer Einleitung werden kurz die Außensicht, die Komponentensicht, die Betreibersicht, die Erstellungssicht, die physische Sicht und die Laufzeitsicht ausführlich behandelt. Tabellen und Diagramme unterstützen das Verständnis der Zusammenhänge. Die verwendeten Notationen orientieren sich an etablierten Standards wie UML und den Empfehlungen für Softwarearchitekturdokumentation.
Die Terminkalender-Anwendung ist ein webbasiertes System zur Verwaltung von Terminen, Benutzern und Kalenderansichten. Sie bietet flexible Frontend-Optionen (HTML/JavaScript oder Angular) mit einem einheitlichen Spring Boot REST-API Backend und ist für den Einsatz in unterschiedlichen Umgebungen konzipiert. Die Architektur legt besonderen Wert auf Wartbarkeit, Erweiterbarkeit und Sicherheit.

1.1	Qualitätsziele
Priorität	Qualitätsziel	Beschreibung	Metriken/Indikatoren
1	Sicherheit	Schutz von Termindaten und Benutzerkonten	JWT-Integrität, OWASP Top 10 Checks
2	Wartbarkeit	Saubere Schichtung & geringe Kopplung	SonarQube Issues, Zyklomatische Komplexität
3	Erweiterbarkeit	Neue Termin-Funktionen ohne Architekturbruch	LOC pro Feature, Anzahl modifizierter Module
4	Performance	Antwortzeit < 2s (95% P95)	API-Metriken, Actuator Traces
5	Testbarkeit	Hohe Abdeckung kritischer Logik	Line/Branch Coverage, Mutation Score
Tabelle 1 Qualitätsziele
1.2	Stakeholder
Rolle	Interesse	Relevante Aspekte
Endbenutzer	Termine verwalten	Usability, Performance
Administrator	Betrieb, Monitoring	Logs, Health, Backups
Entwicklerteam	Weiterentwicklung	Codequalität, Tests
Tabelle 2 Stakeholder
2.	Randbedingungen
2.1	Technische Randbedingungen
Aspekt	Details
Programmiersprache	Java 17 (LTS) – Nutzung moderner Sprachfeatures ohne Abhängigkeit auf preview
Framework	Spring Boot (Spring Web, Spring Security, Spring Data JPA) – vereinfacht Inversion of Control, Security-Konfiguration und Persistenz
Datenbanken	Azure MySQL Flexible Server (Standard/Produktion), H2 (lokale Entwicklung), Docker MySQL (optionale lokale Alternative)
Build-Tool	Maven (pom.xml) – Standard-Plugins, Erweiterbar mit Flyway, JaCoCo
Deployment	Lokaler Start via mvn spring-boot:run mit Azure MySQL, Docker als zusätzliche lokale Option verfügbar
Security-Secret	JWT-Secret aktuell hardcodiert in AuthService – muss produktiv externalisiert werden (ENV / Vault)
Tabelle 3 Technische Randbedingungen
Technisch wird die Anwendung mit flexiblen Frontend-Optionen (HTML/JavaScript/CSS oder Angular) und Spring Boot im Backend umgesetzt. Die Datenhaltung erfolgt relational in einer Azure MySQL Flexible Server Datenbank. Die Kommunikation zwischen Browser und Backend erfolgt über eine einheitliche REST-API mit HTTPS-Verschlüsselung. Das Backend stellt vollständige REST‑Endpunkte bereit und bleibt zustandslos (JWT). Der Betrieb erfolgt standardmäßig als lokale Java-Anwendung mit Cloud-Datenbank, optional ist auch ein containerisierter Betrieb mit Docker möglich.
2.2	Organisatorische Randbedingungen
Organisatorisch wurde in einem kleinen Team, bestehend aus 5 Personen gearbeitet; Quellcodeverwaltung liefen auf einer zentralen Git‑Plattform. Die Hauptsächliche Kommunikation untereinander und die Verwaltung der Dokumentationen lief über einen Discord Server, Welcher zu Beginn des Projekts erstellt wurde. Kurzfristige Abstimmungen und weiterer Austausch lief über eine WhatsApp Gruppe. Des Weiteren wurde anfangs ein Projektplan erstellt, der grobe Vorgaben/ Ziele enthalten hat.
3.	Außensicht
Die Außensicht beschreibt das System aus der Perspektive der Nutzer und der Nachbarsysteme. Im Zentrum steht der Prozess der Terminverwaltung, der die Erstellung, Bearbeitung, Verschiebung, Löschung und Suche von Terminen umfasst. Diese Prozesse sind in der Spezifikation in Kap. 2.1 ausführlich beschrieben und bilden die Grundlage für die Systementwicklung. Das Datenmodell besteht aus den Entitäten Benutzer und Termin, ergänzt durch die Enumeration Priorität. Kategorien sind derzeit als String-Attribut in der Termin-Entität implementiert (vereinfachte Lösung für Phase 1). Die Struktur ist in der Spezifikation (Kap. 3) dokumentiert.
Die Funktionen des Systems sind in Form von Use Cases (UC1–UC9) festgelegt und umfassen unter anderem die Registrierung und Anmeldung von Benutzern, die Verwaltung von Terminen sowie die Verwaltung von Kategorien. Diese Use Cases sind in Kap. 2.4 der Spezifikation aufgeführt und durch ein Use-Case-Diagramm visualisiert. Die Benutzeroberfläche, einschließlich der Dialoglandkarte, Mockups und Navigationsmöglichkeiten, ist ebenfalls Bestandteil der Spezifikation (Kap. 4). Nachbarsysteme spielen nur eine untergeordnete Rolle: Hauptsächlich handelt es sich um optionale E-Mail-Dienste, die für Funktionen wie den Passwort-Reset genutzt werden können (Spezifikation, Kap. 5). Im später folgenden Kapitel (4.1.1 Kontextabgrenzung) folgen ausführliche Beschreibungen des fachlichen- und technischen Kontexts.
3.1	Batch-Benutzerschnittstellen
Für das System sind derzeit keine Batch-Benutzerschnittstellen vorgesehen. Alle Interaktionen erfolgen über die Weboberfläche oder automatisiert über Rest-APIs. Typische Batch-Funktionen wie periodische Import-/ Export-Jobs, Massenverarbeitung oder nächtliche Batch-Läufe sind im aktuellen Projektkontext nicht vorgesehen und daher nicht Bestandteil der Architektur.
3.2	Nachbarsysteme
Nachbarsysteme spielen in der aktuellen Ausbaustufe nur eine untergeordnete Rolle. Optional ist ein externer E‑Mail‑Dienst für den Passwort‑Reset vorgesehen. Weitere Integrationen (z. B. Push‑Benachrichtigungen) sind bewusst nicht Teil dieser Ausarbeitung und werden in der Spezifikation lediglich als Perspektive erwähnt.
4.	Innensicht
Die Innensicht beschreibt die technische Gliederung und Umsetzung des Systems. Die Anwendung folgt einer klassischen Drei-Schichten-Architektur. In der Präsentationsschicht stehen zwei gleichwertige Frontend-Optionen zur Verfügung:

**Option A**: HTML/JavaScript/CSS-basierte Webanwendung (produktiv), bereitgestellt als statische Ressourcen vom Spring Boot Server (src/main/resources/static/)
**Option B**: Angular Single-Page-Application (vorbereitet), läuft als separate Anwendung auf Port 4200

Beide Frontend-Varianten bilden die Schnittstelle zum Benutzer, verarbeiten Eingaben und kommunizieren ausschließlich über die einheitliche REST-API mit dem Backend. Das Backend basiert auf Spring Boot und enthält Controller (AuthController, AppointmentController, CalendarController), die HTTP-Anfragen entgegennehmen, Services (AuthService, AppointmentService, HolidayService), welche die Geschäftslogik kapseln, sowie Sicherheits- und Fehlerbehandlungskomponenten. Für die Datenhaltung kommt Spring Data JPA in Kombination mit einer MySQL-Datenbank zum Einsatz.
Der Betrieb der Anwendung erfolgt standardmäßig über lokalen Start mit Cloud-Datenbank: 
1. **Standard-Betrieb**: `mvn spring-boot:run` mit direkter Azure MySQL Flexible Server Verbindung (`dbotk25.mysql.database.azure.com`)
2. **Alternative Optionen**: 
   - Docker Compose mit lokaler MySQL (für isolierte Entwicklungsumgebung)
   - H2-Datenbank für lokale Entwicklung ohne Netzwerkabhängigkeit

Die Anwendung ist primär für den Cloud-nativen Betrieb mit Azure MySQL konzipiert, während Docker als zusätzliche Deployment-Option verfügbar ist.
Die Erstellungssicht beschreibt die technische Basis der Entwicklung. Das Backend wird in Java mit Spring Boot entwickelt und mit Maven gebaut. Das Frontend besteht aus statischen HTML/JavaScript/CSS-Dateien, die direkt in den Spring Boot Resources eingebunden sind (src/main/resources/static/). So wird eine gleichbleibende Qualität der Software sichergestellt.
In der physischen Sicht zeigt sich das System als eine Zusammensetzung aus einem Webbrowser, der als Client dient, einem Spring Boot Backend, das in einem Container ausgeführt wird, und einer MySQL-Datenbank, die in einem separaten Container betrieben wird. Diese einfache, aber robuste Systemlandschaft kann bei Bedarf durch einen Load Balancer oder durch Container-Orchestrierung mit Kubernetes ergänzt werden, um Skalierbarkeit und Ausfallsicherheit zu erhöhen.
Die Laufzeitsicht lässt sich am besten anhand von Sequenzdiagrammen erläutern. In der Spezifikation sind exemplarisch die Use Cases Anmeldung (UC2), Termin-Erstellung (UC3) und Passwort zurücksetzen (UC7) dokumentiert. Sie verdeutlichen, wie Benutzeraktionen durch das Frontend an das Backend übermittelt werden, dort validiert und weiterverarbeitet werden und schließlich in der Persistenz Schicht gespeichert oder verändert werden. Erfolgs- und Fehlerpfade werden ebenfalls berücksichtigt, sodass der Ablauf für die Entwickler und Architekten nachvollziehbar bleibt.
4.1	Komponentensicht
4.1.1	Kontextabgrenzung
4.1.1.1	Fachlicher Kontext
 
Abbildung 1 Fachlicher Kontext
Fachlich betrachtet interagieren Benutzer über den Web‑Browser mit einer Kalenderoberfläche, um Termine anzulegen, zu ändern, zu verschieben, zu suchen und zu löschen. Persistente Daten, Benutzer, Termine und Kategorien werden im Backend validiert und in der Datenbank gespeichert. Technisch betrachtet ist der Browser der einzige externe Client. Er kommuniziert über HTTPS mit dem Spring‑Boot‑Dienst. Das Backend kapselt sämtliche Zugriffe auf MySQL. Direkte Datenbankverbindungen von außen sind ausgeschlossen. Die Grenze des Systems verläuft damit entlang der REST‑Schnittstelle des Backends: alles dahinter (Geschäftslogik, Persistenz, Security) ist Teil des Systems, alles davor (Client‑Geräte, Netzwerkinfrastruktur) gehört zum Umfeld.
4.1.1.2	Technischer Kontext
 
Abbildung 2 Technischer Kontext
Das technische Kontextdiagramm zeigt die Einbettung des Terminkalenders in seine Umgebung. Der Nutzer greift über ein Endgerät mit einem Webbrowser auf das System zu. Die Kommunikation erfolgt dabei über das Internet mittels HTTPS.
Innerhalb des Systems stehen zwei Frontend-Optionen zur Verfügung: eine HTML/JavaScript-Lösung (statische Ressourcen vom Spring Boot Server) und eine Angular SPA (separate Anwendung). Beide sind für die Darstellung der Benutzeroberfläche und die Interaktion mit dem Anwender verantwortlich, verarbeiten Benutzereingaben, führen erste Validierungen durch und kommunizieren ausschließlich über die einheitliche REST-API mit dem Backend.
Das Spring-Boot-Backend stellt die Geschäftslogik, die Authentifizierungs- und Autorisierungsmechanismen sowie die Schnittstellen für die Termin- und Benutzerverwaltung bereit. Es ist zudem für die zentrale Fehlerbehandlung und die Sicherheit (z. B. JWT-Token, Rollenprüfung, Passwort-Hashing) zuständig.
Alle Daten wie Benutzerdaten, Termine und Kategorien werden in einer Azure MySQL Flexible Server Datenbank persistiert. Das Backend greift über JPA/Hibernate und JDBC über das Internet auf die Cloud-Datenbank zu.
Durch diese klare Trennung in Browser-Frontend, Backend-Services und Datenbank entsteht eine übersichtliche Systemarchitektur, die sich leicht erweitern, warten und skalieren lässt.
4.1.1.3	Lösungsstrategie
Die Lösungsstrategie folgt der Trennung der Zuständigkeiten mit flexiblen Frontend-Optionen. Sowohl das HTML/JavaScript‑Frontend als auch die Angular-SPA übernehmen Darstellung und Interaktion, das Spring‑Boot‑Backend bündelt Geschäftslogik und Sicherheitsmechanismen über eine einheitliche REST-API und die relationale Datenbank gewährleistet Konsistenz und Transaktionen. Sicherheit wird durch ein stateless Authentifizierungsverfahren mit JWT und rollenbasierter Autorisierung umgesetzt; Passwörter werden mit BCrypt gehasht. Datenqualität wird serverseitig über DTO‑Validierungen und Geschäftsregeln (z. B. „Endzeit muss nach Startzeit liegen“) sichergestellt. Die Benutzeroberfläche ist bewusst reduziert gestaltet. Häufige Aktionen (Termin erstellen/ändern) sind ohne Kontextwechsel erreichbar, Such‑ und Filterfunktionen sind in der Kalenderansicht integriert. Die Strategie begünstigt horizontale Skalierung und reduziert Kopplung zwischen Client und Server.
4.1.2	Bausteinsicht
 
Abbildung 3 Bausteinsicht
4.1.2.1	Verfeinerungsebene 0
Auf der obersten Ebene wird das Gesamtsystem als Terminkalender-System betrachtet. Es umfasst alle Komponenten, die zur Bereitstellung der Anwendung notwendig sind: die Benutzerschnittstelle im Browser, die fachliche Logik im Backend sowie die persistente Speicherung in der Datenbank.
4.1.2.2	Verfeinerungsebene 1
In der nächsten Verfeinerungsebene zeigt sich eine klare Aufteilung in drei Hauptbausteine. Das Frontend stellt die Präsentationsschicht dar und ist in zwei Varianten verfügbar: als HTML/JavaScript/CSS-basierte Webanwendung (produktiv) oder als Angular SPA (vorbereitet). Beide dienen der Interaktion mit dem Benutzer, übernehmen clientseitige Validierung von Eingaben und kommunizieren ausschließlich über REST-Schnittstellen mit dem Backend. Das Backend basiert auf Spring Boot und implementiert die fachliche Logik der Anwendung. Es kümmert sich um Authentifizierung und Autorisierung, verwaltet Benutzerkonten und Termine, verarbeitet Suchanfragen und regelt den Zugriff auf die Cloud-Datenbank. Die Azure MySQL Flexible Server Datenbank dient der dauerhaften Speicherung sämtlicher Informationen wie Benutzer, Termine, Kategorien und Prioritäten und ist über das Internet erreichbar.
4.1.2.3	Verfeinerungsebene 2 (Frontend)
Das System bietet zwei gleichwertige Frontend-Implementierungen, die beide auf dieselbe REST-API zugreifen:

**Frontend-Option A: HTML/JavaScript/CSS (Produktiv)**
- **HTML-Seiten**: Statische Ressourcen in `src/main/resources/static/`:
  - `index.html`: Hauptkalender-Anwendung mit Termin-, Wochen- und Monatsansicht
  - `login.html`: Authentifizierung und Benutzerverwaltung  
  - `password-reset.html`: Passwort-Reset-Funktionalität
  - Debug-Interfaces: `debug-interface.html`, `appointment-test.html`
- **JavaScript-Logik**: REST-API Kommunikation, JWT-Management, DOM-Manipulation
- **Zustandsverwaltung**: localStorage für Tokens und Präferenzen

**Frontend-Option B: Angular SPA (Vorbereitet)**
- **Komponenten-Struktur**: Login, Dashboard, Appointments, Dialoge
- **Services**: AuthService, AppointmentService mit HTTP-Client Integration
- **Routing**: Angular Router für Navigation zwischen Ansichten
- **State Management**: Signal-basierte Zustandsverwaltung (Angular 17+)
- **UI Framework**: Angular Material für moderne Benutzeroberfläche

**Gemeinsame REST-API Integration**:
Beide Frontend-Optionen nutzen identische API-Endpunkte (`/api/v1/*`) für:
- Authentifizierung (`/auth/*`)
- Terminverwaltung (`/appointments/*`) 
- Kalenderfunktionen (`/calendar/*`)
- Benutzer- und Systemdaten

Diese flexible Architektur ermöglicht die Wahl zwischen einfacher HTML-Lösung oder moderner SPA-Technologie, je nach Projektanforderungen.
4.1.2.4	Verfeinerungsebene 2 (Backend)
Das Backend wird in einer weiteren Ebene detailliert betrachtet. Auf der Controller-Schicht werden die Anfragen des Frontends entgegengenommen. Hier befinden sich der AuthController für Registrierung und Login, der AppointmentController für die Verwaltung von Terminen sowie der CalendarController für Kalenderansichten. Kategorien werden derzeit als String-Attribut verwaltet (kein separater CategoryController in Phase 1). Die Controller prüfen Anfragen und leiten sie an die entsprechende Serviceschicht weiter.
Die Service-Schicht enthält die eigentliche Geschäftslogik. Hier werden Regeln wie die Kollisionsprüfung bei Terminen, die Verwaltung von Benutzerinformationen oder die Kalenderberechnung umgesetzt. Implementierte Services sind AuthService, AppointmentService, HolidayService und CalendarService. Kategorien werden derzeit direkt im AppointmentService verwaltet.
Für den Zugriff auf die Datenhaltung sorgt die Repository-Schicht, die mittels Spring Data JPA die Verbindung zur MySQL-Datenbank herstellt. Implementierte Repositories sind UserRepository, AppointmentRepository und HolidayRepository. Sie kapseln die Datenzugriffe und stellen der Serviceschicht eine klare Schnittstelle bereit.
Die Azure MySQL Flexible Server Datenbank bildet die Grundlage für die dauerhafte Speicherung. Sie hält alle relevanten Datenbestände wie Benutzerinformationen, Termindaten und Kategorien vor und gewährleistet deren konsistente Verwaltung. Als Cloud-Service bietet sie hohe Verfügbarkeit und automatische Backups.
Durch diese hierarchische Struktur – vom Gesamtsystem über die Hauptbausteine bis hin zur detaillierten Whitebox-Sicht des Backends – wird eine klare Trennung der Verantwortlichkeiten erreicht. Dies erleichtert nicht nur die Verständlichkeit der Architektur, sondern sorgt auch für Wartbarkeit, Erweiterbarkeit und eine saubere Umsetzung der definierten Qualitätsziele.

4.2	Betreibersicht
Die Betreibersicht beschreibt alle Aspekte rund um Installation, Betrieb, Wartung, Notfallmaßnahmen und Kompatibilität der Terminkalender-Anwendung. Sie richtet sich an Administratoren, Betreiber und Support-Teams und stellt sicher, dass das System zuverlässig, sicher und effizient betrieben werden kann.
4.2.1	Übersicht der wichtigsten Betriebsaufgaben
Aufgabe	Beschreibung	Verantwortlich
Installation	JAR bauen, DB anlegen, Konfiguration, Start	Administrator
Deinstallation	Anwendung stoppen, Dateien & DB entfernen	Administrator

Backup	Täglicher Dump, Sicherung der Konfiguration	Admin/Operator
Restore	Backup einspielen, Anwendung neu starten	Admin/Operator
Monitoring	Systemressourcen & Health-Checks überwachen	Operator

Update	Neue Version bauen, einspielen, testen	Administrator
Notfallmaßnahmen
	Neustart, Fehleranalyse 	Operator
Tabelle 4 Betriebsaufgaben
4.2.2	Systemvoraussetzungen und Installation
Für den Betrieb der Anwendung werden Java 17 oder höher, Maven ab Version 3.9 und eine Internetverbindung zur Azure MySQL Datenbank benötigt. Unterstützt werden Windows, Linux und MacOS. Optional können Docker (für lokale MySQL), Nginx als Reverse Proxy und ein SMTP-Server für E-Mail-Benachrichtigungen eingesetzt werden.
Die Installation erfolgt in mehreren Schritten: Nach dem Klonen des Quellcodes oder dem Bezug eines Release-Pakets wird das Projekt mit Maven gebaut, wodurch ein ausführbares JAR-File entsteht. Die Konfiguration der Datenbank erfolgt über die Datei application-prod.properties oder application.yml. Die Anwendung kann dann entweder direkt als JAR mit dem Befehl `java -jar ...` oder – insbesondere während der Entwicklung – mit `mvn spring-boot:run` gestartet werden. Alternativ ist auch ein Betrieb in Docker-Containern möglich. Für den Produktivbetrieb empfiehlt sich die Nutzung eines Reverse Proxys wie Nginx und die Integration in Systemdienste für einen automatischen Neustart nach Systemausfällen.
4.2.3	Betrieb und Systemstart
Das Starten und Anhalten des Systems ist sowohl für Entwicklung als auch Produktion klar geregelt. Während der Entwicklung wird meist `mvn spring-boot:run` verwendet, was schnelles Testen und Hot-Reload ermöglicht. Im Produktivbetrieb wird das gebaute JAR mit `java -jar ...` gestartet, was Stabilität und Portabilität gewährleistet. Das Anhalten erfolgt durch das Beenden des Prozesses im Terminal, per Systemdienst oder – bei Containern – mit `docker-compose down`.
4.2.4	Mehrfachinstallation und Koexistenz
Die Anwendung kann mehrfach auf einem Rechner installiert und betrieben werden, sofern unterschiedliche Ports und Datenbanknamen verwendet werden. Dies ermöglicht den parallelen Betrieb mehrerer, unterschiedlich parametrierter Instanzen, etwa für Test- und Produktivumgebungen. Auch der parallele Betrieb mit anderen Java-Anwendungen oder Webservern ist problemlos möglich, solange Port-Konflikte vermieden werden. MySQL kann gemeinsam mit anderen Anwendungen genutzt werden, sofern unterschiedliche Datenbanken verwendet werden. Bei parallelem Betrieb mit anderen Webservern (z.B. Apache, Tomcat) ist auf unterschiedliche Ports und ggf. eine Reverse Proxy-Konfiguration zu achten.
4.2.5	Betriebsressource
Für den stabilen Betrieb sind ausreichend Festplattenspeicher für Datenbank, Backups und Logdateien sowie genügend Arbeitsspeicher und CPU-Ressourcen erforderlich. Für E-Mail-Benachrichtigungen wird ein SMTP-Server benötigt.
4.2.6	Backup, Recovery und Notfallmaßnahmen
Regelmäßige Backups der MySQL-Datenbank werden empfohlen, etwa durch tägliche Dumps mit `mysqldump`. Auch die Konfigurationsdateien sollten regelmäßig gesichert werden. Im Notfall kann das letzte Backup mit `mysql` oder einem Datenbank-Tool wieder eingespielt werden. Die Integrität der Backups wird regelmäßig überprüft und Test-Restores werden durchgeführt, um die Wiederherstellbarkeit sicherzustellen.
Im Falle von Zwischenfällen sind verschiedene Maßnahmen vorgesehen: Fällt ein App-Server aus, übernimmt der Load Balancer das Routing auf verbleibende Instanzen. Bei Ausfall der Datenbank empfiehlt sich ein Failover-Cluster oder ein Backup-Server. Redundante Netzwerkverbindungen und Monitoring sorgen für zusätzliche Ausfallsicherheit. Bei Ausfall einzelner Software-Komponenten werden diese gezielt neu gestartet, wobei Logs und Monitoring-Daten zur Fehleranalyse herangezogen werden.
4.2.7	Monitoring und Updates
Das Monitoring der Anwendung erfolgt über Spring Boot Actuator, der Health Checks und Metriken bereitstellt. Systemressourcen wie CPU, RAM und Festplatte werden überwacht, und bei Grenzwertüberschreitungen werden Alarme ausgelöst. Das Logging erfolgt zentral mit Logback, wobei alle Anfragen, Fehler und Systemereignisse protokolliert und bei Bedarf an zentrale Systeme wie den ELK-Stack weitergeleitet werden. Regelmäßige Updates der Abhängigkeiten, die Überwachung auf Sicherheitslücken (z.B. mit Dependabot) und eine konsequente Zugriffskontrolle sorgen für einen sicheren Betrieb.
4.3	Erstellungssicht
Die Erstellungssicht beschreibt die Struktur und Organisation der Softwareartefakte, aus denen das Laufzeitsystem der Terminkalender-Anwendung gebaut wird. Sie gibt einen Überblick über alle technischen Einheiten wie Quellcode, Bibliotheken, Skripte, Build-Files und ausführbare Programme. Zudem wird die Verteilung dieser Artefakte auf die Zielsysteme erläutert.
4.3.1	Programmquellen
Die Anwendung basiert auf Java und verwendet das Spring Boot Framework. Der gesamte Quellcode befindet sich im GitHub Repository und ist in verschiedene Pakete unterteilt:
•	controller: REST-API-Endpunkte
•	service: Geschäftslogik
•	repository: Datenzugriff (JPA)
•	entity: Datenmodelle
•	dto: Data Transfer Objects
•	config: Konfigurationen und Security
4.3.2	Statische und dynamische Bibliotheken
Die Anwendung nutzt zahlreiche externe Bibliotheken, die über Maven im `pom.xml` verwaltet werden. Dazu zählen u.a.:
•	Spring Boot Starter (Web, Data JPA, Security)
•	MySQL Connector
•	JWT (JSON Web Token)
•	Lombok
•	Logback
•	JUnit, Mockito (Test)
Diese Bibliotheken werden beim Build automatisch eingebunden und als Teil des ausführbaren JARs ausgeliefert. Sie definiert alle Abhängigkeiten, Plugins und Build-Prozesse. Der Build erzeugt ein ausführbares JAR im Verzeichnis `target/`.
4.4	Physische Sicht
4.4.1	Verteilung auf Zielsysteme
Die Softwareartefakte werden wie folgt verteilt:
- App-Server: Enthält das JAR-File, Konfigurationsdateien und ggf. Skripte
- Datenbank: Azure MySQL Flexible Server (Cloud-Service, keine lokale Installation erforderlich)
- Monitoring-Server: (Optional) Monitoring-Tools und Konfigurationen
- Docker-Alternative: (Optional) Lokale MySQL + App Container für isolierte Entwicklung
Die Verteilung kann lokal, auf mehreren Servern oder in der Cloud erfolgen. Die Erstellungssicht stellt sicher, dass alle notwendigen Artefakte für einen erfolgreichen Build und Betrieb bereitgestellt werden.
4.4.2	Genutzte physische Geräte
Die physische Sicht beschreibt die reale Hardware- und Netzwerkstruktur, auf der die Terminkalender-Anwendung betrieben wird. Sie konzentriert sich auf die genutzten Geräte und deren Zusammenspiel, ohne dabei bereits in die Details der Softwareverteilung oder Prozesssteuerung zu gehen.
Das System nutzt folgende physische Ressourcen:
•	Server/Computer: Für die Ausführung der Spring Boot Anwendung (App-Server mit integriertem Frontend). Die Datenbank läuft als Azure MySQL Flexible Server in der Microsoft Cloud. Der Anwendungsserver kann als physische oder virtuelle Maschine bereitgestellt werden.
•	Netzwerkverbindungen: Internetverbindung (Ethernet, WLAN) zur Kommunikation mit der Azure MySQL Datenbank. Eine stabile Internetverbindung ist für die Funktionsfähigkeit der Anwendung kritisch, da die Datenhaltung vollständig in der Cloud erfolgt.
•	Bildschirme: Für Administratoren und Nutzer, die über Webbrowser auf die HTML-basierte Oberfläche zugreifen. Die Anwendung selbst benötigt keine dedizierten Bildschirme auf dem Server.
•	Drucker: Werden nur benötigt, wenn Ausdrucke von Terminen oder Listen erfolgen sollen. Die Anwendung kann mit beliebigen, im Netzwerk verfügbaren Druckern zusammenarbeiten.
4.4.3	Ausführbare Programme
Das Hauptartefakt ist das JAR-File, z.B. `terminkalender-0.0.1-SNAPSHOT.jar`, das auf jedem Zielsystem mit Java 17+ ausgeführt werden kann. Für den Produktivbetrieb kann das JAR als Systemdienst oder in einem Docker-Container betrieben werden.
4.5	Laufzeitsicht
Zur Laufzeit zeigt sich das System als Abfolge klarer Schritte zwischen Browser, Backend und Datenbank. Wenn ein Benutzer sich anmeldet, sendet das Frontend die Anmeldedaten an einen REST-Endpunkt des Backends. Dort werden die Daten validiert und die Zugangsdaten gegen die gespeicherten Passwort-Hashes geprüft. Bei Erfolg erzeugt das Backend ein kurzlebiges JWT und gibt es an den Browser zurück. Anschließende Anfragen führen das Token im Authorization-Header mit; das Backend verifiziert das Token, ermittelt daraus Identität und Rollen und führt die gewünschte Operation aus.
Bei der Erstellung eines Termins löst der Nutzer die Aktion in der Kalenderoberfläche aus. Das Frontend überträgt die Termindaten als JSON. Der zuständige Controller validiert die Nutzlast, die Domänenschicht prüft Geschäftsregeln (u. a. die zeitliche Konsistenz) und schreibt den Datensatz über JPA in die relationale Datenbank. Die Antwort enthält den gespeicherten Termin; das Frontend aktualisiert daraufhin die Ansicht ohne Seiten-Reload. Fehlerfälle – z. B. ungültige Zeitintervalle oder fehlende Pflichtfelder – werden zentral abgefangen und als konsistente JSON-Fehlermeldungen zurückgegeben.
Der Passwort-Reset beginnt mit der Anforderung eines Reset-Links. Das Backend erzeugt ein Einmal-Token, persistiert dessen Gültigkeit und adressiert den Versand an den konfigurierten E-Mail-Dienst. Über den Link setzt der Benutzer ein neues Passwort, das nach Validierung gehasht in der Datenbank gespeichert wird. Timeouts, Wiederholungen bei transienten Fehlern und strukturierte Protokollierung sorgen dafür, dass die Abläufe robust bleiben und im Betrieb nachvollzogen werden können.
4.5.1	Beispielhafter Ablauf: Termin-Erstellung
 
Abbildung 4 Ablauf Terminerstellung
Das Zusammenspiel der Komponenten ist klar definiert: Der Web-Client sendet HTTP-Anfragen an die Controller des Backends. Diese validieren die Daten und delegieren die Geschäftslogik an die Service-Klassen. Die Services greifen über die Repositories auf die Datenbank zu. Sicherheits- und Fehlerbehandlungskomponenten sind als Querschnittsfunktionen integriert und sorgen für einen robusten und sicheren Ablauf. Beispielhaft sei die Termin-Erstellung genannt: Der Benutzer sendet eine Anfrage, der Controller prüft und leitet weiter, der Service prüft auf Kollisionen und speichert den Termin, die Antwort wird an den Client zurückgegeben. Ebenso läuft die Authentifizierung ab: Die Zugangsdaten werden geprüft, ein JWT-Token wird erstellt und für weitere Anfragen genutzt.
Die Architektur ist so gestaltet, dass sie leicht erweiterbar ist. Neue Komponenten wie Benachrichtigungsdienste, externe Kalenderintegration oder zusätzliche Entitäten können problemlos ergänzt werden. Die klare Trennung der Schichten und die Nutzung etablierter Frameworks sorgen für Wartbarkeit, Testbarkeit und Skalierbarkeit der Anwendung. Die Komponentensicht bildet damit das stabile Fundament für die gesamte Systemarchitektur.
 

5.	Übergreifende Aspekte
5.1	Qualitätsmerkmale
Die Qualität der Terminkalender-Anwendung wird durch verschiedene Merkmale geprägt, die sowohl technische als auch nutzerorientierte Anforderungen abdecken. Dieses Kapitel betrifft die ursprünglichen Qualitätsziele welche unter den nichtfunktionalen Anforderungen aus der Spezifikation (Kapitel 2.3) zu finden sind.
5.1.1	Benutzerfreundlichkeit und Wartbarkeit
Ein zentrales Qualitätsziel des Systems ist die Benutzerfreundlichkeit. Durch die strukturierte HTML/JavaScript-basierte Oberfläche können Nutzer intuitiv zwischen verschiedenen Kalenderansichten navigieren und Termine effizient verwalten. Die direkte Integration mit dem Backend über REST-APIs ermöglicht schnelle Reaktionen und eine flüssige Nutzungserfahrung. So entsteht eine einfache und leicht verständliche Benutzeroberfläche.
Ein weiteres wichtiges Ziel ist die Wartbarkeit. Die klare Schichtenarchitektur im Backend trennt Controller, Services und Repositories, sodass Änderungen in einer Schicht keine Auswirkungen auf andere haben. Auch die modulare Struktur im Frontend unterstützt Erweiterungen und Anpassungen. Auf diese Weise bleibt das System gut pflegbar, testbar und flexibel erweiterbar.
5.1.2	Zuverlässigkeit
Die Zuverlässigkeit und Konsistenz des Systems werden durch einfache Zustandsverwaltung über localStorage und sessionStorage unterstützt. JWT-Tokens und Benutzerpräferenzen werden clientseitig verwaltet, während die eigentliche Datenkonsistenz durch das Backend und die Datenbank gewährleistet wird.
5.1.3	Leistungsfähigkeit und Effizienz
Schließlich spielen die Performance und Skalierbarkeit eine wichtige Rolle. Durch die stateless Architektur des Spring-Boot-Backends können mehrere Instanzen parallel betrieben und über einen Load Balancer verteilt werden. Die Auslagerung der Datenhaltung in eine MySQL-Datenbank sorgt für eine stabile Grundlage bei wachsendem Datenvolumen, während das schlanke HTML/JavaScript-Frontend dem Nutzer eine reaktive und performante Bedienung ermöglicht.
5.1.4	Sicherheit
Ein wesentliches Qualitätsziel ist außerdem die Sicherheit. Im Backend ist dies insbesondere in der Service- und Controller-Schicht verankert, wo Authentifizierungs- und Autorisierungsmechanismen (z. B. JWT) umgesetzt werden. So wird gewährleistet, dass nur berechtigte Nutzer Zugriff auf sensible Funktionen und Daten erhalten.
6.	Architekturentscheidungen
6.1	Einleitung
Bei der Wahl der Technologien wurden Stabilität und langfristige Wartbarkeit berücksichtigt. Für das Backend fiel die Wahl auf Java mit Spring Boot, da es ein stabiles Framework mit einem großen Ökosystem ist. 

Für das Frontend wurden bewusst **zwei gleichwertige Optionen** entwickelt:
- **HTML/JavaScript/CSS**: Einfachheit, geringe Komplexität, keine Framework-Abhängigkeiten, direkt vom Spring Boot Server bereitgestellt
- **Angular SPA**: Moderne TypeScript-basierte Lösung mit komponentenbasierter Architektur, Signal-basiertem State Management und Angular Material UI

Beide Frontend-Varianten nutzen dieselbe REST-API, wodurch maximale Flexibilität bei der Technologiewahl gewährleistet wird. Die Daten werden in einer relationalen Azure MySQL-Datenbank gespeichert, die sowohl robust als auch weit verbreitet ist. Für die Authentifizierung wurde JWT gewählt, da dieses Verfahren einfach, stateless und leicht skalierbar ist. Die Schnittstellen sind als REST-Endpunkte realisiert, da sie leicht verständlich und mit einer Vielzahl von Clients kompatibel sind.
6.2	Authentifizierung und Sicherheit
Die Anwendung setzt auf JWT (JSON Web Tokens) in Kombination mit Spring Security. Diese Entscheidung ermöglicht eine stateless Architektur, die sich leicht horizontal skalieren lässt. Jeder Request trägt die notwendigen Authentifizierungsinformationen selbst im Token, ohne dass der Server Session-Daten speichern muss.
 
6.2.1	Umsetzungsbeispiele (A&S)
6.2.1.1	Security-Konfiguration
Der Einstiegspunkt für die Sicherheitslogik ist eine Konfigurationsklasse. Hier wird festgelegt, welche Endpunkte frei erreichbar sind (z. B. /auth/login), und dass alle anderen Anfragen authentifiziert sein müssen
