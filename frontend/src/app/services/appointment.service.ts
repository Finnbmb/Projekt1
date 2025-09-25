import { Injectable, signal } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap, catchError, throwError, map } from 'rxjs';
import { Appointment, AppointmentRequest, AppointmentResponse } from '../models/appointment.model';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private readonly API_BASE = environment.apiUrl;
  private readonly _appointments = signal<Appointment[]>([]);
  private readonly _isLoading = signal<boolean>(false);
  
  readonly appointments = this._appointments.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}
  
  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }
  
  getAllAppointments(): Observable<Appointment[]> {
    this._isLoading.set(true);
    
    return this.http.get<any>(`${this.API_BASE}/appointments`).pipe(
      map(response => {
        console.log('Backend response:', response, 'Type:', typeof response, 'Is Array:', Array.isArray(response));
        // Backend returns paginated response with 'content' array
        const appointments = response.content || [];
        console.log('Setting appointments signal with:', appointments.length, 'appointments');
        this._appointments.set(appointments);
        this._isLoading.set(false);
        return appointments;
      }),
      catchError(error => {
        console.error('Error in getAllAppointments:', error);
        this._isLoading.set(false);
        return throwError(() => error);
      })
    );
  }
  
  createAppointment(appointment: AppointmentRequest): Observable<AppointmentResponse> {
    this._isLoading.set(true);
    
    return this.http.post<AppointmentResponse>(`${this.API_BASE}/appointments`, appointment, {
      headers: this.getAuthHeaders()
    }).pipe(
      tap(newAppointment => {
        const current = this._appointments();
        this._appointments.set([...current, newAppointment]);
        this._isLoading.set(false);
      }),
      catchError(error => {
        this._isLoading.set(false);
        return throwError(() => error);
      })
    );
  }
  
  updateAppointment(id: number, appointment: AppointmentRequest): Observable<AppointmentResponse> {
    this._isLoading.set(true);
    
    return this.http.put<AppointmentResponse>(`${this.API_BASE}/appointments/${id}`, appointment, {
      headers: this.getAuthHeaders()
    }).pipe(
      tap(updatedAppointment => {
        const current = this._appointments();
        const index = current.findIndex(a => a.id === id);
        if (index !== -1) {
          const updated = [...current];
          updated[index] = updatedAppointment;
          this._appointments.set(updated);
        }
        this._isLoading.set(false);
      }),
      catchError(error => {
        this._isLoading.set(false);
        return throwError(() => error);
      })
    );
  }
  
  deleteAppointment(id: number): Observable<void> {
    this._isLoading.set(true);
    
    return this.http.delete<void>(`${this.API_BASE}/appointments/${id}`, {
      headers: this.getAuthHeaders()
    }).pipe(
      tap(() => {
        const current = this._appointments();
        this._appointments.set(current.filter(a => a.id !== id));
        this._isLoading.set(false);
      }),
      catchError(error => {
        this._isLoading.set(false);
        return throwError(() => error);
      })
    );
  }

  // Dashboard-spezifische Methoden
  getTodaysAppointments(): Observable<Appointment[]> {
    return this.getAllAppointments().pipe(
      map((appointments: Appointment[]) => {
        // Defensive Programmierung: Sicherstellen, dass appointments ein Array ist
        const appointmentsArray = Array.isArray(appointments) ? appointments : [];
        
        console.log('Filtering today\'s appointments from:', appointmentsArray.length, 'total appointments');
        console.log('Available appointments:', appointmentsArray.map(a => ({ 
          title: a.title, 
          startDateTime: a.startDateTime,
          fullObject: a 
        })));
        console.log('First appointment full object:', appointmentsArray[0]);
        console.log('First appointment JSON:', JSON.stringify(appointmentsArray[0], null, 2));
        
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const endOfDay = new Date(today);
        endOfDay.setHours(23, 59, 59, 999);
        
        console.log('Today filter range:', today, 'to', endOfDay);
        
        const todaysAppointments = appointmentsArray.filter(appointment => {
          const appointmentDate = new Date(appointment.startDateTime);
          const isToday = appointmentDate >= today && appointmentDate <= endOfDay;
          console.log(`Appointment "${appointment.title}" on ${appointment.startDateTime} (${typeof appointment.startDateTime}) - isToday: ${isToday}`);
          return isToday;
        });
        
        console.log('Found', todaysAppointments.length, 'appointments for today');
        return todaysAppointments;
      }),
      catchError(error => {
        console.error('Error in getTodaysAppointments:', error);
        return throwError(() => error);
      })
    );
  }

  getUpcomingAppointmentsFromAPI(): Observable<Appointment[]> {
    const now = new Date();
    
    return this.getAllAppointments().pipe(
      map((appointments: Appointment[]) => {
        // Defensive Programmierung: Sicherstellen, dass appointments ein Array ist
        const appointmentsArray = Array.isArray(appointments) ? appointments : [];
        return appointmentsArray
          .filter((appointment: Appointment) => new Date(appointment.startDateTime) > now)
          .sort((a: Appointment, b: Appointment) => 
            new Date(a.startDateTime).getTime() - new Date(b.startDateTime).getTime())
          .slice(0, 5); // Nur die nächsten 5 Termine
      }),
      catchError(error => {
        console.error('Error in getUpcomingAppointmentsFromAPI:', error);
        return throwError(() => error);
      })
    );
  }

  getThisWeekAppointments(): Observable<Appointment[]> {
    const now = new Date();
    const startOfWeek = new Date(now.getFullYear(), now.getMonth(), now.getDate() - now.getDay());
    const endOfWeek = new Date(now.getFullYear(), now.getMonth(), now.getDate() - now.getDay() + 6, 23, 59, 59);
    
    return this.getAllAppointments().pipe(
      map((appointments: Appointment[]) => {
        // Defensive Programmierung: Sicherstellen, dass appointments ein Array ist
        const appointmentsArray = Array.isArray(appointments) ? appointments : [];
        return appointmentsArray.filter((appointment: Appointment) => {
          const appointmentDate = new Date(appointment.startDateTime);
          return appointmentDate >= startOfWeek && appointmentDate <= endOfWeek;
        });
      }),
      catchError(error => {
        console.error('Error in getThisWeekAppointments:', error);
        return throwError(() => error);
      })
    );
  }

  getAppointmentById(id: number): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.API_BASE}/appointments/${id}`).pipe(
      catchError(error => {
        console.error('Error in getAppointmentById:', error);
        return throwError(() => error);
      })
    );
  }
}