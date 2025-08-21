import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../../../../core/services/admin.service';

@Component({
  selector: 'app-admin-create-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>
      <mat-icon>person_add</mat-icon>
      Create New Admin
    </h2>
    
    <mat-dialog-content>
      <form [formGroup]="adminForm" class="admin-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Username</mat-label>
          <input matInput formControlName="username" placeholder="Enter username">
          <mat-error *ngIf="adminForm.get('username')?.hasError('required')">
            Username is required
          </mat-error>
          <mat-error *ngIf="adminForm.get('username')?.hasError('minlength')">
            Username must be at least 3 characters
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" type="email" placeholder="Enter email">
          <mat-error *ngIf="adminForm.get('email')?.hasError('required')">
            Email is required
          </mat-error>
          <mat-error *ngIf="adminForm.get('email')?.hasError('pattern')">
            Please enter a valid email (must include &#64; and .)
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Password</mat-label>
          <input matInput formControlName="password" type="password" placeholder="Enter password">
          <mat-error *ngIf="adminForm.get('password')?.hasError('required')">
            Password is required
          </mat-error>
          <mat-error *ngIf="adminForm.get('password')?.hasError('minlength')">
            Password must be at least 6 characters
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>First Name</mat-label>
          <input matInput formControlName="firstName" placeholder="Enter first name">
          <mat-error *ngIf="adminForm.get('firstName')?.hasError('required')">
            First name is required
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Last Name</mat-label>
          <input matInput formControlName="lastName" placeholder="Enter last name">
          <mat-error *ngIf="adminForm.get('lastName')?.hasError('required')">
            Last name is required
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" 
              (click)="createAdmin()" 
              [disabled]="adminForm.invalid || isLoading">
        <mat-icon *ngIf="isLoading">hourglass_empty</mat-icon>
        <mat-icon *ngIf="!isLoading">save</mat-icon>
        {{ isLoading ? 'Creating...' : 'Create Admin' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .admin-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      min-width: 400px;
    }

    .full-width {
      width: 100%;
    }

    mat-dialog-title {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    mat-dialog-actions {
      padding: 16px 0;
    }
  `]
})
export class AdminCreateDialogComponent {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminService);
  private dialogRef = inject(MatDialogRef<AdminCreateDialogComponent>);

  isLoading = false;

  adminForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.pattern(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    role: ['ADMIN']
  });

  createAdmin() {
    if (this.adminForm.valid) {
      this.isLoading = true;
      
      this.adminService.createAdmin(this.adminForm.value).subscribe({
        next: (result) => {
          this.isLoading = false;
          this.dialogRef.close(result);
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Error creating admin:', error);
        }
      });
    }
  }
}
