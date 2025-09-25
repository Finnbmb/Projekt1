import { Component, OnInit, inject } from '@angular/core';
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
  <mat-card>
    <mat-card-header>
      <mat-card-title>{{ isEditing ? 'Termin bearbeiten' : 'Neuen Termin erstellen' }}</mat-card-title>
    </mat-card-header>

    <mat-card-content>
      <form [formGroup]="appointmentForm" (ngSubmit)="onSubmit()">
        <div class="form-row">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Titel</mat-label>
            <input matInput formControlName="title" placeholder="Terminbezeichnung">
            <mat-error *ngIf="appointmentForm.get('title')?.invalid && appointmentForm.get('title')?.touched">
              {{ getFieldError('title') }}
            </mat-error>
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Beschreibung</mat-label>
            <textarea matInput formControlName="description" rows="3" placeholder="Zusätzliche Informationen"></textarea>
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Startdatum</mat-label>
            <input matInput [matDatepicker]="startDatePicker" formControlName="startDate">
            <mat-datepicker-toggle matSuffix [for]="startDatePicker"></mat-datepicker-toggle>
            <mat-datepicker #startDatePicker></mat-datepicker>
            <mat-error *ngIf="appointmentForm.get('startDate')?.invalid && appointmentForm.get('startDate')?.touched">
              {{ getFieldError('startDate') }}
            </mat-error>
          </mat-form-field>

          <div class="time-container half-width">
            <mat-form-field appearance="outline" class="time-field">
              <mat-label>Stunde</mat-label>
              <mat-select formControlName="startHour">
                <mat-option *ngFor="let hour of hours" [value]="hour">{{ hour.toString().padStart(2, '0') }}</mat-option>
              </mat-select>
            </mat-form-field>
            <span class="time-separator">:</span>
            <mat-form-field appearance="outline" class="time-field">
              <mat-label>Minute</mat-label>
              <mat-select formControlName="startMinute">
                <mat-option *ngFor="let minute of minutes" [value]="minute">{{ minute.toString().padStart(2, '0') }}</mat-option>
              </mat-select>
            </mat-form-field>
          </div>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Enddatum</mat-label>
            <input matInput [matDatepicker]="endDatePicker" formControlName="endDate">
            <mat-datepicker-toggle matSuffix [for]="endDatePicker"></mat-datepicker-toggle>
            <mat-datepicker #endDatePicker></mat-datepicker>
            <mat-error *ngIf="appointmentForm.get('endDate')?.invalid && appointmentForm.get('endDate')?.touched">
              {{ getFieldError('endDate') }}
            </mat-error>
          </mat-form-field>

          <div class="time-container half-width">
            <mat-form-field appearance="outline" class="time-field">
              <mat-label>Stunde</mat-label>
              <mat-select formControlName="endHour">
                <mat-option *ngFor="let hour of hours" [value]="hour">{{ hour.toString().padStart(2, '0') }}</mat-option>
              </mat-select>
            </mat-form-field>
            <span class="time-separator">:</span>
            <mat-form-field appearance="outline" class="time-field">
              <mat-label>Minute</mat-label>
              <mat-select formControlName="endMinute">
                <mat-option *ngFor="let minute of minutes" [value]="minute">{{ minute.toString().padStart(2, '0') }}</mat-option>
              </mat-select>
            </mat-form-field>
          </div>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Ort</mat-label>
            <input matInput formControlName="location" placeholder="Ort oder Online">
          </mat-form-field>

          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Priorität</mat-label>
            <mat-select formControlName="priority">
              <mat-option *ngFor="let priority of priorities" [value]="priority.value">
                {{ priority.label }}
              </mat-option>
            </mat-select>
            <mat-error *ngIf="appointmentForm.get('priority')?.invalid && appointmentForm.get('priority')?.touched">
              {{ getFieldError('priority') }}
            </mat-error>
          </mat-form-field>
        </div>
      </form>
    </mat-card-content>

    <mat-card-actions align="end">
      <button mat-button type="button" (click)="onCancel()" [disabled]="isLoading">
        Abbrechen
      </button>
      <button mat-raised-button color="primary" (click)="onSubmit()" [disabled]="isLoading || appointmentForm.invalid">
        <mat-spinner *ngIf="isLoading" diameter="20"></mat-spinner>
        {{ isEditing ? 'Aktualisieren' : 'Erstellen' }}
      </button>
    </mat-card-actions>
  </mat-card>
</div>`,
  styles: [`
    .container {
      max-width: 800px;
      margin: 20px auto;
      padding: 0 20px;
    }

    .form-row {
      display: flex;
      gap: 16px;
      margin-bottom: 16px;
      align-items: flex-start;
    }

    .full-width {
      width: 100%;
    }

    .half-width {
      flex: 1;
    }

    mat-card {
      margin-bottom: 20px;
    }

    mat-card-content {
      padding: 24px;
    }

    mat-card-actions {
      padding: 16px 24px;
      gap: 8px;
    }

    mat-form-field {
      margin-bottom: 16px;
    }

    .form-row:last-child {
      margin-bottom: 0;
    }

    .time-container {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .time-field {
      flex: 1;
      margin-bottom: 16px;
    }

    .time-separator {
      font-size: 18px;
      font-weight: bold;
      color: #666;
      margin-bottom: 16px;
    }

    @media (max-width: 768px) {
      .form-row {
        flex-direction: column;
        gap: 0;
      }
      
      .half-width {
        width: 100%;
      }
    }
  `]
})
export class AppointmentFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private appointmentService = inject(AppointmentService);
  private snackBar = inject(MatSnackBar);

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
        this.router.navigate(['/appointments']);
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
        this.router.navigate(['/appointments']);
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
    this.router.navigate(['/appointments']);
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
}