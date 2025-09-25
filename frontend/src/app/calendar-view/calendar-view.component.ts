import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';

import { AppointmentService } from '../services/appointment.service';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { AppointmentFormComponent } from '../appointment-form/appointment-form.component';
import { Appointment } from '../models/appointment.model';

export type CalendarView = 'month' | 'week' | 'day';

interface CalendarDay {
  date: Date;
  appointments: Appointment[];
  isCurrentMonth: boolean;
  isToday: boolean;
  isSelected: boolean;
}

interface WeekDay {
  date: Date;
  appointments: Appointment[];
  dayName: string;
  dayNumber: number;
}

@Component({
  selector: 'app-calendar-view',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatButtonToggleModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    MatSelectModule,
    MatChipsModule,
    MatTooltipModule,
    AppointmentFormComponent
  ],
  templateUrl: './calendar-view.component.html',
  styleUrl: './calendar-view.component.scss'
})
export class CalendarViewComponent implements OnInit {
  private router = inject(Router);
  private httpClient = inject(HttpClient);
  private appointmentService = inject(AppointmentService);
  private snackBar = inject(MatSnackBar);
  
  private readonly apiUrl = `${environment.apiUrl}/calendar`;

  // Signals für reactive UI
  currentView = signal<CalendarView>('month');
  currentDate = signal<Date>(new Date());
  isLoading = signal<boolean>(false);
  calendarDays = signal<CalendarDay[]>([]);
  weekDays = signal<WeekDay[]>([]);
  dayAppointments = signal<Appointment[]>([]);
  selectedDate = signal<Date | null>(null);
  selectedAppointment = signal<Appointment | null>(null);
  showAppointmentForm = signal<boolean>(false);

  // UI Konstanten
  readonly weekDayNames = ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'];
  readonly weekDayNamesFull = ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'];
  readonly monthNames = [
    'Januar', 'Februar', 'März', 'April', 'Mai', 'Juni',
    'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'
  ];

  ngOnInit() {
    this.loadCurrentView();
  }

  // Navigation
  goBack() {
    this.router.navigate(['/dashboard']);
  }

  // View Management
  setView(view: CalendarView) {
    this.currentView.set(view);
    this.loadCurrentView();
  }

  previousPeriod() {
    const current = new Date(this.currentDate());
    const view = this.currentView();
    
    if (view === 'month') {
      current.setMonth(current.getMonth() - 1);
    } else if (view === 'week') {
      current.setDate(current.getDate() - 7);
    } else if (view === 'day') {
      current.setDate(current.getDate() - 1);
    }
    
    this.currentDate.set(current);
    this.loadCurrentView();
  }

  nextPeriod() {
    const current = new Date(this.currentDate());
    const view = this.currentView();
    
    if (view === 'month') {
      current.setMonth(current.getMonth() + 1);
    } else if (view === 'week') {
      current.setDate(current.getDate() + 7);
    } else if (view === 'day') {
      current.setDate(current.getDate() + 1);
    }
    
    this.currentDate.set(current);
    this.loadCurrentView();
  }

  goToToday() {
    this.currentDate.set(new Date());
    this.loadCurrentView();
  }

  onDateSelected(date: Date) {
    this.currentDate.set(date);
    this.loadCurrentView();
  }

  // Data Loading
  private async loadCurrentView() {
    const view = this.currentView();
    
    switch (view) {
      case 'month':
        await this.loadMonthView();
        break;
      case 'week':
        await this.loadWeekView();
        break;
      case 'day':
        await this.loadDayView();
        break;
    }
  }

  private async loadMonthView() {
    this.isLoading.set(true);
    try {
      const current = this.currentDate();
      const year = current.getFullYear();
      const month = current.getMonth() + 1;

      const monthData = await this.getMonthView(year, month);
      const calendarDays = this.generateMonthCalendarDays(year, month, monthData);
      
      this.calendarDays.set(calendarDays);
    } catch (error) {
      console.error('Fehler beim Laden der Monatsansicht:', error);
      this.snackBar.open('Fehler beim Laden der Monatsansicht', 'OK', { duration: 3000 });
    } finally {
      this.isLoading.set(false);
    }
  }

  private async loadWeekView() {
    this.isLoading.set(true);
    try {
      const current = this.currentDate();
      const startOfWeek = this.getStartOfWeek(current);
      
      const weekData = await this.getWeekView(startOfWeek);
      const weekDays = this.generateWeekDays(startOfWeek, weekData);
      
      this.weekDays.set(weekDays);
    } catch (error) {
      console.error('Fehler beim Laden der Wochenansicht:', error);
      this.snackBar.open('Fehler beim Laden der Wochenansicht', 'OK', { duration: 3000 });
    } finally {
      this.isLoading.set(false);
    }
  }

  private async loadDayView() {
    this.isLoading.set(true);
    try {
      const current = this.currentDate();
      const appointments = await this.getDayView(current);
      
      this.dayAppointments.set(appointments);
    } catch (error) {
      console.error('Fehler beim Laden der Tagesansicht:', error);
      this.snackBar.open('Fehler beim Laden der Tagesansicht', 'OK', { duration: 3000 });
    } finally {
      this.isLoading.set(false);
    }
  }

  // Calendar Generation Helpers
  private generateMonthCalendarDays(year: number, month: number, monthData: { [key: string]: Appointment[] }): CalendarDay[] {
    const firstDay = new Date(year, month - 1, 1);
    const lastDay = new Date(year, month, 0);
    const startDate = new Date(firstDay);
    const today = new Date();
    
    // Start vom Sonntag der ersten Woche
    startDate.setDate(startDate.getDate() - firstDay.getDay());
    
    const days: CalendarDay[] = [];
    const current = new Date(startDate);
    
    // 6 Wochen generieren (42 Tage)
    for (let week = 0; week < 6; week++) {
      for (let day = 0; day < 7; day++) {
        const dateKey = this.formatDateKey(current);
        const appointments = monthData[dateKey] || [];
        
        days.push({
          date: new Date(current),
          appointments: appointments,
          isCurrentMonth: current.getMonth() === month - 1,
          isToday: this.isSameDay(current, today),
          isSelected: false
        });
        
        current.setDate(current.getDate() + 1);
      }
    }
    
    return days;
  }

  private generateWeekDays(startOfWeek: Date, weekData: { [key: string]: Appointment[] }): WeekDay[] {
    const days: WeekDay[] = [];
    const current = new Date(startOfWeek);
    
    for (let i = 0; i < 7; i++) {
      const dateKey = this.formatDateKey(current);
      const appointments = weekData[dateKey] || [];
      
      days.push({
        date: new Date(current),
        appointments: appointments,
        dayName: this.weekDayNamesFull[current.getDay()],
        dayNumber: current.getDate()
      });
      
      current.setDate(current.getDate() + 1);
    }
    
    return days;
  }

  // Utility Methods
  private getStartOfWeek(date: Date): Date {
    const start = new Date(date);
    const day = start.getDay();
    const diff = start.getDate() - day + (day === 0 ? -6 : 1); // Montag als Wochenstart
    start.setDate(diff);
    return start;
  }

  private isSameDay(date1: Date, date2: Date): boolean {
    return date1.getDate() === date2.getDate() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getFullYear() === date2.getFullYear();
  }

  private formatDateKey(date: Date): string {
    return date.getFullYear() + '-' + 
           String(date.getMonth() + 1).padStart(2, '0') + '-' + 
           String(date.getDate()).padStart(2, '0');
  }

  // Formatting
  getViewTitle(): string {
    const current = this.currentDate();
    const view = this.currentView();
    
    if (view === 'month') {
      return `${this.monthNames[current.getMonth()]} ${current.getFullYear()}`;
    } else if (view === 'week') {
      const startOfWeek = this.getStartOfWeek(current);
      const endOfWeek = new Date(startOfWeek);
      endOfWeek.setDate(endOfWeek.getDate() + 6);
      
      if (startOfWeek.getMonth() === endOfWeek.getMonth()) {
        return `${startOfWeek.getDate()}-${endOfWeek.getDate()} ${this.monthNames[startOfWeek.getMonth()]} ${startOfWeek.getFullYear()}`;
      } else {
        return `${startOfWeek.getDate()} ${this.monthNames[startOfWeek.getMonth()]} - ${endOfWeek.getDate()} ${this.monthNames[endOfWeek.getMonth()]} ${endOfWeek.getFullYear()}`;
      }
    } else if (view === 'day') {
      return `${this.weekDayNamesFull[current.getDay()]}, ${current.getDate()} ${this.monthNames[current.getMonth()]} ${current.getFullYear()}`;
    }
    
    return '';
  }

  formatTime(dateTime: string): string {
    return new Date(dateTime).toLocaleTimeString('de-DE', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDateTime(dateTime: string): string {
    return new Date(dateTime).toLocaleString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getPriorityColor(priority: string): string {
    switch (priority?.toLowerCase()) {
      case 'hoch':
      case 'high':
        return 'red';
      case 'mittel':
      case 'medium':
        return 'orange';
      case 'niedrig':
      case 'low':
        return 'green';
      default:
        return 'blue';
    }
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
        return priority || 'Normal';
    }
  }

  // Appointment Management
  onDayClick(day: CalendarDay) {
    this.selectedDate.set(day.date);
    if (day.appointments.length === 0) {
      this.createAppointmentForDate(day.date);
    }
  }

  onWeekDayClick(day: WeekDay) {
    this.selectedDate.set(day.date);
    if (day.appointments.length === 0) {
      this.createAppointmentForDate(day.date);
    }
  }

  createAppointmentForDate(date: Date) {
    // Set the selected date and show appointment form
    this.selectedDate.set(date);
    this.showAppointmentForm.set(true);
  }

  openCreateDialog() {
    this.selectedDate.set(this.currentDate());
    this.showAppointmentForm.set(true);
  }

  closeAppointmentForm() {
    this.showAppointmentForm.set(false);
    this.selectedDate.set(null);
    this.selectedAppointment.set(null);
  }

  onAppointmentSaved() {
    this.closeAppointmentForm();
    this.loadCurrentView(); // Reload current view
  }

  onAppointmentClick(event: Event, appointment: Appointment) {
    event.stopPropagation(); // Prevent day click event
    this.editAppointment(appointment);
  }

  async editAppointment(appointment: Appointment) {
    // Open appointment form in edit mode
    this.selectedAppointment.set(appointment);
    this.showAppointmentForm.set(true);
  }

  async deleteAppointment(appointment: Appointment) {
    if (appointment.id && confirm(`Möchten Sie den Termin "${appointment.title}" wirklich löschen?`)) {
      try {
        await this.appointmentService.deleteAppointment(appointment.id);
        this.snackBar.open('Termin erfolgreich gelöscht', 'OK', { duration: 3000 });
        this.loadCurrentView(); // Reload current view
      } catch (error) {
        console.error('Fehler beim Löschen des Termins:', error);
        this.snackBar.open('Fehler beim Löschen des Termins', 'OK', { duration: 3000 });
      }
    }
  }

  // Getter for template
  getHoursArray(): number[] {
    return Array.from({ length: 24 }, (_, i) => i);
  }

  // Appointment positioning for week/day view
  getAppointmentTop(appointment: Appointment): number {
    const startTime = new Date(appointment.startDateTime);
    const hours = startTime.getHours();
    const minutes = startTime.getMinutes();
    return (hours * 60) + minutes; // 60px per hour, 1px per minute
  }

  getAppointmentHeight(appointment: Appointment): number {
    const startTime = new Date(appointment.startDateTime);
    const endTime = new Date(appointment.endDateTime);
    const durationMs = endTime.getTime() - startTime.getTime();
    const durationMinutes = durationMs / (1000 * 60);
    return Math.max(durationMinutes, 30); // Minimum 30px height
  }

  // Helper method for template
  isTodayDate(date: Date): boolean {
    return this.isSameDay(date, new Date());
  }

  // Calendar API Methods
  private async getMonthView(year: number, month: number): Promise<{ [key: string]: Appointment[] }> {
    try {
      const response = await firstValueFrom(
        this.httpClient.get<{ [key: string]: Appointment[] }>(
          `${this.apiUrl}/month/${year}/${month}`
        )
      );
      return response || {};
    } catch (error) {
      console.error('Error fetching month view:', error);
      return {};
    }
  }

  private async getWeekView(startDate: Date): Promise<{ [key: string]: Appointment[] }> {
    try {
      const dateString = this.formatDateKey(startDate);
      const response = await firstValueFrom(
        this.httpClient.get<{ [key: string]: Appointment[] }>(
          `${this.apiUrl}/week?date=${dateString}`
        )
      );
      return response || {};
    } catch (error) {
      console.error('Error fetching week view:', error);
      return {};
    }
  }

  private async getDayView(date: Date): Promise<Appointment[]> {
    try {
      const dateString = this.formatDateKey(date);
      const response = await firstValueFrom(
        this.httpClient.get<Appointment[]>(
          `${this.apiUrl}/day?date=${dateString}`
        )
      );
      return response || [];
    } catch (error) {
      console.error('Error fetching day view:', error);
      return [];
    }
  }
}