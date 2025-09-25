import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AppointmentService } from '../services/appointment.service';
import { Appointment } from '../models/appointment.model';
import { ConfirmDialogComponent } from '../components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDialogModule,
    MatSnackBarModule
  ],
  templateUrl: './appointments.html',
  styleUrls: ['./appointments.scss']
})
export class AppointmentsComponent implements OnInit {
  private appointmentService = inject(AppointmentService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  appointments = this.appointmentService.appointments;
  isLoading = this.appointmentService.isLoading;

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.appointmentService.getAllAppointments().subscribe({
      next: (appointments) => {
        // Appointments werden über das Signal automatisch aktualisiert
      },
      error: (error) => {
        this.snackBar.open('Fehler beim Laden der Termine', 'Schließen', {
          duration: 5000
        });
        console.error('Error loading appointments:', error);
      }
    });
  }

  onNewAppointment(): void {
    this.router.navigate(['/appointments/new']);
  }

  onEditAppointment(appointment: Appointment): void {
    this.router.navigate(['/appointments/edit', appointment.id]);
  }

  onDeleteAppointment(appointment: Appointment): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Termin löschen',
        message: `Möchten Sie den Termin "${appointment.title}" wirklich löschen?`,
        confirmText: 'Löschen',
        cancelText: 'Abbrechen'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && appointment.id) {
        this.appointmentService.deleteAppointment(appointment.id).subscribe({
          next: () => {
            this.snackBar.open('Termin wurde gelöscht', 'Schließen', {
              duration: 3000
            });
          },
          error: (error) => {
            this.snackBar.open('Fehler beim Löschen des Termins', 'Schließen', {
              duration: 5000
            });
            console.error('Error deleting appointment:', error);
          }
        });
      }
    });
  }

  getPriorityColor(priority: string): string {
    switch (priority?.toLowerCase()) {
      case 'high':
        return 'warn';
      case 'medium':
        return 'accent';
      case 'low':
        return 'primary';
      default:
        return 'primary';
    }
  }

  formatDateTime(dateTime: string): string {
    return new Date(dateTime).toLocaleString('de-DE', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatTime(dateTime: string): string {
    return new Date(dateTime).toLocaleString('de-DE', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  openCreateDialog(): void {
    this.onNewAppointment();
  }

  getPriorityLabel(priority: string): string {
    switch (priority?.toLowerCase()) {
      case 'high':
        return 'Hoch';
      case 'medium':
        return 'Mittel';
      case 'low':
        return 'Niedrig';
      default:
        return 'Standard';
    }
  }

  editAppointment(appointment: Appointment): void {
    this.onEditAppointment(appointment);
  }

  deleteAppointment(appointment: Appointment): void {
    this.onDeleteAppointment(appointment);
  }
}