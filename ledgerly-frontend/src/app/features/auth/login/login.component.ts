import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatSnackBarModule],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="logo-section">
            <h1 class="logo">Ledgerly</h1>
            <p class="tagline">Smart Financial Management</p>
          </div>
          <h2 class="auth-title">Welcome Back</h2>
          <p class="auth-subtitle">Sign in to your account to continue</p>
        </div>

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="auth-form">
          <div class="form-group">
                         <label for="usernameOrEmail" class="form-label">Username or Email</label>
             <div class="input-wrapper">
               <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
               </svg>
               <input 
                 type="text" 
                 id="usernameOrEmail" 
                 formControlName="usernameOrEmail" 
                 class="form-input" 
                 placeholder="Enter your username or email"
                 [class.error]="loginForm.get('usernameOrEmail')?.invalid && loginForm.get('usernameOrEmail')?.touched"
               />
             </div>
             <div class="error-message" *ngIf="loginForm.get('usernameOrEmail')?.invalid && loginForm.get('usernameOrEmail')?.touched">
               Username or email is required
             </div>
          </div>

          <div class="form-group">
            <label for="password" class="form-label">Password</label>
            <div class="input-wrapper">
              <svg class="input-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
              </svg>
              <input 
                type="password" 
                id="password" 
                formControlName="password" 
                class="form-input" 
                placeholder="Enter your password"
                [class.error]="loginForm.get('password')?.invalid && loginForm.get('password')?.touched"
              />
            </div>
            <div class="error-message" *ngIf="loginForm.get('password')?.invalid && loginForm.get('password')?.touched">
              Password is required
            </div>
          </div>

          <button type="submit" [disabled]="loginForm.invalid || isSubmitting" class="btn btn-primary btn-lg w-full">
            <svg *ngIf="!isSubmitting" class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 5v2a2 2 0 01-2 2H7a2 2 0 01-2-2v-2a2 2 0 012-2h5a2 2 0 012 2z"></path>
            </svg>
            <svg *ngIf="isSubmitting" class="btn-icon animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            {{ isSubmitting ? 'Signing In...' : 'Sign In' }}
          </button>
        </form>

        <div class="auth-footer">
          <div class="redirect-links">
            <a routerLink="/auth/register" class="redirect-link">
              Don't have an account? <span class="link-highlight">Register here</span>
            </a>
            <a routerLink="/auth/change-password" class="redirect-link">
              <span class="link-highlight">Change Password</span>
            </a>
          </div>
        </div>

        <!-- Error Display -->
        <div *ngIf="errorMessage" class="alert alert-error">
          {{ errorMessage }}
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: var(--spacing-4);
      background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
    }

    .auth-card {
      background: var(--white);
      border-radius: var(--radius-2xl);
      box-shadow: var(--shadow-xl);
      padding: var(--spacing-8);
      width: 100%;
      max-width: 450px;
      animation: slideUp 0.5s ease-out;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(30px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .auth-header {
      text-align: center;
      margin-bottom: var(--spacing-8);
    }

    .logo-section {
      margin-bottom: var(--spacing-6);
    }

    .logo {
      font-size: var(--font-size-3xl);
      font-weight: 700;
      color: var(--primary-color);
      margin: 0;
      background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .tagline {
      font-size: var(--font-size-sm);
      color: var(--gray-600);
      margin: var(--spacing-1) 0 0 0;
      font-weight: 500;
    }

    .auth-title {
      font-size: var(--font-size-2xl);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0 0 var(--spacing-2) 0;
    }

    .auth-subtitle {
      font-size: var(--font-size-base);
      color: var(--gray-600);
      margin: 0;
    }

    .auth-form {
      margin-bottom: var(--spacing-6);
    }

    .form-group {
      margin-bottom: var(--spacing-6);
    }

    .form-label {
      display: block;
      font-size: var(--font-size-sm);
      font-weight: 600;
      color: var(--gray-700);
      margin-bottom: var(--spacing-2);
    }

    .input-wrapper {
      position: relative;
    }

    .input-icon {
      position: absolute;
      left: var(--spacing-3);
      top: 50%;
      transform: translateY(-50%);
      width: 20px;
      height: 20px;
      color: var(--gray-400);
      z-index: 10;
    }

    .form-input {
      width: 100%;
      padding: var(--spacing-3) var(--spacing-3) var(--spacing-3) var(--spacing-10);
      font-size: var(--font-size-base);
      line-height: 1.5;
      color: var(--gray-900);
      background-color: var(--white);
      border: 2px solid var(--gray-200);
      border-radius: var(--radius-lg);
      transition: all var(--transition-fast);
    }

    .form-input:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px var(--primary-light);
    }

    .form-input.error {
      border-color: var(--danger-color);
    }

    .error-message {
      color: var(--danger-color);
      font-size: var(--font-size-sm);
      margin-top: var(--spacing-2);
      display: flex;
      align-items: center;
      gap: var(--spacing-2);
    }

    .error-message::before {
      content: "⚠";
      font-size: var(--font-size-sm);
    }

    .btn {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: var(--spacing-2);
      width: 100%;
      padding: var(--spacing-4);
      font-size: var(--font-size-base);
      font-weight: 600;
      border: none;
      border-radius: var(--radius-lg);
      cursor: pointer;
      transition: all var(--transition-fast);
      text-decoration: none;
    }

    .btn-primary {
      background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
      color: var(--white);
    }

    .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: var(--shadow-lg);
    }

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }

    .btn-icon {
      width: 20px;
      height: 20px;
    }

    .animate-spin {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    .auth-footer {
      text-align: center;
    }

    .redirect-links {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-3);
    }

    .redirect-link {
      color: var(--gray-600);
      text-decoration: none;
      font-size: var(--font-size-sm);
      transition: color var(--transition-fast);
    }

    .redirect-link:hover {
      color: var(--gray-800);
    }

    .link-highlight {
      color: var(--primary-color);
      font-weight: 600;
    }

    .link-highlight:hover {
      color: var(--primary-hover);
      text-decoration: underline;
    }

    .alert {
      margin-top: var(--spacing-4);
      padding: var(--spacing-4);
      border-radius: var(--radius-lg);
      border: 1px solid transparent;
      font-size: var(--font-size-sm);
    }

    .alert-error {
      background-color: #fef2f2;
      border-color: #fecaca;
      color: #991b1b;
    }

    @media (max-width: 640px) {
      .auth-card {
        padding: var(--spacing-6);
        margin: var(--spacing-4);
      }

      .logo {
        font-size: var(--font-size-2xl);
      }

      .auth-title {
        font-size: var(--font-size-xl);
      }
    }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  loginForm = this.fb.group({
    usernameOrEmail: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  isSubmitting = false;
  errorMessage: string | null = null;

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;

    const { usernameOrEmail, password } = this.loginForm.value;

    this.authService.login({ usernameOrEmail, password }).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.token) {
          this.snackBar.open('Login successful!', 'Close', { duration: 3000 });
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = 'Login failed';
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else if (error.status === 401) {
          this.errorMessage = 'Invalid username or password';
        } else {
          this.errorMessage = 'An error occurred during login';
        }
        console.error('Login error:', error);
      }
    });
  }
}