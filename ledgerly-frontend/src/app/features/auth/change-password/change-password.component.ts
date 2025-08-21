import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, MatSnackBarModule],
  template: `
    <div class="auth-container">
      <form class="auth-form" [formGroup]="changePasswordForm" (ngSubmit)="onSubmit()">
        <h2>Change Password</h2>
        
        <div class="form-group">
          <label for="usernameOrEmail">Username or Email</label>
          <input type="text" id="usernameOrEmail" formControlName="usernameOrEmail" required>
          <div *ngIf="changePasswordForm.get('usernameOrEmail')?.invalid && changePasswordForm.get('usernameOrEmail')?.touched" class="error-message">
            Username or email is required
          </div>
        </div>

        <div class="form-group">
          <label for="currentPassword">Current Password</label>
          <input type="password" id="currentPassword" formControlName="currentPassword" required>
          <div *ngIf="changePasswordForm.get('currentPassword')?.invalid && changePasswordForm.get('currentPassword')?.touched" class="error-message">
            Current password is required
          </div>
        </div>

        <div class="form-group">
          <label for="newPassword">New Password</label>
          <input type="password" id="newPassword" formControlName="newPassword" required>
          <div *ngIf="changePasswordForm.get('newPassword')?.invalid && changePasswordForm.get('newPassword')?.touched" class="error-message">
            <div *ngIf="changePasswordForm.get('newPassword')?.errors?.['required']">New password is required</div>
            <div *ngIf="changePasswordForm.get('newPassword')?.errors?.['minlength']">Password must be at least 6 characters</div>
          </div>
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirm New Password</label>
          <input type="password" id="confirmPassword" formControlName="confirmPassword" required>
          <div *ngIf="changePasswordForm.get('confirmPassword')?.invalid && changePasswordForm.get('confirmPassword')?.touched" class="error-message">
            <div *ngIf="changePasswordForm.get('confirmPassword')?.errors?.['required']">Please confirm your new password</div>
            <div *ngIf="changePasswordForm.get('confirmPassword')?.errors?.['passwordMismatch']">Passwords do not match</div>
          </div>
        </div>

        <button type="submit" [disabled]="changePasswordForm.invalid || isSubmitting">
          {{ isSubmitting ? 'Changing Password...' : 'Change Password' }}
        </button>
        
        <p *ngIf="errorMessage" class="error-message">{{ errorMessage }}</p>
        <p *ngIf="successMessage" class="success-message">{{ successMessage }}</p>
        
        <p class="redirect-link">
          <a routerLink="/auth/login">Back to Login</a>
        </p>
      </form>
    </div>
  `,
  styles: [`
    .auth-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background-color: #f5f5f5;
    }
    
    .auth-form {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      width: 100%;
      max-width: 400px;
    }
    
    .form-group {
      margin-bottom: 1rem;
    }
    
    label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 600;
      color: #333;
    }
    
    input {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
      box-sizing: border-box;
    }
    
    input:focus {
      outline: none;
      border-color: #007bff;
      box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
    }
    
    button {
      width: 100%;
      padding: 0.75rem;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      cursor: pointer;
      margin-top: 1rem;
    }
    
    button:hover:not(:disabled) {
      background-color: #0056b3;
    }
    
    button:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }
    
    .error-message {
      color: #dc3545;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }
    
    .success-message {
      color: #28a745;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }
    
    .redirect-link {
      text-align: center;
      margin-top: 1rem;
    }
    
    .redirect-link a {
      color: #007bff;
      text-decoration: none;
    }
    
    .redirect-link a:hover {
      text-decoration: underline;
    }
    
    h2 {
      text-align: center;
      margin-bottom: 1.5rem;
      color: #333;
    }
  `]
})
export class ChangePasswordComponent {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  errorMessage: string | null = null;
  successMessage: string | null = null;
  isSubmitting = false;

  changePasswordForm = this.fb.group({
    usernameOrEmail: ['', [Validators.required]],
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: this.passwordMatchValidator });

  private passwordMatchValidator(group: any) {
    const newPassword = group.get('newPassword');
    const confirmPassword = group.get('confirmPassword');
    
    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.changePasswordForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const payload = {
      usernameOrEmail: this.changePasswordForm.value.usernameOrEmail,
      currentPassword: this.changePasswordForm.value.currentPassword,
      newPassword: this.changePasswordForm.value.newPassword
    };

    this.http.post('http://localhost:8080/api/v1/auth/change-password', payload).subscribe({
      next: (response: any) => {
        this.isSubmitting = false;
        if (response.success) {
          this.successMessage = 'Password changed successfully!';
          this.snackBar.open('Password changed successfully!', 'Close', { duration: 3000 });
          
          // Clear form
          this.changePasswordForm.reset();
          
          // Redirect to login after a short delay
          setTimeout(() => {
            this.router.navigate(['/auth/login']);
          }, 2000);
        } else {
          this.errorMessage = response.message || 'Failed to change password';
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else if (err.status === 401) {
          this.errorMessage = 'Invalid username/email or current password';
        } else {
          this.errorMessage = 'An error occurred while changing password';
        }
        console.error('Change password error:', err);
      }
    });
  }
}
