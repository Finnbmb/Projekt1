# UML-Diagramme - Terminkalender-Anwendung

## 1. Use Case Diagramm

```plantuml
@startuml
!define RECTANGLE class

actor "Benutzer" as User
actor "System Administrator" as Admin

rectangle "Terminkalender System" {
  usecase "Termin erstellen" as UC1
  usecase "Termine anzeigen" as UC2
  usecase "Termin bearbeiten" as UC3
  usecase "Termin löschen" as UC4
  usecase "Termine suchen" as UC5
  usecase "Kalenderansicht" as UC6
  usecase "Termine filtern" as UC7
  
  usecase "System konfigurieren" as UC8
  usecase "Benutzer verwalten" as UC9
  usecase "Backup erstellen" as UC10
}

User --> UC1
User --> UC2
User --> UC3
User --> UC4
User --> UC5
User --> UC6
User --> UC7

Admin --> UC8
Admin --> UC9
Admin --> UC10

UC6 .> UC2 : <<include>>
UC5 .> UC2 : <<include>>
UC7 .> UC2 : <<include>>

note right of UC1
  Benutzer kann neue Termine
  mit Titel, Datum, Uhrzeit
  und Beschreibung erstellen
end note

note right of UC6
  Monats- und Wochenansicht
  mit visueller Darstellung
  der Termine
end note
@enduml
```

## 2. Klassen-Diagramm

```plantuml
@startuml
!define ENTITY class

package "com.example.calendar.model" {
  ENTITY User {
    -Long id
    -String username
    -String email
    -String passwordHash
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    +User()
    +User(username, email)
    +getId(): Long
    +getUsername(): String
    +getEmail(): String
    +setPassword(password: String): void
    +getAppointments(): List<Appointment>
  }

  ENTITY Appointment {
    -Long id
    -String title
    -String description
    -LocalDateTime startDateTime
    -LocalDateTime endDateTime
    -String location
    -User user
    -LocalDateTime createdAt
    -LocalDateTime updatedAt
    +Appointment()
    +Appointment(title, startDateTime, endDateTime)
    +getId(): Long
    +getTitle(): String
    +getDuration(): Duration
    +isOverlapping(other: Appointment): boolean
    +getUser(): User
    +setUser(user: User): void
  }

  User ||--o{ Appointment : "has many"
}

package "com.example.calendar.dto" {
  class AppointmentDTO {
    +Long id
    +String title
    +String description
    +LocalDateTime startDateTime
    +LocalDateTime endDateTime
    +String location
    +Long userId
    +AppointmentDTO()
    +toEntity(): Appointment
    +fromEntity(appointment: Appointment): AppointmentDTO
  }

  class CreateAppointmentDTO {
    +String title
    +String description
    +LocalDateTime startDateTime
    +LocalDateTime endDateTime
    +String location
    +CreateAppointmentDTO()
    +validate(): void
  }
}

package "com.example.calendar.repository" {
  interface AppointmentRepository {
    +findAll(): List<Appointment>
    +findById(id: Long): Optional<Appointment>
    +save(appointment: Appointment): Appointment
    +deleteById(id: Long): void
    +findByUserIdAndDateBetween(userId: Long, start: LocalDateTime, end: LocalDateTime): List<Appointment>
    +findByTitleContaining(title: String): List<Appointment>
  }

  interface UserRepository {
    +findAll(): List<User>
    +findById(id: Long): Optional<User>
    +save(user: User): User
    +findByUsername(username: String): Optional<User>
    +findByEmail(email: String): Optional<User>
  }
}

package "com.example.calendar.service" {
  class AppointmentService {
    -AppointmentRepository appointmentRepository
    -UserRepository userRepository
    +AppointmentService(appointmentRepository, userRepository)
    +getAllAppointments(): List<AppointmentDTO>
    +getAppointmentById(id: Long): AppointmentDTO
    +createAppointment(dto: CreateAppointmentDTO): AppointmentDTO
    +updateAppointment(id: Long, dto: CreateAppointmentDTO): AppointmentDTO
    +deleteAppointment(id: Long): void
    +getAppointmentsByDateRange(start: LocalDateTime, end: LocalDateTime): List<AppointmentDTO>
    +searchAppointments(query: String): List<AppointmentDTO>
    -validateAppointment(dto: CreateAppointmentDTO): void
    -checkForConflicts(appointment: Appointment): void
  }

  class CalendarService {
    -AppointmentService appointmentService
    +CalendarService(appointmentService)
    +getMonthView(year: int, month: int): MonthViewDTO
    +getWeekView(date: LocalDate): WeekViewDTO
    +getDayView(date: LocalDate): DayViewDTO
  }
}

package "com.example.calendar.controller" {
  class AppointmentController {
    -AppointmentService appointmentService
    +AppointmentController(appointmentService)
    +getAllAppointments(): ResponseEntity<List<AppointmentDTO>>
    +getAppointmentById(id: Long): ResponseEntity<AppointmentDTO>
    +createAppointment(dto: CreateAppointmentDTO): ResponseEntity<AppointmentDTO>
    +updateAppointment(id: Long, dto: CreateAppointmentDTO): ResponseEntity<AppointmentDTO>
    +deleteAppointment(id: Long): ResponseEntity<Void>
    +searchAppointments(query: String): ResponseEntity<List<AppointmentDTO>>
  }

  class CalendarController {
    -CalendarService calendarService
    +CalendarController(calendarService)
    +getMonthView(year: int, month: int): ResponseEntity<MonthViewDTO>
    +getWeekView(date: LocalDate): ResponseEntity<WeekViewDTO>
  }
}

' Relationships
AppointmentService --> AppointmentRepository
AppointmentService --> UserRepository
AppointmentController --> AppointmentService
CalendarService --> AppointmentService
CalendarController --> CalendarService

AppointmentRepository ..> Appointment
UserRepository ..> User
AppointmentService ..> AppointmentDTO
AppointmentService ..> CreateAppointmentDTO
@enduml
```

## 3. Sequenz-Diagramm: Termin erstellen

```plantuml
@startuml
actor "Benutzer" as User
participant "AppointmentController" as Controller
participant "AppointmentService" as Service
participant "AppointmentRepository" as Repository
participant "UserRepository" as UserRepo
database "Database" as DB

User -> Controller: POST /api/v1/appointments\n{CreateAppointmentDTO}

activate Controller
Controller -> Controller: Validate Request
Controller -> Service: createAppointment(dto)

activate Service
Service -> Service: validateAppointment(dto)

alt Validation successful
    Service -> UserRepo: findById(userId)
    activate UserRepo
    UserRepo -> DB: SELECT * FROM users WHERE id = ?
    DB --> UserRepo: User data
    UserRepo --> Service: User entity
    deactivate UserRepo
    
    Service -> Service: checkForConflicts(appointment)
    
    alt No conflicts
        Service -> Repository: save(appointment)
        activate Repository
        Repository -> DB: INSERT INTO appointments
        DB --> Repository: Appointment with ID
        Repository --> Service: Saved appointment
        deactivate Repository
        
        Service -> Service: convertToDTO(appointment)
        Service --> Controller: AppointmentDTO
        deactivate Service
        
        Controller --> User: 201 Created\n{AppointmentDTO}
    else Conflict detected
        Service --> Controller: ConflictException
        Controller --> User: 409 Conflict\n{Error message}
    end
else Validation failed
    Service --> Controller: ValidationException
    Controller --> User: 400 Bad Request\n{Validation errors}
end

deactivate Controller
@enduml
```

## 4. Sequenz-Diagramm: Termine abrufen

```plantuml
@startuml
actor "Benutzer" as User
participant "AppointmentController" as Controller
participant "AppointmentService" as Service
participant "AppointmentRepository" as Repository
database "Database" as DB

User -> Controller: GET /api/v1/appointments?startDate=2025-07-01&endDate=2025-07-31

activate Controller
Controller -> Service: getAppointmentsByDateRange(startDate, endDate)

activate Service
Service -> Repository: findByUserIdAndDateBetween(userId, startDate, endDate)

activate Repository
Repository -> DB: SELECT * FROM appointments\nWHERE user_id = ? AND start_datetime BETWEEN ? AND ?
DB --> Repository: List of appointments
Repository --> Service: List<Appointment>
deactivate Repository

Service -> Service: convertToDTOList(appointments)
Service --> Controller: List<AppointmentDTO>
deactivate Service

Controller --> User: 200 OK\n{List<AppointmentDTO>}
deactivate Controller
@enduml
```

## 5. Aktivitäts-Diagramm: Terminbearbeitung

```plantuml
@startuml
start

:Benutzer wählt Termin zur Bearbeitung;
:System lädt Termindaten;

if (Termin gefunden?) then (ja)
  :Bearbeitungsformular anzeigen;
  :Benutzer ändert Daten;
  
  if (Eingaben gültig?) then (ja)
    if (Terminkonflikte prüfen) then (keine Konflikte)
      :Änderungen speichern;
      :Erfolgsmeldung anzeigen;
      stop
    else (Konflikt erkannt)
      :Konflikte anzeigen;
      :Alternative Zeiten vorschlagen;
    endif
  else (ungültig)
    :Validierungsfehler anzeigen;
  endif
  
  :Benutzer kann erneut bearbeiten;
  
else (nein)
  :Fehlermeldung "Termin nicht gefunden";
  stop
endif

@enduml
```

## 6. Komponenten-Diagramm

```plantuml
@startuml
package "Frontend" {
  [Web Client] as WebClient
  [Mobile App] as MobileApp
}

package "API Gateway" {
  [Spring Boot Application] as SpringBoot
}

package "Business Layer" {
  [AppointmentController] as AptController
  [CalendarController] as CalController
  [AppointmentService] as AptService
  [CalendarService] as CalService
  [SecurityService] as SecService
}

package "Data Access Layer" {
  [AppointmentRepository] as AptRepo
  [UserRepository] as UserRepo
  [JPA/Hibernate] as JPA
}

package "Database" {
  [H2 Database] as H2DB
  [PostgreSQL] as PostgresDB
}

package "External Services" {
  [Email Service] as EmailSvc
  [Calendar Export] as CalExport
}

' Frontend connections
WebClient --> SpringBoot : HTTP/REST
MobileApp --> SpringBoot : HTTP/REST

' Controller connections
SpringBoot --> AptController
SpringBoot --> CalController

' Service connections
AptController --> AptService
CalController --> CalService
AptService --> SecService
CalService --> AptService

' Repository connections
AptService --> AptRepo
AptService --> UserRepo
CalService --> AptRepo

' Data layer connections
AptRepo --> JPA
UserRepo --> JPA
JPA --> H2DB : Development
JPA --> PostgresDB : Production

' External service connections
AptService --> EmailSvc : Notifications
CalService --> CalExport : Export functionality

note right of SpringBoot
  Spring Boot Application
  with embedded Tomcat
  Port: 8080
end note

note bottom of H2DB
  In-Memory Database
  for Development
end note

note bottom of PostgresDB
  Production Database
  with persistent storage
end note
@enduml
```

## 7. Deployment-Diagramm

```plantuml
@startuml
node "Development Environment" {
  artifact "calendar-app.jar" as DevApp
  database "H2 Database" as DevDB
  
  DevApp --> DevDB
}

node "Production Environment" {
  node "Load Balancer" {
    artifact "Nginx" as LB
  }
  
  node "Application Server 1" {
    artifact "calendar-app.jar" as ProdApp1
  }
  
  node "Application Server 2" {
    artifact "calendar-app.jar" as ProdApp2
  }
  
  node "Database Server" {
    database "PostgreSQL" as ProdDB
  }
  
  node "Cache Server" {
    database "Redis" as Cache
  }
  
  LB --> ProdApp1
  LB --> ProdApp2
  ProdApp1 --> ProdDB
  ProdApp2 --> ProdDB
  ProdApp1 --> Cache
  ProdApp2 --> Cache
}

cloud "Internet" {
  actor "Users" as Users
}

Users --> LB : HTTPS/443

note right of ProdDB
  PostgreSQL 15+
  with backup strategy
end note

note right of Cache
  Redis for session
  and data caching
end note
@enduml
```

## 8. Zustandsdiagramm: Termin-Lifecycle

```plantuml
@startuml
[*] --> Draft : Termin wird erstellt

Draft --> Scheduled : Termin wird gespeichert
Draft --> [*] : Erstellung abgebrochen

Scheduled --> InProgress : Startzeitpunkt erreicht
Scheduled --> Modified : Termin wird bearbeitet
Scheduled --> Cancelled : Termin wird abgesagt

Modified --> Scheduled : Änderungen gespeichert
Modified --> [*] : Änderungen verworfen

InProgress --> Completed : Endzeitpunkt erreicht
InProgress --> Cancelled : Termin wird abgebrochen

Cancelled --> [*] : Termin gelöscht
Completed --> [*] : Termin archiviert

note right of Scheduled
  Termin ist geplant
  und bestätigt
end note

note right of InProgress
  Termin läuft
  gerade
end note

note right of Completed
  Termin wurde
  erfolgreich
  abgeschlossen
end note
@enduml
```

Diese UML-Diagramme visualisieren die wichtigsten Aspekte der Terminkalender-Anwendung und können für die Projekt-Dokumentation verwendet werden. Sie können mit PlantUML-Tools gerendert werden. 📊
