# ADR-0001: Entscheidung für Layered Architecture

Status: Accepted  
Datum: 2025-09-22

## Kontext
Die Domäne (Terminverwaltung, Authentifizierung, Kalenderansichten) ist überschaubar. Anforderungen an extreme Skalierung oder unabhängige Deployment-Zyklen einzelner Subdomänen bestehen in der ersten Projektphase nicht.

## Entscheidung
Wir verwenden eine klassische 3(+)-Schichten Architektur (Controller → Service → Repository → DB) mit klaren Paketgrenzen.

## Alternativen
- Hexagonal / Ports & Adapters (höhere Abstraktionskosten)
- Microservices (Overhead für Deployment, Monitoring, CI)
- Modulmonolith mit expliziten Boundaries (derzeit Overhead)

## Gründe
- Geringe kognitive Einstiegshürde
- Schnelle Implementierung bei Studierendenprojekt
- Klare Verantwortlichkeiten / Trennung von Web, Logik, Persistenz

## Konsequenzen
- Abhängigkeiten verlaufen vertikal, potenziell später schwerere Extraktion einzelner Services
- Zusätzliche Abstraktionsschicht (Service) auch bei simplen Durchreichmethoden

## Folgen / Nächste Schritte
- Durchsetzen von Abhängigkeitsregeln (keine Controller → Repository Direktzugriffe)
- Für Phase 2 Evaluierung: Teilbereiche mit Ports kapseln (Notification, Recurrence)
