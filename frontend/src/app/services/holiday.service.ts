import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Holiday {
  id: number;
  name: string;
  date: string; // ISO date string
  type: 'PUBLIC' | 'RELIGIOUS' | 'CULTURAL' | 'SCHOOL' | 'MEMORIAL';
  federalState?: string;
  description?: string;
}

export interface HolidayCheckResponse {
  date: string;
  isHoliday: boolean;
  holidays: Holiday[];
  federalState: string;
}

@Injectable({
  providedIn: 'root'
})
export class HolidayService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/holidays`;

  /**
   * Prüft, ob ein bestimmtes Datum ein Feiertag ist
   */
  checkHoliday(date: Date, federalState?: string): Observable<HolidayCheckResponse> {
    let params = new HttpParams()
      .set('date', this.formatDate(date));
    
    if (federalState) {
      params = params.set('federalState', federalState);
    }

    return this.http.get<HolidayCheckResponse>(`${this.apiUrl}/check`, { params });
  }

  /**
   * Holt alle Feiertage für ein bestimmtes Jahr
   */
  getHolidaysForYear(year: number, federalState?: string): Observable<Holiday[]> {
    let params = new HttpParams();
    
    if (federalState) {
      params = params.set('federalState', federalState);
    }

    return this.http.get<Holiday[]>(`${this.apiUrl}/year/${year}`, { params });
  }

  /**
   * Holt alle Feiertage in einem Datumsbereich
   */
  getHolidaysInRange(startDate: Date, endDate: Date, federalState?: string): Observable<Holiday[]> {
    let params = new HttpParams()
      .set('startDate', this.formatDate(startDate))
      .set('endDate', this.formatDate(endDate));
    
    if (federalState) {
      params = params.set('federalState', federalState);
    }

    return this.http.get<Holiday[]>(`${this.apiUrl}/range`, { params });
  }

  /**
   * Holt alle Feiertage für einen bestimmten Monat
   */
  getHolidaysForMonth(year: number, month: number, federalState?: string): Observable<Holiday[]> {
    const startDate = new Date(year, month - 1, 1);
    const endDate = new Date(year, month, 0); // Letzter Tag des Monats
    
    return this.getHolidaysInRange(startDate, endDate, federalState);
  }

  /**
   * Formatiert ein Datum für die API (YYYY-MM-DD)
   */
  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  /**
   * Hilfsmethode: Prüft, ob ein Datum ein Feiertag ist (synchron aus bereits geladenen Daten)
   */
  isHolidayInList(date: Date, holidays: Holiday[]): boolean {
    const dateStr = this.formatDate(date);
    return holidays.some(holiday => holiday.date === dateStr);
  }

  /**
   * Findet Feiertage für ein bestimmtes Datum aus einer Liste
   */
  getHolidaysForDate(date: Date, holidays: Holiday[]): Holiday[] {
    const dateStr = this.formatDate(date);
    return holidays.filter(holiday => holiday.date === dateStr);
  }

  /**
   * Holt den Anzeigenamen für einen Feiertagstyp
   */
  getHolidayTypeDisplayName(type: Holiday['type']): string {
    const typeNames = {
      'PUBLIC': 'Gesetzlicher Feiertag',
      'RELIGIOUS': 'Religiöser Feiertag',
      'CULTURAL': 'Kultureller Feiertag',
      'SCHOOL': 'Schulferien',
      'MEMORIAL': 'Gedenktag'
    };
    return typeNames[type] || type;
  }

  /**
   * Holt die Farbe für einen Feiertagstyp
   */
  getHolidayTypeColor(type: Holiday['type']): string {
    const typeColors = {
      'PUBLIC': '#e74c3c',      // Rot für gesetzliche Feiertage
      'RELIGIOUS': '#9b59b6',   // Lila für religiöse Feiertage
      'CULTURAL': '#3498db',    // Blau für kulturelle Feiertage
      'SCHOOL': '#f39c12',      // Orange für Schulferien
      'MEMORIAL': '#95a5a6'     // Grau für Gedenktage
    };
    return typeColors[type] || '#7f8c8d';
  }
}