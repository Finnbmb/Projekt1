# API-Spezifikation - Terminkalender REST API

## 1. API-Übersicht

### 1.1 Basis-Information
- **API Version:** v1
- **Base URL:** `http://localhost:8080/api/v1`
- **Content-Type:** `application/json`
- **Authentication:** JWT Bearer Token (geplant)

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

Diese API-Spezifikation definiert alle Endpunkte und Datenformate für die Terminkalender-Anwendung und dient als Entwicklungsgrundlage. 📋
