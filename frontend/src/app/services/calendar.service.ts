import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { Appointment } from '../models/appointment.model';

export interface CalendarViewDto {
  year: number;
  month: number;
  startDate: string;
  endDate: string;
  appointmentsByDate: { [key: string]: Appointment[] };
  holidaysByDate?: { [key: string]: any[] };
  federalState?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CalendarService {
  private httpClient = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/calendar`;
  private apiViewUrl = `${environment.apiUrl}/calendar-view`;

  // Basis Calendar API (einfache Datenstrukturen)
  async getMonthView(year: number, month: number): Promise<{ [key: string]: Appointment[] }> {
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

  async getWeekView(startDate: Date): Promise<{ [key: string]: Appointment[] }> {
    try {
      const dateString = this.formatDate(startDate);
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

  async getDayView(date: Date): Promise<Appointment[]> {
    try {
      const dateString = this.formatDate(date);
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

  // Erweiterte Calendar View API (mit Feiertagen und mehr Metadaten)
  async getMonthViewDetailed(year: number, month: number, federalState?: string): Promise<CalendarViewDto> {
    const params = federalState ? `?federalState=${federalState}` : '';
    const response = await firstValueFrom(
      this.httpClient.get<CalendarViewDto>(
        `${this.apiViewUrl}/month/${year}/${month}${params}`
      )
    );
    return response;
  }

  async getWeekViewDetailed(startDate: Date, federalState?: string): Promise<CalendarViewDto> {
    const dateString = this.formatDate(startDate);
    const params = new URLSearchParams();
    params.append('weekStart', dateString);
    if (federalState) {
      params.append('federalState', federalState);
    }
    
    const response = await firstValueFrom(
      this.httpClient.get<CalendarViewDto>(
        `${this.apiViewUrl}/week?${params}`
      )
    );
    return response;
  }

  async getCurrentMonthView(federalState?: string): Promise<CalendarViewDto> {
    const params = federalState ? `?federalState=${federalState}` : '';
    const response = await firstValueFrom(
      this.httpClient.get<CalendarViewDto>(
        `${this.apiViewUrl}/current-month${params}`
      )
    );
    return response;
  }

  async getCurrentWeekView(federalState?: string): Promise<CalendarViewDto> {
    const params = federalState ? `?federalState=${federalState}` : '';
    const response = await firstValueFrom(
      this.httpClient.get<CalendarViewDto>(
        `${this.apiViewUrl}/current-week${params}`
      )
    );
    return response;
  }

  // Hilfsmethoden
  private formatDate(date: Date): string {
    return date.getFullYear() + '-' + 
           String(date.getMonth() + 1).padStart(2, '0') + '-' + 
           String(date.getDate()).padStart(2, '0');
  }

  // Utility-Methoden für das Frontend
  getStartOfWeek(date: Date): Date {
    const start = new Date(date);
    const day = start.getDay();
    const diff = start.getDate() - day + (day === 0 ? -6 : 1); // Montag als Wochenstart
    start.setDate(diff);
    return start;
  }

  getEndOfWeek(date: Date): Date {
    const start = this.getStartOfWeek(date);
    const end = new Date(start);
    end.setDate(end.getDate() + 6);
    return end;
  }

  getStartOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth(), 1);
  }

  getEndOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0);
  }

  isSameDay(date1: Date, date2: Date): boolean {
    return date1.getDate() === date2.getDate() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getFullYear() === date2.getFullYear();
  }

  isToday(date: Date): boolean {
    return this.isSameDay(date, new Date());
  }

  formatDateKey(date: Date): string {
    return date.getFullYear() + '-' + 
           String(date.getMonth() + 1).padStart(2, '0') + '-' + 
           String(date.getDate()).padStart(2, '0');
  }
}