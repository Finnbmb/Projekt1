import { Injectable, signal } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest, User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_BASE = environment.apiUrl;
  private readonly TOKEN_KEY = 'terminkalender_token';
  
  private readonly _isAuthenticated = signal<boolean>(false);
  private readonly _currentUser = signal<User | null>(null);
  private readonly _isLoading = signal<boolean>(false);
  
  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  
  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.checkAuthStatus();
  }
  
  private checkAuthStatus(): void {
    if (this.isBrowser()) {
      const token = localStorage.getItem(this.TOKEN_KEY);
      if (token) {
        // TODO: Validate token
        this._isAuthenticated.set(true);
      }
    }
  }

  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }
  
  login(credentials: LoginRequest): Observable<LoginResponse> {
    this._isLoading.set(true);
    
    return this.http.post<LoginResponse>(`${this.API_BASE}/auth/login`, credentials).pipe(
      tap(response => {
        console.log('AuthService: Login successful, storing token:', response.token?.substring(0, 20) + '...');
        if (this.isBrowser()) {
          localStorage.setItem(this.TOKEN_KEY, response.token);
        }
        this._isAuthenticated.set(true);
        this._currentUser.set(response.user);
        this._isLoading.set(false);
      }),
      catchError(error => {
        this._isLoading.set(false);
        return throwError(() => error);
      })
    );
  }
  
  register(userData: RegisterRequest): Observable<LoginResponse> {
    console.log('AuthService.register() called');
    console.log('API_BASE:', this.API_BASE);
    console.log('Register URL:', `${this.API_BASE}/auth/register`);
    console.log('User data:', { ...userData, password: '***', confirmPassword: '***' });
    
    this._isLoading.set(true);
    
    return this.http.post<LoginResponse>(`${this.API_BASE}/auth/register`, userData).pipe(
      tap(response => {
        if (this.isBrowser()) {
          localStorage.setItem(this.TOKEN_KEY, response.token);
        }
        this._isAuthenticated.set(true);
        this._currentUser.set(response.user);
        this._isLoading.set(false);
      }),
      catchError(error => {
        console.error('Register error:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error details:', error.error);
        this._isLoading.set(false);
        return throwError(() => error);
      })
    );
  }
  
  logout(): void {
    if (this.isBrowser()) {
      localStorage.removeItem(this.TOKEN_KEY);
    }
    this._isAuthenticated.set(false);
    this._currentUser.set(null);
    this.router.navigate(['/login']);
  }

  forgotPassword(email: string): Observable<any> {
    this._isLoading.set(true);
    
    return this.http.post(`${this.API_BASE}/auth/forgot-password`, { email }).pipe(
      tap(() => {
        this._isLoading.set(false);
      }),
      catchError(error => {
        this._isLoading.set(false);
        return throwError(() => error);
      })
    );
  }
  
  getToken(): string | null {
    const token = this.isBrowser() ? localStorage.getItem(this.TOKEN_KEY) : null;
    console.log('AuthService: getToken called, token exists:', !!token);
    return token;
  }
}