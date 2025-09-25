export interface Appointment {
  id?: number;
  title: string;
  description?: string;
  startDateTime: string; // ISO Date String format
  endDateTime: string;   // ISO Date String format
  location?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  userId?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface AppointmentRequest {
  title: string;
  description?: string;
  startDateTime: string;
  endDateTime: string;
  location?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
}

export interface AppointmentResponse {
  id: number;
  title: string;
  description?: string;
  startDateTime: string;
  endDateTime: string;
  location?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  userId: number;
  createdAt: string;
  updatedAt: string;
}

export interface CalendarEvent {
  id: string;
  title: string;
  start: string;
  end: string;
  color?: string;
  description?: string;
  location?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
}
