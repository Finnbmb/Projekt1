# ADR-0005: Vertagung Recurrence Expansion Feature

Status: Accepted  
Datum: 2025-09-22

## Kontext
Datenmodell enthält Felder für Wiederkehr (`isRecurring`, `recurrenceType`), aber keine Logik zur Instanzierung zukünftiger Wiederholungen.

## Entscheidung
Volle Recurrence-Expansion (Generierung einzelner zukünftiger Termininstanzen) wird auf spätere Phase verschoben. Aktuell nur Speicherung der Metadaten ohne funktionalen Effekt.

## Alternativen
- Sofortige On-the-fly Expansion (Berechnung bei jeder Abfrage)
- Materialization: Vorab-Erzeugung zukünftiger Instanzen (z. B. 90 Tage)
- Hybrid: Lazy-Erzeugung beim ersten Zugriff

## Gründe für Vertagung
- Fokus auf stabile Basisfunktionen (CRUD, Auth, Kollision, Erinnerungen)
- Begrenzte Zeit (5 Tage Rest)
- Wiederkehrlogik birgt Komplexität (Zeitzonen, Verschiebungen, Ausnahmen)

## Konsequenzen
- Nutzer sieht keine tatsächliche Wiederholungswirkung
- Frontend darf Recurrence-Felder noch nicht als funktional darstellen

## Kriterien für Umsetzung in Phase 2
- Mindestens 80% der Kern-Tests grün
- Scheduler & Flyway integriert
- Belastungsszenarien (Performance) validiert

## Vorbereitete Schritte jetzt
- Felder bleiben im Modell
- Tests sichern Nicht-Funktionalität (kein automatisches Klonen)
