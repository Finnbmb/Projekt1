# Anforderungsspezifikation - Terminkalender-Anwendung

## 1. Einführung

### 1.1 Zweck
Dieses Dokument definiert die funktionalen und nicht-funktionalen Anforderungen für die Terminkalender-Anwendung im Rahmen des Software-Technik Projekts 1.

### 1.2 Geltungsbereich
Die Spezifikation umfasst alle Aspekte der Terminkalender-Anwendung von der Benutzeroberfläche bis zur Datenpersistierung.

### 1.3 Definitionen
- **Termin:** Ein geplantes Ereignis mit Datum, Uhrzeit und Beschreibung
- **Benutzer:** Person, die die Kalender-Anwendung verwendet
- **REST API:** Programmierschnittstelle für externe Systeme

## 2. Gesamtbeschreibung

### 2.1 Produktperspektive
Die Terminkalender-Anwendung ist eine eigenständige Webanwendung, die es Benutzern ermöglicht, Termine zu verwalten.

### 2.2 Produktfunktionen
- Termine erstellen, bearbeiten, löschen
- Termine nach Datum filtern
- Kalenderansicht (Monats-/Wochenansicht)
- Terminerinnerungen
- Export von Terminen

### 2.3 Benutzerklassen
- **Endbenutzer:** Personen, die ihre Termine verwalten möchten
- **Systemadministrator:** Wartung und Konfiguration der Anwendung

## 3. Funktionale Anforderungen

### 3.1 Terminverwaltung

#### FR-001: Termin erstellen
**Beschreibung:** Benutzer kann einen neuen Termin erstellen  
**Eingabe:** Titel, Beschreibung, Startdatum/-zeit, Enddatum/-zeit, Ort (optional)  
**Ausgabe:** Bestätigung der Terminerstellung  
**Vorbedingung:** Benutzer ist authentifiziert  
**Nachbedingung:** Termin ist im System gespeichert  

#### FR-002: Termin anzeigen
**Beschreibung:** Benutzer kann Termine anzeigen lassen  
**Eingabe:** Zeitraum (optional)  
**Ausgabe:** Liste aller Termine im angegebenen Zeitraum  
**Vorbedingung:** Keine  
**Nachbedingung:** Termine werden angezeigt  

#### FR-003: Termin bearbeiten
**Beschreibung:** Benutzer kann bestehende Termine ändern  
**Eingabe:** Termin-ID und geänderte Daten  
**Ausgabe:** Bestätigung der Änderung  
**Vorbedingung:** Termin existiert  
**Nachbedingung:** Geänderte Daten sind gespeichert  

#### FR-004: Termin löschen
**Beschreibung:** Benutzer kann Termine entfernen  
**Eingabe:** Termin-ID  
**Ausgabe:** Bestätigung der Löschung  
**Vorbedingung:** Termin existiert  
**Nachbedingung:** Termin ist aus dem System entfernt  

### 3.2 Kalenderansicht

#### FR-005: Monatsansicht
**Beschreibung:** Anzeige von Terminen in Monatsübersicht  
**Eingabe:** Monat und Jahr  
**Ausgabe:** Kalender mit eingetragenen Terminen  

#### FR-006: Wochenansicht
**Beschreibung:** Detaillierte Wochenansicht  
**Eingabe:** Woche (Start-Datum)  
**Ausgabe:** Wochenkalender mit Terminen  

### 3.3 Suche und Filter

#### FR-007: Termine filtern
**Beschreibung:** Filterung von Terminen nach Kriterien  
**Eingabe:** Suchkriterien (Datum, Titel, Beschreibung)  
**Ausgabe:** Gefilterte Terminliste  

## 4. Nicht-funktionale Anforderungen

### 4.1 Leistung
- **NFR-001:** Antwortzeit < 2 Sekunden für alle API-Calls
- **NFR-002:** Unterstützung von bis zu 100 gleichzeitigen Benutzern

### 4.2 Zuverlässigkeit
- **NFR-003:** Verfügbarkeit von 99% während der Geschäftszeiten
- **NFR-004:** Automatische Datensicherung alle 24 Stunden

### 4.3 Benutzerfreundlichkeit
- **NFR-005:** Intuitive Benutzeroberfläche
- **NFR-006:** Responsive Design für mobile Geräte

### 4.4 Sicherheit
- **NFR-007:** HTTPS-Verschlüsselung für alle Übertragungen
- **NFR-008:** Sichere Datenspeicherung

### 4.5 Portabilität
- **NFR-009:** Lauffähig auf Windows, Linux, macOS
- **NFR-010:** Kompatibel mit Java 17+

## 5. Systemschnittstellen

### 5.1 REST API
- JSON-basierte Kommunikation
- Standard HTTP-Methoden (GET, POST, PUT, DELETE)
- RESTful URL-Struktur

### 5.2 Datenbank
- Relationale Datenbank (H2/PostgreSQL)
- JPA/Hibernate für Datenzugriff

## 6. Qualitätsanforderungen

### 6.1 Wartbarkeit
- Modulare Architektur
- Dokumentierter Code
- Unit Tests (Abdeckung > 80%)

### 6.2 Testbarkeit
- Automatisierte Tests
- Testdatensets
- Continuous Integration

## 7. Anhang

### 7.1 Glossar
- **API:** Application Programming Interface
- **CRUD:** Create, Read, Update, Delete
- **HTTP:** HyperText Transfer Protocol
- **JSON:** JavaScript Object Notation
- **REST:** Representational State Transfer

### 7.2 Referenzen
- Spring Boot Documentation
- REST API Best Practices
- Software Engineering Standards
