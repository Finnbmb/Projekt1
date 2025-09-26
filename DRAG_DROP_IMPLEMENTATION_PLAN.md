# 🎯 Drag & Drop Feature - Implementierungsplan

## 📊 **Machbarkeitsanalyse**

### **Schwierigkeitsgrad: MITTEL (⭐⭐⭐/5)**

**Grund für mittlere Schwierigkeit:**
- ✅ **Vorhandene Angular-Architektur** mit moderner Signal-API
- ✅ **Robuste Backend-API** mit Update-Funktionalität  
- ✅ **FullCalendar bereits integriert** (@fullcalendar/angular)
- ✅ **Material Design System** für konsistente UX
- ❓ **Zusätzliche Drag & Drop-Library** erforderlich

---

## 🚀 **Implementierungsoptionen**

### **Option 1: Angular CDK Drag & Drop (EMPFOHLEN)**
```bash
# Bereits verfügbar in @angular/cdk
npm install @angular/cdk
```

**Vorteile:**
- ✅ Native Angular-Integration
- ✅ Bereits in Ihrem Material Design Setup
- ✅ Accessibility-Support
- ✅ Touch-Device-Unterstützung

### **Option 2: FullCalendar Drag & Drop**
```typescript
// Bereits verfügbar in @fullcalendar/interaction
import { FullCalendarModule } from '@fullcalendar/angular';
import interactionPlugin from '@fullcalendar/interaction';
```

**Vorteile:**
- ✅ Bereits integriert in Ihrem System
- ✅ Kalender-optimiert
- ✅ Cross-View Drag & Drop

---

## 🛠️ **Detaillierte Implementierung**

### **Phase 1: Frontend - Angular CDK Integration**

#### **1. CDK Drag & Drop Module importieren**
```typescript
// calendar-view.component.ts
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';

@Component({
  imports: [
    // ... existing imports
    DragDropModule
  ]
})
```

#### **2. Template für Drag & Drop erweitern**
```html
<!-- calendar-view.component.html -->
<!-- Monatsansicht mit Drag & Drop -->
<div *ngIf="currentView() === 'month'" class="month-view">
  <div class="month-grid">
    <!-- Weekday Headers -->
    <div *ngFor="let dayName of weekDayNames" class="weekday-header">
      {{ dayName }}
    </div>

    <!-- Calendar Days mit Drop-Zones -->
    <div 
      *ngFor="let day of calendarDays()" 
      class="calendar-day"
      cdkDropList
      [cdkDropListData]="day.appointments"
      (cdkDropListDropped)="onAppointmentDropped($event, day.date)"
      [class.drag-over]="isDragOver"
      [class.drop-target]="canDropOnDay(day)">
      
      <div class="day-number">{{ day.date.getDate() }}</div>
      
      <!-- Draggable Appointments -->
      <div 
        *ngFor="let appointment of day.appointments" 
        class="appointment-preview draggable-appointment"
        cdkDrag
        [cdkDragData]="appointment"
        (cdkDragStarted)="onDragStarted(appointment)"
        (cdkDragEnded)="onDragEnded()">
        
        <!-- Drag Handle -->
        <div class="drag-handle" cdkDragHandle>
          <mat-icon>drag_indicator</mat-icon>
        </div>
        
        <span class="appointment-time">{{ formatTime(appointment.startDateTime) }}</span>
        <span class="appointment-title">{{ appointment.title }}</span>
        
        <!-- Drag Preview -->
        <div *cdkDragPreview class="drag-preview">
          <div class="preview-content">
            <mat-icon>event</mat-icon>
            <span>{{ appointment.title }}</span>
            <span class="preview-time">{{ formatTime(appointment.startDateTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
```

#### **3. Component-Logik für Drag & Drop**
```typescript
// calendar-view.component.ts
export class CalendarViewComponent implements OnInit {
  // Drag & Drop State
  isDragOver = signal<boolean>(false);
  draggedAppointment = signal<Appointment | null>(null);

  // Drag & Drop Methods
  onDragStarted(appointment: Appointment) {
    this.draggedAppointment.set(appointment);
    document.body.classList.add('dragging-appointment');
  }

  onDragEnded() {
    this.draggedAppointment.set(null);
    this.isDragOver.set(false);
    document.body.classList.remove('dragging-appointment');
  }

  canDropOnDay(day: CalendarDay): boolean {
    const draggedAppt = this.draggedAppointment();
    if (!draggedAppt) return false;
    
    // Verhindere Drop auf gleichen Tag
    const currentDate = new Date(draggedAppt.startDateTime);
    return !this.isSameDay(currentDate, day.date);
  }

  async onAppointmentDropped(event: CdkDragDrop<Appointment[]>, targetDate: Date) {
    const appointment = event.item.data as Appointment;
    
    if (!appointment || !appointment.id) return;

    try {
      // Berechne neue Start- und Endzeit
      const originalStart = new Date(appointment.startDateTime);
      const originalEnd = new Date(appointment.endDateTime);
      const duration = originalEnd.getTime() - originalStart.getTime();

      // Setze neue Startzeit (gleiche Uhrzeit, neues Datum)
      const newStart = new Date(targetDate);
      newStart.setHours(originalStart.getHours(), originalStart.getMinutes(), 0, 0);
      
      const newEnd = new Date(newStart.getTime() + duration);

      // Update via API
      await this.updateAppointmentDateTime(appointment.id, newStart, newEnd);
      
      // UI Feedback
      this.snackBar.open(
        `Termin "${appointment.title}" zu ${this.formatDate(targetDate)} verschoben`, 
        'OK', 
        { duration: 3000 }
      );

      // Reload calendar
      this.loadCurrentView();

    } catch (error) {
      console.error('Error moving appointment:', error);
      this.snackBar.open('Fehler beim Verschieben des Termins', 'Fehler', { 
        duration: 5000 
      });
    }
  }

  private async updateAppointmentDateTime(appointmentId: string, newStart: Date, newEnd: Date) {
    const updateData = {
      startDateTime: this.formatDateTimeForApi(newStart),
      endDateTime: this.formatDateTimeForApi(newEnd)
    };

    await firstValueFrom(
      this.httpClient.patch(
        `${environment.apiUrl}/appointments/${appointmentId}/datetime`,
        updateData
      )
    );
  }

  private formatDateTimeForApi(date: Date): string {
    return date.toISOString().slice(0, 19); // YYYY-MM-DDTHH:mm:ss
  }

  private isSameDay(date1: Date, date2: Date): boolean {
    return date1.getDate() === date2.getDate() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getFullYear() === date2.getFullYear();
  }
}
```

#### **4. CSS für Drag & Drop Styling**
```scss
// calendar-view.component.scss
.draggable-appointment {
  position: relative;
  cursor: grab;
  transition: all 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(0,0,0,0.15);
  }

  .drag-handle {
    position: absolute;
    left: -8px;
    top: 50%;
    transform: translateY(-50%);
    opacity: 0;
    transition: opacity 0.2s ease;
    cursor: grab;

    mat-icon {
      font-size: 16px;
      color: #666;
    }
  }

  &:hover .drag-handle {
    opacity: 1;
  }
}

.cdk-drag-dragging {
  cursor: grabbing;
  opacity: 0.8;
  transform: rotate(5deg);
}

.calendar-day {
  &.cdk-drop-list-dragging .cdk-drag {
    transition: transform 250ms cubic-bezier(0, 0, 0.2, 1);
  }

  &.drop-target {
    background-color: rgba(103, 58, 183, 0.1);
    border: 2px dashed #673ab7;
    animation: pulse 1s infinite;
  }

  &.drag-over {
    background-color: rgba(76, 175, 80, 0.1);
    border: 2px solid #4caf50;
  }
}

.drag-preview {
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.25);
  padding: 12px;
  border-left: 4px solid #673ab7;

  .preview-content {
    display: flex;
    align-items: center;
    gap: 8px;

    mat-icon {
      color: #673ab7;
    }

    .preview-time {
      font-size: 12px;
      color: #666;
      margin-left: auto;
    }
  }
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}

// Global drag state
body.dragging-appointment {
  .calendar-day:not(.drop-target) {
    opacity: 0.6;
  }
}
```

---

### **Phase 2: Backend - API-Erweiterung**

#### **1. Neuer Endpoint für DateTime-Update**
```java
// AppointmentController.java
@PatchMapping("/{id}/datetime")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<AppointmentResponseDto> updateAppointmentDateTime(
        @PathVariable Long id,
        @RequestBody @Valid AppointmentDateTimeUpdateDto updateDto) {
    
    try {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        AppointmentResponseDto updated = appointmentService.updateAppointmentDateTime(
            id, updateDto, currentUserId
        );
        return ResponseEntity.ok(updated);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    } catch (AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    } catch (Exception e) {
        logger.error("Error updating appointment datetime: " + e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
```

#### **2. DTO für DateTime-Update**
```java
// AppointmentDateTimeUpdateDto.java
package de.swtp1.terminkalender.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AppointmentDateTimeUpdateDto {
    
    @NotNull(message = "Startzeit ist erforderlich")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDateTime;
    
    @NotNull(message = "Endzeit ist erforderlich")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDateTime;
    
    // Getters and Setters
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    
    // Validation
    public boolean isValid() {
        return startDateTime != null && 
               endDateTime != null && 
               endDateTime.isAfter(startDateTime);
    }
}
```

#### **3. Service-Methode erweitern**
```java
// AppointmentService.java
@Transactional
public AppointmentResponseDto updateAppointmentDateTime(
        Long appointmentId, 
        AppointmentDateTimeUpdateDto updateDto, 
        Long userId) {
    
    // Validation
    if (!updateDto.isValid()) {
        throw new IllegalArgumentException("Ungültige Zeitangaben");
    }
    
    // Find and authorize
    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden"));
    
    if (!appointment.getUserId().equals(userId)) {
        throw new AccessDeniedException("Nicht autorisiert");
    }
    
    // Update datetime
    appointment.setStartDateTime(updateDto.getStartDateTime());
    appointment.setEndDateTime(updateDto.getEndDateTime());
    
    // Validation: Check for conflicts
    validateAppointmentConflicts(appointment, userId);
    
    // Save
    Appointment saved = appointmentRepository.save(appointment);
    
    // Log activity
    logger.info("Appointment {} moved to {} by user {}", 
        appointmentId, updateDto.getStartDateTime(), userId);
    
    return toResponseDto(saved);
}

private void validateAppointmentConflicts(Appointment appointment, Long userId) {
    List<Appointment> conflicts = appointmentRepository
        .findConflictingAppointments(
            userId, 
            appointment.getStartDateTime(), 
            appointment.getEndDateTime(),
            appointment.getId() // Exclude current appointment
        );
    
    if (!conflicts.isEmpty()) {
        throw new IllegalArgumentException(
            "Terminkonflikt: Es existiert bereits ein Termin zu dieser Zeit"
        );
    }
}
```

#### **4. Repository-Methode für Konfliktprüfung**
```java
// AppointmentRepository.java
@Query("SELECT a FROM Appointment a WHERE a.userId = :userId " +
       "AND a.id != :excludeId " +
       "AND ((a.startDateTime < :endDateTime AND a.endDateTime > :startDateTime))")
List<Appointment> findConflictingAppointments(
    @Param("userId") Long userId,
    @Param("startDateTime") LocalDateTime startDateTime, 
    @Param("endDateTime") LocalDateTime endDateTime,
    @Param("excludeId") Long excludeId
);
```

---

### **Phase 3: Erweiterte Features**

#### **1. Multi-Day Drag & Drop**
```typescript
// Für mehrtägige Termine
onMultiDayDrop(event: CdkDragDrop<Appointment[]>, targetDate: Date) {
  const appointment = event.item.data as Appointment;
  const dayDifference = this.calculateDayDifference(
    new Date(appointment.startDateTime), 
    targetDate
  );
  
  // Beide Daten um die gleiche Anzahl Tage verschieben
  const newStart = new Date(appointment.startDateTime);
  const newEnd = new Date(appointment.endDateTime);
  
  newStart.setDate(newStart.getDate() + dayDifference);
  newEnd.setDate(newEnd.getDate() + dayDifference);
  
  this.updateAppointmentDateTime(appointment.id, newStart, newEnd);
}
```

#### **2. Time-Slot Drag & Drop (Wochenansicht)**
```html
<!-- Wochenansicht mit Stunden-Slots -->
<div class="week-time-slot" 
     cdkDropList
     [cdkDropListData]="getAppointmentsForHour(day.date, hour)"
     (cdkDropListDropped)="onTimeSlotDrop($event, day.date, hour)">
  <!-- Time slots for precise timing -->
</div>
```

#### **3. Drag & Drop Feedback**
```typescript
// Visual feedback during drag
onDragMoved(event: CdkDragMove) {
  const dropZone = document.elementFromPoint(event.pointerPosition.x, event.pointerPosition.y);
  
  // Highlight valid drop zones
  document.querySelectorAll('.drop-zone').forEach(zone => {
    zone.classList.remove('drag-hover');
  });
  
  if (dropZone?.classList.contains('drop-zone')) {
    dropZone.classList.add('drag-hover');
  }
}
```

---

## ⚡ **Performance-Optimierungen**

### **1. Lazy Loading für große Kalender**
```typescript
// Nur sichtbare Termine laden
loadVisibleAppointments() {
  const viewStart = this.getViewStartDate();
  const viewEnd = this.getViewEndDate();
  
  return this.appointmentService.getAppointmentsByDateRange(viewStart, viewEnd);
}
```

### **2. Debounced API-Calls**
```typescript
// Verhindere zu viele API-Calls beim Drag
private dragDropSubject = new Subject<{appointment: Appointment, date: Date}>();

constructor() {
  this.dragDropSubject.pipe(
    debounceTime(300),
    distinctUntilChanged()
  ).subscribe(({appointment, date}) => {
    this.performAppointmentMove(appointment, date);
  });
}
```

---

## 🎨 **UX/UI Verbesserungen**

### **1. Touch-Device Unterstützung**
```typescript
// Touch-friendly drag handles
@Component({
  host: {
    '(touchstart)': 'onTouchStart($event)',
    '(touchmove)': 'onTouchMove($event)',
    '(touchend)': 'onTouchEnd($event)'
  }
})
```

### **2. Accessibility Features**
```html
<!-- Screen reader support -->
<div class="appointment-preview"
     cdkDrag
     [attr.aria-label]="getAppointmentAriaLabel(appointment)"
     [attr.aria-describedby]="appointment.id + '-description'">
  
  <div [id]="appointment.id + '-description'" class="sr-only">
    Termin kann per Drag & Drop verschoben werden
  </div>
</div>
```

### **3. Keyboard Navigation**
```typescript
// Keyboard shortcuts für Drag & Drop
@HostListener('keydown', ['$event'])
onKeyDown(event: KeyboardEvent) {
  if (event.key === 'Enter' && event.shiftKey) {
    this.startKeyboardDrag();
  }
}
```

---

## 📊 **Implementierungsaufwand**

| Phase | Aufwand | Zeit | Schwierigkeit |
|-------|---------|------|---------------|
| **CDK Integration** | 2-3 Tage | ⭐⭐ | Frontend-Kenntnisse |
| **Backend API** | 1-2 Tage | ⭐ | Spring Boot Standard |
| **UI/UX Polish** | 1-2 Tage | ⭐⭐ | CSS/Design Skills |
| **Testing & Debug** | 1-2 Tage | ⭐⭐ | Integration Testing |
| **GESAMT** | **5-9 Tage** | **⭐⭐⭐** | **MITTEL** |

---

## 🚀 **Sofort-Start Anleitung**

### **Schritt 1: CDK Drag & Drop aktivieren**
```bash
cd frontend
npm install @angular/cdk  # Falls nicht installiert
```

### **Schritt 2: Minimaler Test**
```typescript
// calendar-view.component.ts - Testweise hinzufügen
import { DragDropModule } from '@angular/cdk/drag-drop';

// In Template:
<div cdkDrag>Drag mich!</div>
```

### **Schritt 3: Erste Drag & Drop Implementierung**
- Ein Termin draggable machen
- Drop-Zone definieren  
- API-Call beim Drop
- Success-Feedback

**=> In 2-3 Stunden haben Sie einen funktionierenden Prototyp!**

---

## ✅ **Fazit: SEHR GUT MACHBAR!**

**Ihr System ist ideal für Drag & Drop:**
- ✅ Moderne Angular-Architektur
- ✅ Robuste Backend-API
- ✅ Material Design Integration
- ✅ Solide Datenstrukturen

**Die Implementierung ist ein logischer nächster Schritt zur Verbesserung der User Experience!** 🎯