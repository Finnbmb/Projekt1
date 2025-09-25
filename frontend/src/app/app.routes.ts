import { Routes } from '@angular/router';

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
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'appointments',
    loadComponent: () => import('./appointments/appointments.component').then(m => m.AppointmentsComponent)
  },
  {
    path: 'appointments/new',
    loadComponent: () => import('./appointment-form/appointment-form.component').then(m => m.AppointmentFormComponent)
  },
  {
    path: 'appointments/edit/:id',
    loadComponent: () => import('./appointment-form/appointment-form.component').then(m => m.AppointmentFormComponent)
  }
];
