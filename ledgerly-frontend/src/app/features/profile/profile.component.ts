import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';

import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatTabsModule
  ],
  template: `
    <div class="profile-container">
      <div class="profile-header">
        <h1>Profile Management</h1>
        <p>Update your personal and business information</p>
      </div>

      <mat-tab-group>
        <mat-tab label="Personal Information">
          <mat-card class="profile-card">
            <mat-card-header>
              <mat-card-title>Personal Details</mat-card-title>
              <mat-card-subtitle>Update your personal information</mat-card-subtitle>
            </mat-card-header>
            
            <mat-card-content>
              <form [formGroup]="personalForm" (ngSubmit)="updatePersonalInfo()" class="profile-form">
                <div class="form-row">
                  <mat-form-field appearance="outline">
                    <mat-label>First Name</mat-label>
                    <input matInput formControlName="firstName" placeholder="Enter first name">
                    <mat-icon matSuffix>person</mat-icon>
                    <mat-error *ngIf="personalForm.get('firstName')?.hasError('required')">
                      First name is required
                    </mat-error>
                  </mat-form-field>

                  <mat-form-field appearance="outline">
                    <mat-label>Last Name</mat-label>
                    <input matInput formControlName="lastName" placeholder="Enter last name">
                    <mat-icon matSuffix>person</mat-icon>
                    <mat-error *ngIf="personalForm.get('lastName')?.hasError('required')">
                      Last name is required
                    </mat-error>
                  </mat-form-field>
                </div>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Email</mat-label>
                  <input matInput type="email" formControlName="email" placeholder="Enter email" readonly>
                  <mat-icon matSuffix>email</mat-icon>
                  <mat-hint>Email cannot be changed</mat-hint>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Phone Number</mat-label>
                  <input matInput formControlName="phoneNumber" placeholder="Enter phone number">
                  <mat-icon matSuffix>phone</mat-icon>
                </mat-form-field>

                <div class="form-actions">
                  <button 
                    mat-raised-button 
                    color="primary" 
                    type="submit"
                    [disabled]="personalForm.invalid || isUpdatingPersonal">
                    <mat-spinner *ngIf="isUpdatingPersonal" diameter="20"></mat-spinner>
                    <span *ngIf="!isUpdatingPersonal">Update Personal Info</span>
                  </button>
                </div>
              </form>
            </mat-card-content>
          </mat-card>
        </mat-tab>

        <mat-tab label="Business Information" *ngIf="currentUser?.role === 'OWNER'">
          <mat-card class="profile-card">
            <mat-card-header>
              <mat-card-title>Business Details</mat-card-title>
              <mat-card-subtitle>Update your business information</mat-card-subtitle>
            </mat-card-header>
            
            <mat-card-content>
              <form [formGroup]="businessForm" (ngSubmit)="updateBusinessInfo()" class="profile-form">
                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Business Name</mat-label>
                  <input matInput formControlName="businessName" placeholder="Enter business name">
                  <mat-icon matSuffix>business</mat-icon>
                  <mat-error *ngIf="businessForm.get('businessName')?.hasError('required')">
                    Business name is required
                  </mat-error>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Business Address</mat-label>
                  <textarea matInput formControlName="businessAddress" placeholder="Enter business address" rows="3"></textarea>
                  <mat-icon matSuffix>location_on</mat-icon>
                  <mat-error *ngIf="businessForm.get('businessAddress')?.hasError('required')">
                    Business address is required
                  </mat-error>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>GST Number</mat-label>
                  <input matInput formControlName="gstNumber" placeholder="Enter GST number">
                  <mat-icon matSuffix>receipt</mat-icon>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>PAN Number</mat-label>
                  <input matInput formControlName="panNumber" placeholder="Enter PAN number">
                  <mat-icon matSuffix>credit_card</mat-icon>
                </mat-form-field>

                <div class="form-actions">
                  <button 
                    mat-raised-button 
                    color="primary" 
                    type="submit"
                    [disabled]="businessForm.invalid || isUpdatingBusiness">
                    <mat-spinner *ngIf="isUpdatingBusiness" diameter="20"></mat-spinner>
                    <span *ngIf="!isUpdatingBusiness">Update Business Info</span>
                  </button>
                </div>
              </form>
            </mat-card-content>
          </mat-card>
        </mat-tab>

        <mat-tab label="Security">
          <mat-card class="profile-card">
            <mat-card-header>
              <mat-card-title>Security Settings</mat-card-title>
              <mat-card-subtitle>Change your password and security preferences</mat-card-subtitle>
            </mat-card-header>
            
            <mat-card-content>
              <form [formGroup]="securityForm" (ngSubmit)="changePassword()" class="profile-form">
                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Current Password</mat-label>
                  <input matInput type="password" formControlName="currentPassword" placeholder="Enter current password">
                  <mat-icon matSuffix>lock</mat-icon>
                  <mat-error *ngIf="securityForm.get('currentPassword')?.hasError('required')">
                    Current password is required
                  </mat-error>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>New Password</mat-label>
                  <input matInput type="password" formControlName="newPassword" placeholder="Enter new password">
                  <mat-icon matSuffix>lock_outline</mat-icon>
                  <mat-error *ngIf="securityForm.get('newPassword')?.hasError('required')">
                    New password is required
                  </mat-error>
                  <mat-error *ngIf="securityForm.get('newPassword')?.hasError('minlength')">
                    Password must be at least 6 characters
                  </mat-error>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Confirm New Password</mat-label>
                  <input matInput type="password" formControlName="confirmPassword" placeholder="Confirm new password">
                  <mat-icon matSuffix>lock_outline</mat-icon>
                  <mat-error *ngIf="securityForm.get('confirmPassword')?.hasError('required')">
                    Please confirm your new password
                  </mat-error>
                  <mat-error *ngIf="securityForm.hasError('passwordMismatch')">
                    Passwords do not match
                  </mat-error>
                </mat-form-field>

                <div class="form-actions">
                  <button 
                    mat-raised-button 
                    color="warn" 
                    type="submit"
                    [disabled]="securityForm.invalid || isChangingPassword">
                    <mat-spinner *ngIf="isChangingPassword" diameter="20"></mat-spinner>
                    <span *ngIf="!isChangingPassword">Change Password</span>
                  </button>
                </div>
              </form>
            </mat-card-content>
          </mat-card>
        </mat-tab>
      </mat-tab-group>
    </div>
  `,
  styles: [`
    .profile-container {
      padding: 20px;
    }

    .profile-header {
      margin-bottom: 30px;
    }

    .profile-header h1 {
      margin: 0 0 8px 0;
      color: #333;
      font-size: 28px;
      font-weight: 500;
    }

    .profile-header p {
      margin: 0;
      color: #666;
      font-size: 16px;
    }

    .profile-card {
      margin: 20px 0;
    }

    .profile-form {
      display: flex;
      flex-direction: column;
      gap: 20px;
      margin-top: 20px;
    }

    .form-row {
      display: flex;
      gap: 20px;
    }

    .form-row mat-form-field {
      flex: 1;
    }

    .full-width {
      width: 100%;
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      margin-top: 20px;
    }

    @media (max-width: 768px) {
      .form-row {
        flex-direction: column;
      }
    }
  `]
})
export class ProfileComponent implements OnInit {
  currentUser: User | null = null;

  personalForm!: FormGroup;
  businessForm!: FormGroup;
  securityForm!: FormGroup;

  isUpdatingPersonal = false;
  isUpdatingBusiness = false;
  isChangingPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.initializeForms();
  }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.loadUserData();
  }

  private initializeForms(): void {
    this.personalForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['']
    });

    this.businessForm = this.fb.group({
      businessName: ['', [Validators.required]],
      businessAddress: ['', [Validators.required]],
      gstNumber: [''],
      panNumber: ['']
    });

    this.securityForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');

    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    return null;
  }

  private loadUserData(): void {
    if (this.currentUser) {
      this.personalForm.patchValue({
        firstName: this.currentUser.firstName,
        lastName: this.currentUser.lastName,
        email: this.currentUser.email,
        phoneNumber: this.currentUser.phoneNumber || ''
      });
    }
  }

  updatePersonalInfo(): void {
    if (this.personalForm.valid) {
      this.isUpdatingPersonal = true;
      const updateData = this.personalForm.value;

      this.authService.updateProfile(updateData).subscribe({
        next: () => {
          this.isUpdatingPersonal = false;
          this.snackBar.open('Personal information updated successfully!', 'Close', {
            duration: 3000,
            panelClass: 'success-snackbar'
          });
        },
        error: (error) => {
          this.isUpdatingPersonal = false;
          const errorMessage = error.error?.message || 'Failed to update personal information.';
          this.snackBar.open(errorMessage, 'Close', {
            duration: 5000,
            panelClass: 'error-snackbar'
          });
        }
      });
    }
  }

  updateBusinessInfo(): void {
    if (this.businessForm.valid) {
      this.isUpdatingBusiness = true;
      const updateData = this.businessForm.value;

      // This would call a business service in a real implementation
      setTimeout(() => {
        this.isUpdatingBusiness = false;
        this.snackBar.open('Business information updated successfully!', 'Close', {
          duration: 3000,
          panelClass: 'success-snackbar'
        });
      }, 1000);
    }
  }

  changePassword(): void {
    if (this.securityForm.valid) {
      this.isChangingPassword = true;
      const passwordData = this.securityForm.value;

      // This would call a password change service in a real implementation
      setTimeout(() => {
        this.isChangingPassword = false;
        this.securityForm.reset();
        this.snackBar.open('Password changed successfully!', 'Close', {
          duration: 3000,
          panelClass: 'success-snackbar'
        });
      }, 1000);
    }
  }
}
