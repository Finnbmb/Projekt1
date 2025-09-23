# ADR-0003: Manuelles DTO Mapping

Status: Accepted  
Datum: 2025-09-22

## Kontext
Die Anwendung nutzt mehrere Request-/Response-DTOs (z. B. AppointmentRequestDto, AppointmentResponseDto). Mapping kann manuell oder per Framework erfolgen.

## Entscheidung
Manuelles Mapping innerhalb der Service-Klassen (einfache Konstruktoren / Builder-Pattern optional).

## Alternativen
- MapStruct (Compile-Time Code Gen)
- ModelMapper (Reflektionsbasiert)

## Gründe
- Geringe Anzahl DTOs → Aufwand für Framework-Setup unverhältnismäßig
- Bessere Kontrolle über Mapping + Validierungslogik
- Weniger implizite Magie (Lernaspekt im Projekt)

## Konsequenzen
- Mehr Boilerplate-Code bei Wachstum
- Höherer Änderungsaufwand bei DTO-Felderweiterungen

## Perspektive
Bei deutlicher Zunahme an DTOs (>15) Evaluierung MapStruct.
