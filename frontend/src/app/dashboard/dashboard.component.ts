import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../services/auth.service';
import { AppointmentService } from '../services/appointment.service';
import { User } from '../models/user.model';
import { Appointment } from '../models/appointment.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatMenuModule,
    MatDividerModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private appointmentService = inject(AppointmentService);
  private router = inject(Router);

  currentUser = this.authService.currentUser;
  isLoading = this.appointmentService.isLoading;

  todaysAppointments: Appointment[] = [];
  upcomingAppointments: Appointment[] = [];
  thisWeekAppointments: Appointment[] = [];

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    // Heute's Termine laden
    this.appointmentService.getTodaysAppointments().subscribe({
      next: (appointments) => {
        this.todaysAppointments = appointments;
      },
      error: (error) => {
        console.error('Error loading today\'s appointments:', error);
      }
    });

    // Kommende Termine laden
    this.appointmentService.getUpcomingAppointmentsFromAPI().subscribe({
      next: (appointments) => {
        this.upcomingAppointments = appointments;
      },
      error: (error) => {
        console.error('Error loading upcoming appointments:', error);
      }
    });

    // Diese Woche's Termine laden
    this.appointmentService.getThisWeekAppointments().subscribe({
      next: (appointments) => {
        this.thisWeekAppointments = appointments;
      },
      error: (error) => {
        console.error('Error loading this week\'s appointments:', error);
      }
    });
  }

  onNewAppointment(): void {
    this.router.navigate(['/appointments/new']);
  }

  onViewAllAppointments(): void {
    this.router.navigate(['/appointments']);
  }

  onEditAppointment(appointment: Appointment): void {
    this.router.navigate(['/appointments/edit', appointment.id]);
  }

  onLogout(): void {
    this.authService.logout();
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

  getWelcomeMessage(): string {
    const hour = new Date().getHours();
    const name = this.currentUser()?.username || 'Benutzer';
    
    if (hour < 12) {
      return `Guten Morgen, ${name}!`;
    } else if (hour < 18) {
      return `Guten Tag, ${name}!`;
    } else {
      return `Guten Abend, ${name}!`;
    }
  }

  getCurrentDate(): string {
    return new Date().toLocaleDateString('de-DE', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  get todayCount(): number {
    return this.todaysAppointments.length;
  }

  get weekCount(): number {
    return this.thisWeekAppointments.length;
  }

  get upcomingCount(): number {
    return this.upcomingAppointments.length;
  }

  createNewAppointment(): void {
    this.onNewAppointment();
  }

  goToAppointments(): void {
    this.onViewAllAppointments();
  }
}