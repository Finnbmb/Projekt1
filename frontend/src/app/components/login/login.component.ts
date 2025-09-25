import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../services/auth.service';
import { LoginRequest, RegisterRequest } from '../../models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  
  readonly isLoading = this.authService.isLoading;
  private readonly _loginError = signal<string | null>(null);
  readonly loginError = this._loginError.asReadonly();
  
  private readonly _showRegister = signal<boolean>(false);
  readonly showRegister = this._showRegister.asReadonly();
  
  private readonly _showForgotPassword = signal<boolean>(false);
  readonly showForgotPassword = this._showForgotPassword.asReadonly();
  
  private readonly _registerError = signal<string | null>(null);
  readonly registerError = this._registerError.asReadonly();
  
  private readonly _forgotPasswordError = signal<string | null>(null);
  readonly forgotPasswordError = this._forgotPasswordError.asReadonly();
  
  private readonly _forgotPasswordSuccess = signal<string | null>(null);
  readonly forgotPasswordSuccess = this._forgotPasswordSuccess.asReadonly();
  
  loginForm: FormGroup = this.fb.group({
    email: ['admin@example.com', [Validators.required, Validators.minLength(3)]], // Standard-Login für Tests
    password: ['admin123', [Validators.required, Validators.minLength(6)]] // Standard-Passwort
  });
  
  registerForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  });
  
  forgotPasswordForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });
  
  onSubmit(): void {
    if (this.loginForm.valid) {
      const credentials: LoginRequest = this.loginForm.value;
      
      this.authService.login(credentials).subscribe({
        next: (response) => {
          console.log('Login component received response:', response);
          this.snackBar.open(`Willkommen, ${response.user.name || response.user.username}!`, 'Schließen', {
            duration: 3000
          });
          console.log('Navigating to dashboard...');
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          console.error('Login error:', error);
          const errorMessage = error.error?.message || 'Login fehlgeschlagen. Bitte überprüfen Sie Ihre Anmeldedaten.';
          this._loginError.set(errorMessage);
          this.snackBar.open(errorMessage, 'Schließen', {
            duration: 5000
          });
        }
      });
    } else {
      this.markAllFieldsAsTouched();
    }
  }
  
  private markAllFieldsAsTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      this.loginForm.get(key)?.markAsTouched();
    });
  }
  
  getFieldError(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName === 'email' ? 'Benutzername' : 'Passwort'} ist erforderlich`;
    }
    if (field?.hasError('minlength')) {
      const minLength = field.errors?.['minlength'].requiredLength;
      return `${fieldName === 'email' ? 'Benutzername' : 'Passwort'} muss mindestens ${minLength} Zeichen lang sein`;
    }
    return '';
  }
  
  // Register functions
  showRegisterForm(): void {
    this._showRegister.set(true);
    this._showForgotPassword.set(false);
  }
  
  hideRegisterForm(): void {
    this._showRegister.set(false);
    this.registerForm.reset();
    this._registerError.set(null);
  }
  
  onRegister(): void {
    console.log('onRegister() called');
    console.log('Form valid:', this.registerForm.valid);
    console.log('Form errors:', this.registerForm.errors);
    console.log('Form controls status:', {
      username: this.registerForm.get('username')?.valid,
      email: this.registerForm.get('email')?.valid,
      password: this.registerForm.get('password')?.valid,
      confirmPassword: this.registerForm.get('confirmPassword')?.valid
    });
    
    if (this.registerForm.valid) {
      const { username, email, password, confirmPassword } = this.registerForm.value;
      console.log('Form data:', { username, email, password: '***', confirmPassword: '***' });
      
      if (password !== confirmPassword) {
        this._registerError.set('Passwörter stimmen nicht überein');
        return;
      }
      
      const registerData: RegisterRequest = { username, email, password, confirmPassword };
      console.log('Sending register request to backend:', { ...registerData, password: '***', confirmPassword: '***' });
      
      this.authService.register(registerData).subscribe({
        next: () => {
          this.snackBar.open('Registrierung erfolgreich! Bitte melden Sie sich an.', 'Schließen', {
            duration: 5000
          });
          this.hideRegisterForm();
        },
        error: (error) => {
          const errorMessage = error.error?.message || 'Registrierung fehlgeschlagen';
          this._registerError.set(errorMessage);
          this.snackBar.open(errorMessage, 'Schließen', {
            duration: 5000
          });
        }
      });
    }
  }
  
  // Forgot password functions
  showForgotPasswordForm(): void {
    this._showForgotPassword.set(true);
    this._showRegister.set(false);
  }
  
  hideForgotPasswordForm(): void {
    this._showForgotPassword.set(false);
    this.forgotPasswordForm.reset();
    this._forgotPasswordError.set(null);
    this._forgotPasswordSuccess.set(null);
  }
  
  onForgotPassword(): void {
    if (this.forgotPasswordForm.valid) {
      const { email } = this.forgotPasswordForm.value;
      
      this.authService.forgotPassword(email).subscribe({
        next: () => {
          this._forgotPasswordSuccess.set('E-Mail zum Zurücksetzen des Passworts wurde gesendet.');
          this._forgotPasswordError.set(null);
        },
        error: (error) => {
          const errorMessage = error.error?.message || 'Fehler beim Senden der E-Mail';
          this._forgotPasswordError.set(errorMessage);
          this._forgotPasswordSuccess.set(null);
        }
      });
    }
  }
}
