# API-Spezifikation - Terminkalender REST API

## 1. API-Übersicht

### 1.1 Basis-Information
- **API Version:** v1
- **Base URL:** `http://localhost:8080/api/v1`
- **Content-Type:** `application/json`
- **Authentication:** JWT Bearer Token (implementiert)
- **Database:** Azure MySQL Flexible Server (Produktion), H2 (Entwicklung)

### 1.2 HTTP Status Codes
| Code | Bedeutung | Verwendung |
|------|-----------|------------|
| 200 | OK | Erfolgreiche GET, PUT Requests |
| 201 | Created | Erfolgreiche POST Requests |
| 204 | No Content | Erfolgreiche DELETE Requests |
| 400 | Bad Request | Ungültige Request-Parameter |
| 401 | Unauthorized | Authentifizierung erforderlich |
| 403 | Forbidden | Keine Berechtigung |
| 404 | Not Found | Ressource nicht gefunden |
| 409 | Conflict | Konflikt bei Erstellung/Update |
| 500 | Internal Server Error | Server-Fehler |

## 2. Appointment API

### 2.1 Alle Termine abrufen

```http
GET /api/v1/appointments
```

**Query Parameters:**
- `date` (optional): Filter nach Datum (YYYY-MM-DD)
- `startDate` (optional): Startdatum für Zeitraum (YYYY-MM-DD)
- `endDate` (optional): Enddatum für Zeitraum (YYYY-MM-DD)
- `page` (optional): Seitennummer (default: 0)
- `size` (optional): Anzahl pro Seite (default: 20)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Team Meeting",
      "description": "Weekly team sync meeting",
      "startDateTime": "2025-07-25T10:00:00",
      "endDateTime": "2025-07-25T11:00:00",
      "location": "Conference Room A",
      "userId": 1,
      "createdAt": "2025-07-24T15:30:00",
      "updatedAt": "2025-07-24T15:30:00"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2.2 Spezifischen Termin abrufen

```http
GET /api/v1/appointments/{id}
```

**Path Parameters:**
- `id`: Eindeutige Termin-ID

**Response:**
```json
{
  "id": 1,
  "title": "Team Meeting",
  "description": "Weekly team sync meeting",
  "startDateTime": "2025-07-25T10:00:00",
  "endDateTime": "2025-07-25T11:00:00",
  "location": "Conference Room A",
  "userId": 1,
  "createdAt": "2025-07-24T15:30:00",
  "updatedAt": "2025-07-24T15:30:00"
}
```

### 2.3 Neuen Termin erstellen

```http
POST /api/v1/appointments
```

**Request Body:**
```json
{
  "title": "Team Meeting",
  "description": "Weekly team sync meeting",
  "startDateTime": "2025-07-25T10:00:00",
  "endDateTime": "2025-07-25T11:00:00",
  "location": "Conference Room A"
}
```

**Validation Rules:**
- `title`: Pflichtfeld, 1-200 Zeichen
- `description`: Optional, max. 1000 Zeichen
- `startDateTime`: Pflichtfeld, ISO 8601 Format
- `endDateTime`: Pflichtfeld, muss nach startDateTime liegen
- `location`: Optional, max. 255 Zeichen

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Team Meeting",
  "description": "Weekly team sync meeting",
  "startDateTime": "2025-07-25T10:00:00",
  "endDateTime": "2025-07-25T11:00:00",
  "location": "Conference Room A",
  "userId": 1,
  "createdAt": "2025-07-24T15:30:00",
  "updatedAt": "2025-07-24T15:30:00"
}
```

### 2.4 Termin aktualisieren

```http
PUT /api/v1/appointments/{id}
```

**Path Parameters:**
- `id`: Eindeutige Termin-ID

**Request Body:**
```json
{
  "title": "Updated Team Meeting",
  "description": "Updated weekly team sync meeting",
  "startDateTime": "2025-07-25T10:30:00",
  "endDateTime": "2025-07-25T11:30:00",
  "location": "Conference Room B"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Updated Team Meeting",
  "description": "Updated weekly team sync meeting",
  "startDateTime": "2025-07-25T10:30:00",
  "endDateTime": "2025-07-25T11:30:00",
  "location": "Conference Room B",
  "userId": 1,
  "createdAt": "2025-07-24T15:30:00",
  "updatedAt": "2025-07-24T16:45:00"
}
```

### 2.5 Termin löschen

```http
DELETE /api/v1/appointments/{id}
```

**Path Parameters:**
- `id`: Eindeutige Termin-ID

**Response (204 No Content):**
```
(Leerer Response Body)
```

## 3. Calendar API

### 3.1 Kalender-Ansicht für Monat

```http
GET /api/v1/calendar/month
```

**Query Parameters:**
- `year`: Jahr (YYYY)
- `month`: Monat (1-12)

**Response:**
```json
{
  "year": 2025,
  "month": 7,
  "days": [
    {
      "date": "2025-07-01",
      "appointments": []
    },
    {
      "date": "2025-07-25",
      "appointments": [
        {
          "id": 1,
          "title": "Team Meeting",
          "startDateTime": "2025-07-25T10:00:00",
          "endDateTime": "2025-07-25T11:00:00"
        }
      ]
    }
  ]
}
```

### 3.2 Kalender-Ansicht für Woche

```http
GET /api/v1/calendar/week
```

**Query Parameters:**
- `date`: Datum innerhalb der gewünschten Woche (YYYY-MM-DD)

**Response:**
```json
{
  "weekStartDate": "2025-07-21",
  "weekEndDate": "2025-07-27",
  "days": [
    {
      "date": "2025-07-21",
      "dayOfWeek": "MONDAY",
      "appointments": []
    },
    {
      "date": "2025-07-25",
      "dayOfWeek": "FRIDAY",
      "appointments": [
        {
          "id": 1,
          "title": "Team Meeting",
          "startDateTime": "2025-07-25T10:00:00",
          "endDateTime": "2025-07-25T11:00:00",
          "location": "Conference Room A"
        }
      ]
    }
  ]
}
```

## 4. Search API

### 4.1 Termine durchsuchen

```http
GET /api/v1/appointments/search
```

**Query Parameters:**
- `q`: Suchbegriff (durchsucht Titel und Beschreibung)
- `startDate`: Startdatum für Zeitraum (YYYY-MM-DD)
- `endDate`: Enddatum für Zeitraum (YYYY-MM-DD)
- `location`: Filter nach Ort

**Response:**
```json
{
  "query": "team",
  "totalResults": 1,
  "results": [
    {
      "id": 1,
      "title": "Team Meeting",
      "description": "Weekly team sync meeting",
      "startDateTime": "2025-07-25T10:00:00",
      "endDateTime": "2025-07-25T11:00:00",
      "location": "Conference Room A",
      "relevanceScore": 0.95
    }
  ]
}
```

## 5. User API (Geplant)

### 5.1 Benutzer erstellen

```http
POST /api/v1/users
```

**Request Body:**
```json
{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "john.doe",
  "email": "john.doe@example.com",
  "createdAt": "2025-07-24T15:30:00"
}
```

## 6. Error Responses

### 6.1 Standard Error Format

```json
{
  "timestamp": "2025-07-24T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/appointments",
  "details": [
    {
      "field": "title",
      "message": "Title is required"
    },
    {
      "field": "startDateTime",
      "message": "Start date must be in the future"
    }
  ]
}
```

### 6.2 Häufige Fehlerfälle

**Termin nicht gefunden (404):**
```json
{
  "timestamp": "2025-07-24T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Appointment with id 999 not found",
  "path": "/api/v1/appointments/999"
}
```

**Validation Error (400):**
```json
{
  "timestamp": "2025-07-24T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "End date must be after start date",
  "path": "/api/v1/appointments"
}
```

## 7. Rate Limiting (Geplant)

### 7.1 Limits
- **Standard User:** 100 Requests pro Minute
- **Admin User:** 1000 Requests pro Minute

### 7.2 Headers
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1690196400
```

## 8. Versionierung

### 8.1 Versioning Strategy
- **URL Versioning:** `/api/v1/`, `/api/v2/`
- **Backward Compatibility:** Minimum 6 Monate Support für alte Versionen

### 8.2 Deprecation
```http
Sunset: Sat, 01 Jan 2026 00:00:00 GMT
Deprecation: true
```

## 9. OpenAPI/Swagger Integration

### 9.1 Swagger UI
- **URL:** `http://localhost:8080/swagger-ui.html`
- **API Docs:** `http://localhost:8080/v3/api-docs`

### 9.2 Beispiel OpenAPI Definition
```yaml
openapi: 3.0.1
info:
  title: Calendar API
  description: REST API for appointment management
  version: 1.0.0
servers:
  - url: http://localhost:8080/api/v1
paths:
  /appointments:
    get:
      summary: Get all appointments
      parameters:
        - name: date
          in: query
          schema:
            type: string
            format: date
      responses:
        '200':
          description: List of appointments
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Appointment'
```

## 3. Authentication API

### 3.1 Benutzer registrieren

```http
POST /api/v1/auth/register
```

**Request Body:**
```json
{
  "name": "Max Mustermann",
  "email": "max@example.com",
  "password": "securePassword123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "name": "Max Mustermann",
    "email": "max@example.com"
  }
}
```

### 3.2 Benutzer anmelden

```http
POST /api/v1/auth/login
```

**Request Body:**
```json
{
  "email": "max@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "name": "Max Mustermann",
    "email": "max@example.com"
  }
}
```

### 3.3 Alle Benutzer auflisten (Admin)

```http
GET /api/v1/auth/users
```

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Max Mustermann",
    "email": "max@example.com"
  },
  {
    "id": 2,
    "name": "Anna Schmidt",
    "email": "anna@example.com"
  }
]
```

### 3.4 Benutzer löschen (Admin)

```http
DELETE /api/v1/auth/users/{userId}
```

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response (200 OK):**
```json
{
  "message": "Benutzer erfolgreich gelöscht"
}
```

## 4. Holiday API

### 4.1 Feiertage für ein Jahr abrufen

```http
GET /api/v1/holidays/year/{year}
```

**Query Parameters:**
- `state` (optional): Bundesland (BW, BY, BE, BB, HB, HH, HE, MV, NI, NW, RP, SL, SN, ST, SH, TH)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Neujahr",
    "date": "2025-01-01",
    "type": "FEDERAL",
    "applicableStates": []
  },
  {
    "id": 2,
    "name": "Heilige Drei Könige",
    "date": "2025-01-06",
    "type": "STATE_SPECIFIC",
    "applicableStates": ["BW", "BY", "ST"]
  },
  {
    "id": 3,
    "name": "Ostersonntag",
    "date": "2025-04-20",
    "type": "RELIGIOUS",
    "applicableStates": ["BB", "HE"]
  }
]
```

### 4.2 Feiertage für Datumsbereich abrufen

```http
GET /api/v1/holidays/range
```

**Query Parameters:**
- `startDate`: Startdatum (YYYY-MM-DD)
- `endDate`: Enddatum (YYYY-MM-DD)
- `state` (optional): Bundesland

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Tag der Deutschen Einheit",
    "date": "2025-10-03",
    "type": "FEDERAL",
    "applicableStates": []
  }
]
```

### 4.3 Feiertage initialisieren (Admin)

```http
POST /api/v1/holidays/initialize
```

**Request Body:**
```json
{
  "years": [2025, 2026, 2027, 2028, 2029, 2030]
}
```

**Response (200 OK):**
```json
{
  "message": "Feiertage für 6 Jahre erfolgreich initialisiert",
  "totalHolidays": 432
}
```

## 5. System Monitoring API

### 5.1 Health Check

```http
GET /actuator/health
```

**Response (200 OK):**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

### 5.2 System Metrics

```http
GET /actuator/metrics
```

**Response (200 OK):**
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.max",
    "http.server.requests",
    "system.cpu.usage"
  ]
}
```

## 6. Error Responses

### 6.1 Standard Error Format
```json
{
  "timestamp": "2025-09-26T10:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'title'",
  "path": "/api/v1/appointments"
}
```

### 6.2 Authentication Error
```json
{
  "timestamp": "2025-09-26T10:30:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is invalid or expired",
  "path": "/api/v1/appointments"
}
```

## 7. Data Models

### 7.1 Appointment Model (Extended)
```json
{
  "id": 1,
  "title": "Team Meeting",
  "description": "Weekly team sync meeting",
  "startDateTime": "2025-07-25T10:00:00",
  "endDateTime": "2025-07-25T11:00:00",
  "location": "Conference Room A",
  "category": "Work",
  "priority": "HIGH",
  "colorCode": "#ff5733",
  "reminderMinutes": 15,
  "userId": 1
}
```

### 7.2 Holiday Model
```json
{
  "id": 1,
  "name": "Neujahr",
  "date": "2025-01-01",
  "type": "FEDERAL",
  "applicableStates": []
}
```

### 7.3 User Model
```json
{
  "id": 1,
  "name": "Max Mustermann",
  "email": "max@example.com"
}
```

Diese erweiterte API-Spezifikation definiert alle implementierten Endpunkte einschließlich Authentifizierung, Feiertag-System und Admin-Funktionen für die Terminkalender-Anwendung. 📋
