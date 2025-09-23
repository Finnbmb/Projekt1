# ADR-0002: JWT-basierte Authentifizierung

Status: Accepted  
Datum: 2025-09-22

## Kontext
Die Anwendung benötigt Login/Registrierung für Nutzer, später mögliche Rollen. Session-Management serverseitig erhöht Zustand und Skalierungsaufwand.

## Entscheidung
Verwendung stateless JWT Tokens (HS512 Signatur). Claims: userId, username, role.

## Alternativen
- Serverseitige HTTP Sessions (Stateful)
- OAuth2/OpenID Connect Provider (Overhead, kein externer Provider verfügbar)
- Passtoken mit DB-Lookups (mehr DB-Traffic)

## Gründe
- Einfacher Horizontal-Scale (keine Session-Replikation)
- Schnelle Implementierung
- Geringer Infrastrukturbedarf während Projektphase

## Konsequenzen
- Token-Invalidierung nur durch Ablauf oder Secret-Rotation
- Gefahr von Token Theft → Muss durch HTTPS & kurze Laufzeit mitigiert werden

## Sicherheitsmaßnahmen (geplant)
- Secret Externalisierung via ENV Variable
- Kürzere Lifetime (z. B. 2h) + Refresh-Konzept Phase 2
- Tests für 401 bei invalidem / abgelaufenem Token
