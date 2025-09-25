import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'appointments',
    loadComponent: () => import('./appointments/appointments.component').then(m => m.AppointmentsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'appointments/new',
    loadComponent: () => import('./appointment-form/appointment-form.component').then(m => m.AppointmentFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'appointments/edit/:id',
    loadComponent: () => import('./appointment-form/appointment-form.component').then(m => m.AppointmentFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'calendar',
    loadComponent: () => import('./calendar-view/calendar-view.component').then(m => m.CalendarViewComponent),
    canActivate: [authGuard]
  }
];
