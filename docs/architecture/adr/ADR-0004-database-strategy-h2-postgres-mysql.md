# ADR-0004: Datenbankstrategie (H2 Dev → PostgreSQL / Azure MySQL Prod)

Status: Accepted  
Datum: 2025-09-22

## Kontext
Lokale Entwicklung benötigt schnelle Startzeiten und einfache Resetbarkeit. Produktion (oder Hosting) soll auf Cloud-Datenbank laufen (Azure). Ursprünglich war PostgreSQL geplant; MySQL auf Azure (Azure Database for MySQL) ist jetzt ein mögliches Hosting-Ziel.

## Entscheidung
- Entwicklungsumgebung: H2 (File Mode) für schnelle Tests.
- Produktions/Hostingziel: Azure Database for MySQL (verwalteter Service) – aufgrund verfügbarer Ressourcen / Vorgaben.
- Migrations-Tool (Flyway) wird eingeführt bevor der Wechsel produktiv erfolgt.

## Alternativen
- PostgreSQL (Managed) – Ursprünglich geplant, weiterhin Option.
- SQLite – Ungeeignet für Multi-User Prod.
- NoSQL (MongoDB) – Fehlende komplexe Query-Anforderungen rechtfertigen Wechsel nicht.

## Gründe für Azure MySQL
- Einfache Bereitstellung über Azure Portal
- Automatische Backups & Skalierungsoptionen
- Bekannte Betriebsumgebung in Zielinfrastruktur

## Konsequenzen
- SQL Dialekt-Differenzen beachten (z. B. Timestamp Präzision)
- Performance-Tuning (Indexierung) explizit in MySQL testen
- Flyway Skripte müssen MySQL-kompatibel sein

## Maßnahmen
1. Einführung Flyway (Baseline Migration aus Entities)
2. Abstraktion von Dialekt-spezifischen Funktionen vermeiden
3. Konfiguration: `spring.datasource.*` getrennt je Profile (dev, prod)
4. Smoke Test Pipeline gegen temporäre Azure MySQL Instanz (optional)

## Offene Punkte
- Entscheidung ob PostgreSQL vollständig verworfen wird (derzeit offen)
- Kostenmodell Azure vs. Alternative
