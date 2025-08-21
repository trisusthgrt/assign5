import { Component, inject, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../../../../core/services/admin.service';

@Component({
  selector: 'app-admin-edit-dialog',
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
      <mat-icon>edit</mat-icon>
      Edit Admin
    </h2>
    
    <mat-dialog-content>
      <form [formGroup]="adminForm" class="admin-form">
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
          <mat-label>Phone Number</mat-label>
          <input matInput formControlName="phoneNumber" placeholder="10-digit number" maxlength="10">
          <mat-error *ngIf="adminForm.get('phoneNumber')?.hasError('pattern')">
            Phone must be exactly 10 digits
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-raised-button color="primary" 
              (click)="updateAdmin()" 
              [disabled]="adminForm.invalid || isLoading">
        <mat-icon *ngIf="isLoading">hourglass_empty</mat-icon>
        <mat-icon *ngIf="!isLoading">save</mat-icon>
        {{ isLoading ? 'Updating...' : 'Update Admin' }}
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
export class AdminEditDialogComponent {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminService);
  private dialogRef = inject(MatDialogRef<AdminEditDialogComponent>);

  isLoading = false;

  adminForm: FormGroup = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.pattern(/.+@.+\..+/)]],
    phoneNumber: ['', [Validators.pattern(/^\d{10}$/)]]
  });

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    if (data) {
      this.adminForm.patchValue({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        phoneNumber: data.phoneNumber || ''
      });
    }
  }

  updateAdmin() {
    if (this.adminForm.valid) {
      this.isLoading = true;
      
      this.adminService.updateAdmin(this.data.id, this.adminForm.value).subscribe({
        next: (result) => {
          this.isLoading = false;
          this.dialogRef.close(result);
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Error updating admin:', error);
        }
      });
    }
  }
}
