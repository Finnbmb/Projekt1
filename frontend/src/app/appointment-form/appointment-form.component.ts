import { Component, OnInit, inject, input, output } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, NativeDateAdapter } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatOptionModule } from '@angular/material/core';

import { AppointmentService } from '../services/appointment.service';
import { Appointment, AppointmentRequest } from '../models/appointment.model';

@Component({
  selector: 'app-appointment-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatIconModule,
    MatOptionModule
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'de-DE' },
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'DD.MM.YYYY',
        },
        display: {
          dateInput: 'DD.MM.YYYY',
          monthYearLabel: 'MMM YYYY',
          dateA11yLabel: 'DD.MM.YYYY',
          monthYearA11yLabel: 'MMMM YYYY',
        },
      },
    },
    {
      provide: DateAdapter,
      useClass: NativeDateAdapter,
      deps: [MAT_DATE_LOCALE]
    }
  ],
  template: `<mat-toolbar color="primary">
  <button mat-icon-button (click)="onCancel()">
    <mat-icon>arrow_back</mat-icon>
  </button>
  <span>{{ isEditing ? 'Termin bearbeiten' : 'Neuen Termin erstellen' }}</span>
</mat-toolbar>

<div class="container">
  <mat-card class="appointment-card">
    <mat-card-header>
      <mat-card-title class="form-title">
        <mat-icon>event</mat-icon>
        {{ isEditing ? 'Termin bearbeiten' : 'Neuen Termin erstellen' }}
      </mat-card-title>
    </mat-card-header>

    <mat-card-content class="form-content">
      <form [formGroup]="appointmentForm" (ngSubmit)="onSubmit()">
        
        <!-- Basic Information Section -->
        <div class="form-section">
          <h3 class="section-title">
            <mat-icon>info</mat-icon>
            Grundinformationen
          </h3>
          
          <div class="form-row">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Titel *</mat-label>
              <input matInput formControlName="title" placeholder="z.B. Team Meeting, Arzttermin, etc.">
              <mat-icon matSuffix>title</mat-icon>
              <mat-error *ngIf="appointmentForm.get('title')?.invalid && appointmentForm.get('title')?.touched">
                {{ getFieldError('title') }}
              </mat-error>
            </mat-form-field>
          </div>

          <div class="form-row">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Beschreibung</mat-label>
              <textarea matInput formControlName="description" rows="3" 
                placeholder="Zusätzliche Informationen zum Termin..."></textarea>
              <mat-icon matSuffix>description</mat-icon>
            </mat-form-field>
          </div>
        </div>

        <!-- Date and Time Section -->
        <div class="form-section">
          <h3 class="section-title">
            <mat-icon>schedule</mat-icon>
            Datum und Uhrzeit
          </h3>
          
          <!-- Start Date and Time -->
          <div class="datetime-group">
            <h4 class="datetime-label">
              <mat-icon class="datetime-icon">play_arrow</mat-icon>
              Beginn
            </h4>
            <div class="datetime-row">
              <mat-form-field appearance="outline" class="date-field">
                <mat-label>Startdatum *</mat-label>
                <input matInput [matDatepicker]="startDatePicker" formControlName="startDate" readonly>
                <mat-datepicker-toggle matSuffix [for]="startDatePicker"></mat-datepicker-toggle>
                <mat-datepicker #startDatePicker></mat-datepicker>
                <mat-error *ngIf="appointmentForm.get('startDate')?.invalid && appointmentForm.get('startDate')?.touched">
                  {{ getFieldError('startDate') }}
                </mat-error>
              </mat-form-field>

              <div class="time-group">
                <mat-form-field appearance="outline" class="time-field">
                  <mat-label>Stunde</mat-label>
                  <mat-select formControlName="startHour">
                    <mat-option *ngFor="let hour of hours" [value]="hour">
                      {{ hour.toString().padStart(2, '0') }}
                    </mat-option>
                  </mat-select>
                </mat-form-field>
                <span class="time-separator">:</span>
                <mat-form-field appearance="outline" class="time-field">
                  <mat-label>Minute</mat-label>
                  <mat-select formControlName="startMinute">
                    <mat-option *ngFor="let minute of minutes" [value]="minute">
                      {{ minute.toString().padStart(2, '0') }}
                    </mat-option>
                  </mat-select>
                </mat-form-field>
              </div>
            </div>
          </div>

          <!-- End Date and Time -->
          <div class="datetime-group">
            <h4 class="datetime-label">
              <mat-icon class="datetime-icon">stop</mat-icon>
              Ende
            </h4>
            <div class="datetime-row">
              <mat-form-field appearance="outline" class="date-field">
                <mat-label>Enddatum *</mat-label>
                <input matInput [matDatepicker]="endDatePicker" formControlName="endDate" readonly>
                <mat-datepicker-toggle matSuffix [for]="endDatePicker"></mat-datepicker-toggle>
                <mat-datepicker #endDatePicker></mat-datepicker>
                <mat-error *ngIf="appointmentForm.get('endDate')?.invalid && appointmentForm.get('endDate')?.touched">
                  {{ getFieldError('endDate') }}
                </mat-error>
              </mat-form-field>

              <div class="time-group">
                <mat-form-field appearance="outline" class="time-field">
                  <mat-label>Stunde</mat-label>
                  <mat-select formControlName="endHour">
                    <mat-option *ngFor="let hour of hours" [value]="hour">
                      {{ hour.toString().padStart(2, '0') }}
                    </mat-option>
                  </mat-select>
                </mat-form-field>
                <span class="time-separator">:</span>
                <mat-form-field appearance="outline" class="time-field">
                  <mat-label>Minute</mat-label>
                  <mat-select formControlName="endMinute">
                    <mat-option *ngFor="let minute of minutes" [value]="minute">
                      {{ minute.toString().padStart(2, '0') }}
                    </mat-option>
                  </mat-select>
                </mat-form-field>
              </div>
            </div>
          </div>
        </div>

        <!-- Additional Details Section -->
        <div class="form-section">
          <h3 class="section-title">
            <mat-icon>settings</mat-icon>
            Zusätzliche Details
          </h3>
          
          <div class="form-row">
            <mat-form-field appearance="outline" class="half-width">
              <mat-label>Ort</mat-label>
              <input matInput formControlName="location" placeholder="z.B. Konferenzraum A, Online, etc.">
              <mat-icon matSuffix>location_on</mat-icon>
            </mat-form-field>

            <mat-form-field appearance="outline" class="half-width">
              <mat-label>Priorität *</mat-label>
              <mat-select formControlName="priority">
                <mat-option *ngFor="let priority of priorities" [value]="priority.value">
                  <mat-icon [style.color]="getPriorityColor(priority.value)">
                    {{ getPriorityIcon(priority.value) }}
                  </mat-icon>
                  {{ priority.label }}
                </mat-option>
              </mat-select>
              <mat-error *ngIf="appointmentForm.get('priority')?.invalid && appointmentForm.get('priority')?.touched">
                {{ getFieldError('priority') }}
              </mat-error>
            </mat-form-field>
          </div>
        </div>
      </form>
    </mat-card-content>

    <mat-card-actions class="form-actions">
      <button mat-button type="button" (click)="onCancel()" [disabled]="isLoading" class="cancel-btn">
        <mat-icon>cancel</mat-icon>
        Abbrechen
      </button>
      <button mat-raised-button color="primary" (click)="onSubmit()" 
              [disabled]="isLoading || appointmentForm.invalid" class="submit-btn">
        <mat-spinner *ngIf="isLoading" diameter="20"></mat-spinner>
        <mat-icon *ngIf="!isLoading">{{ isEditing ? 'update' : 'add' }}</mat-icon>
        {{ isEditing ? 'Aktualisieren' : 'Erstellen' }}
      </button>
    </mat-card-actions>
  </mat-card>
</div>`,
  styles: [`
    .container {
      padding: 20px;
      max-width: 900px;
      margin: 0 auto;
      background: #f5f5f5;
      min-height: calc(100vh - 64px);
    }

    .appointment-card {
      box-shadow: 0 4px 20px rgba(0,0,0,0.1);
      border-radius: 12px;
      overflow: hidden;
    }

    .form-title {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 1.5rem;
      color: #1976d2;
    }

    .form-content {
      padding: 24px !important;
    }

    /* Section Styling */
    .form-section {
      margin-bottom: 32px;
      padding: 20px;
      background: #fafafa;
      border-radius: 8px;
      border-left: 4px solid #1976d2;
    }

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0 0 20px 0;
      font-size: 1.2rem;
      color: #1976d2;
      font-weight: 500;
    }

    /* Form Layout */
    .form-row {
      display: flex;
      gap: 20px;
      margin-bottom: 20px;
    }

    .full-width {
      width: 100%;
    }

    .half-width {
      flex: 1;
    }

    /* Date and Time Styling */
    .datetime-group {
      margin-bottom: 24px;
      padding: 16px;
      background: white;
      border-radius: 8px;
      border: 1px solid #e0e0e0;
    }

    .datetime-label {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0 0 12px 0;
      font-size: 1rem;
      color: #424242;
      font-weight: 500;
    }

    .datetime-icon {
      font-size: 20px;
      color: #1976d2;
    }

    .datetime-row {
      display: flex;
      gap: 16px;
      align-items: flex-start;
    }

    .date-field {
      flex: 2;
      min-width: 200px;
    }

    .time-group {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 8px;
      min-width: 120px;
    }

    .time-field {
      width: 70px;
    }

    .time-separator {
      font-size: 20px;
      font-weight: bold;
      color: #666;
      margin-top: 8px;
    }

    /* Enhanced Form Fields */
    ::ng-deep .mat-mdc-form-field {
      margin-bottom: 8px;
    }

    ::ng-deep .mat-mdc-form-field.date-field .mat-mdc-form-field-flex {
      background-color: white;
    }

    ::ng-deep .mat-mdc-form-field-outline {
      border-radius: 8px;
    }

    /* Priority Icons */
    ::ng-deep .mat-mdc-option .mat-icon {
      margin-right: 8px;
    }

    /* Actions */
    .form-actions {
      padding: 20px 24px;
      background: #f8f9fa;
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }

    .cancel-btn {
      min-width: 120px;
    }

    .submit-btn {
      min-width: 140px;
    }

    .submit-btn mat-spinner {
      margin-right: 8px;
    }

    /* Responsive Design */
    @media (max-width: 768px) {
      .container {
        padding: 10px;
      }

      .form-row {
        flex-direction: column;
        gap: 12px;
      }

      .datetime-row {
        flex-direction: column;
        gap: 12px;
      }

      .date-field,
      .time-group {
        width: 100%;
        min-width: unset;
      }

      .time-group {
        justify-content: center;
      }

      .form-actions {
        flex-direction: column;
      }

      .cancel-btn,
      .submit-btn {
        width: 100%;
      }
    }

    /* Success/Error States */
    ::ng-deep .mat-mdc-form-field.mat-form-field-invalid .mat-mdc-form-field-outline-thick {
      border-color: #f44336;
    }

    ::ng-deep .mat-mdc-form-field:not(.mat-form-field-invalid) .mat-mdc-form-field-outline-thick {
      border-color: #1976d2;
    }
  `]
})
export class AppointmentFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private appointmentService = inject(AppointmentService);
  private snackBar = inject(MatSnackBar);

  // Input/Output für Kalenderansicht
  initialDate = input<Date | null>(null);
  saved = output<void>();
  cancelled = output<void>();

  appointmentForm: FormGroup;
  isEditing = false;
  isLoading = false;
  appointmentId: number | null = null;

  priorities = [
    { value: 'LOW', label: 'Niedrig' },
    { value: 'MEDIUM', label: 'Mittel' },
    { value: 'HIGH', label: 'Hoch' }
  ];

  hours = Array.from({length: 24}, (_, i) => i);
  minutes = Array.from({length: 12}, (_, i) => i * 5); // 0, 5, 10, 15, ..., 55

  constructor() {
    // Set default dates to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    this.appointmentForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      startDate: [tomorrow, Validators.required],
      startHour: [9, Validators.required],
      startMinute: [0, Validators.required],
      endDate: [tomorrow, Validators.required],
      endHour: [10, Validators.required],
      endMinute: [0, Validators.required],
      location: [''],
      priority: ['MEDIUM', Validators.required]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditing = true;
      this.appointmentId = parseInt(id, 10);
      this.loadAppointment();
    } else if (this.initialDate()) {
      // Wenn initialDate gesetzt ist, verwende dieses Datum
      const date = this.initialDate()!;
      this.appointmentForm.patchValue({
        startDate: date,
        endDate: date
      });
    }
  }

  private loadAppointment(): void {
    if (!this.appointmentId) return;
    
    this.isLoading = true;
    this.appointmentService.getAppointmentById(this.appointmentId).subscribe({
      next: (appointment) => {
        this.populateForm(appointment);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading appointment:', error);
        this.snackBar.open('Fehler beim Laden des Termins', 'Schließen', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  private populateForm(appointment: Appointment): void {
    const startDateTime = new Date(appointment.startDateTime);
    const endDateTime = new Date(appointment.endDateTime);

    this.appointmentForm.patchValue({
      title: appointment.title,
      description: appointment.description || '',
      startDate: startDateTime,
      startHour: startDateTime.getHours(),
      startMinute: startDateTime.getMinutes(),
      endDate: endDateTime,
      endHour: endDateTime.getHours(),
      endMinute: endDateTime.getMinutes(),
      location: appointment.location || '',
      priority: appointment.priority
    });
  }



  onSubmit(): void {
    if (this.appointmentForm.valid) {
      this.isLoading = true;
      const formValue = this.appointmentForm.value;
      
      // Combine date and time
      const startDateTime = this.combineDateTimeFromFields(formValue.startDate, formValue.startHour, formValue.startMinute);
      const endDateTime = this.combineDateTimeFromFields(formValue.endDate, formValue.endHour, formValue.endMinute);

      const appointmentData: AppointmentRequest = {
        title: formValue.title,
        description: formValue.description,
        startDateTime: startDateTime,
        endDateTime: endDateTime,
        location: formValue.location,
        priority: formValue.priority
      };

      if (this.isEditing && this.appointmentId) {
        this.updateAppointment(appointmentData);
      } else {
        this.createAppointment(appointmentData);
      }
    } else {
      this.markAllFieldsAsTouched();
    }
  }

  private combineDateTimeFromFields(date: Date, hour: number, minute: number): string {
    const dateTime = new Date(date);
    dateTime.setHours(hour, minute, 0, 0);
    
    // Format as local datetime string in ISO format (without timezone conversion)
    const year = dateTime.getFullYear();
    const month = String(dateTime.getMonth() + 1).padStart(2, '0');
    const day = String(dateTime.getDate()).padStart(2, '0');
    const hourStr = String(dateTime.getHours()).padStart(2, '0');
    const minuteStr = String(dateTime.getMinutes()).padStart(2, '0');
    const second = String(dateTime.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hourStr}:${minuteStr}:${second}`;
  }

  private createAppointment(appointmentData: AppointmentRequest): void {
    this.appointmentService.createAppointment(appointmentData).subscribe({
      next: () => {
        this.snackBar.open('Termin erfolgreich erstellt', 'Schließen', { duration: 3000 });
        if (this.initialDate()) {
          // Wenn von Kalenderansicht aufgerufen, Output-Event auslösen
          this.saved.emit();
        } else {
          // Normale Navigation
          this.router.navigate(['/appointments']);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error creating appointment:', error);
        this.snackBar.open('Fehler beim Erstellen des Termins', 'Schließen', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  private updateAppointment(appointmentData: AppointmentRequest): void {
    if (!this.appointmentId) return;

    this.appointmentService.updateAppointment(this.appointmentId, appointmentData).subscribe({
      next: () => {
        this.snackBar.open('Termin erfolgreich aktualisiert', 'Schließen', { duration: 3000 });
        if (this.initialDate()) {
          // Wenn von Kalenderansicht aufgerufen, Output-Event auslösen
          this.saved.emit();
        } else {
          // Normale Navigation
          this.router.navigate(['/appointments']);
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error updating appointment:', error);
        this.snackBar.open('Fehler beim Aktualisieren des Termins', 'Schließen', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  private markAllFieldsAsTouched(): void {
    Object.keys(this.appointmentForm.controls).forEach(key => {
      this.appointmentForm.get(key)?.markAsTouched();
    });
  }

  onCancel(): void {
    if (this.initialDate()) {
      // Wenn von Kalenderansicht aufgerufen, Output-Event auslösen
      this.cancelled.emit();
    } else {
      // Normale Navigation
      this.router.navigate(['/appointments']);
    }
  }

  getFieldError(fieldName: string): string {
    const field = this.appointmentForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${this.getFieldLabel(fieldName)} ist erforderlich`;
    }
    if (field?.hasError('minlength')) {
      const minLength = field.errors?.['minlength'].requiredLength;
      return `${this.getFieldLabel(fieldName)} muss mindestens ${minLength} Zeichen lang sein`;
    }
    return '';
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      title: 'Titel',
      startDate: 'Startdatum',
      startHour: 'Startstunde',
      startMinute: 'Startminute',
      endDate: 'Enddatum',
      endHour: 'Endstunde',
      endMinute: 'Endminute',
      priority: 'Priorität'
    };
    return labels[fieldName] || fieldName;
  }

  getPriorityIcon(priority: string): string {
    switch (priority) {
      case 'HIGH': return 'priority_high';
      case 'MEDIUM': return 'flag';
      case 'LOW': return 'low_priority';
      default: return 'flag';
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'HIGH': return '#f44336';
      case 'MEDIUM': return '#ff9800';
      case 'LOW': return '#4caf50';
      default: return '#757575';
    }
  }
}