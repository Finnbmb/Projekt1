import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { CommonModule } from '@angular/common';
import { Appointment } from '../../models/appointment.model';

@Component({
  selector: 'app-appointment-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './appointment-dialog.html',
  styleUrl: './appointment-dialog.scss'
})
export class AppointmentDialog implements OnInit {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<AppointmentDialog>);
  private data = inject(MAT_DIALOG_DATA);

  appointmentForm!: FormGroup;
  isEditMode = false;

  priorities = [
    { value: 'LOW', label: 'Niedrig' },
    { value: 'MEDIUM', label: 'Mittel' },
    { value: 'HIGH', label: 'Hoch' }
  ];

  timeOptions = this.generateTimeOptions();

  ngOnInit() {
    this.isEditMode = !!this.data?.appointment;
    this.createForm();
    
    if (this.isEditMode && this.data.appointment) {
      const appointment = this.data.appointment;
      console.log('Original appointment data:', appointment);
      
      if (appointment.startDateTime) {
        const startDateTime = new Date(appointment.startDateTime);
        // Format für HTML5 date input: YYYY-MM-DD
        const startDateStr = startDateTime.getFullYear() + '-' + 
          String(startDateTime.getMonth() + 1).padStart(2, '0') + '-' + 
          String(startDateTime.getDate()).padStart(2, '0');
        // Format für HTML5 time input: HH:MM
        const startTimeStr = String(startDateTime.getHours()).padStart(2, '0') + ':' + 
          String(startDateTime.getMinutes()).padStart(2, '0');
        
        this.appointmentForm.patchValue({
          startDate: startDateStr,
          startTime: startTimeStr
        });
      }
      
      if (appointment.endDateTime) {
        const endDateTime = new Date(appointment.endDateTime);
        // Format für HTML5 date input: YYYY-MM-DD
        const endDateStr = endDateTime.getFullYear() + '-' + 
          String(endDateTime.getMonth() + 1).padStart(2, '0') + '-' + 
          String(endDateTime.getDate()).padStart(2, '0');
        // Format für HTML5 time input: HH:MM
        const endTimeStr = String(endDateTime.getHours()).padStart(2, '0') + ':' + 
          String(endDateTime.getMinutes()).padStart(2, '0');
        
        this.appointmentForm.patchValue({
          endDate: endDateStr,
          endTime: endTimeStr
        });
      }
      
      this.appointmentForm.patchValue({
        title: appointment.title,
        description: appointment.description || '',
        location: appointment.location || '',
        priority: appointment.priority || 'MEDIUM'
      });
      
      console.log('Form populated with separated date/time values');
    } else {
      // Für neue Termine: Setze heute als Standarddatum
      const today = new Date();
      const todayStr = today.getFullYear() + '-' + 
        String(today.getMonth() + 1).padStart(2, '0') + '-' + 
        String(today.getDate()).padStart(2, '0');
      
      this.appointmentForm.patchValue({
        startDate: todayStr,
        endDate: todayStr
      });
    }
  }

  private generateTimeOptions() {
    const options = [];
    for (let hour = 0; hour < 24; hour++) {
      for (let minute = 0; minute < 60; minute += 15) {
        const timeString = hour.toString().padStart(2, '0') + ':' + minute.toString().padStart(2, '0');
        const displayString = timeString + ' Uhr';
        options.push({ value: timeString, label: displayString });
      }
    }
    return options;
  }

  private createForm() {
    this.appointmentForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      startDate: ['', Validators.required],
      startTime: ['09:00', Validators.required],
      endDate: ['', Validators.required],
      endTime: ['10:00', Validators.required],
      location: ['', [Validators.maxLength(100)]],
      priority: ['MEDIUM', Validators.required]
    });
  }

  onSubmit() {
    if (this.appointmentForm.valid) {
      const formValue = this.appointmentForm.value;
      
      console.log('Form values before processing:', formValue);
      
      const startDateTime = this.combineDateAndTime(formValue.startDate, formValue.startTime);
      const endDateTime = this.combineDateAndTime(formValue.endDate, formValue.endTime);
      
      const appointmentData = {
        title: formValue.title,
        description: formValue.description,
        location: formValue.location,
        priority: formValue.priority,
        startDateTime: startDateTime,
        endDateTime: endDateTime
      };
      
      console.log('Final appointment data for backend:', appointmentData);

      this.dialogRef.close({
        action: this.isEditMode ? 'update' : 'create',
        appointment: appointmentData
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  private combineDateAndTime(dateString: string, time: string): string {
    console.log('Combining date:', dateString, 'with time:', time);
    
    if (!dateString || !time) {
      throw new Error('Date and time are required');
    }
    
    // dateString ist bereits im Format YYYY-MM-DD
    // time ist im Format HH:mm
    const result = dateString + 'T' + time + ':00';
    console.log('Combined result:', result);
    return result;
  }
}
