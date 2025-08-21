import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminService } from '../../../core/services/admin.service';
import { AdminCreateDialogComponent } from './admin-create-dialog/admin-create-dialog.component';
import { AdminEditDialogComponent } from './admin-edit-dialog/admin-edit-dialog.component';

@Component({
  selector: 'app-admin-management',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule
  ],
  template: `
    <div class="admin-management">
      <mat-card>
        <mat-card-header>
          <mat-card-title>
            <mat-icon>admin_panel_settings</mat-icon>
            Admin Management
          </mat-card-title>
          <div class="spacer"></div>
          <button mat-raised-button color="primary" (click)="openCreateDialog()">
            <mat-icon>add</mat-icon>
            Add Admin
          </button>
        </mat-card-header>
        
        <mat-card-content>
          <div class="table-container">
            <table mat-table [dataSource]="admins" class="admin-table">
              <ng-container matColumnDef="id">
                <th mat-header-cell *matHeaderCellDef>ID</th>
                <td mat-cell *matCellDef="let admin">{{ admin.id }}</td>
              </ng-container>

              <ng-container matColumnDef="username">
                <th mat-header-cell *matHeaderCellDef>Username</th>
                <td mat-cell *matCellDef="let admin">{{ admin.username }}</td>
              </ng-container>

              <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>Name</th>
                <td mat-cell *matCellDef="let admin">{{ admin.firstName }} {{ admin.lastName }}</td>
              </ng-container>

              <ng-container matColumnDef="email">
                <th mat-header-cell *matHeaderCellDef>Email</th>
                <td mat-cell *matCellDef="let admin">{{ admin.email }}</td>
              </ng-container>

              <ng-container matColumnDef="status">
                <th mat-header-cell *matHeaderCellDef>Status</th>
                <td mat-cell *matCellDef="let admin">
                  <span class="status-badge" [class.active]="admin.active" [class.inactive]="!admin.active">
                    {{ admin.active ? 'Active' : 'Inactive' }}
                  </span>
                </td>
              </ng-container>

              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef>Actions</th>
                <td mat-cell *matCellDef="let admin" class="actions-cell">
                  <button mat-icon-button color="primary" (click)="openEditDialog(admin)" matTooltip="Edit">
                    <mat-icon>edit</mat-icon>
                  </button>
                  <button mat-icon-button color="warn" (click)="deleteAdmin(admin)" matTooltip="Delete">
                    <mat-icon>delete</mat-icon>
                  </button>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            </table>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .admin-management {
      padding: 24px;
    }

    mat-card-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
    }

    mat-card-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 20px;
    }

    .spacer { flex: 1; }

    .table-container { overflow: auto; }

    .admin-table { width: 100%; }

    .actions-cell button { margin-right: 8px; }

    .status-badge {
      padding: 4px 10px;
      border-radius: 999px;
      font-size: 12px;
      font-weight: 600;
    }
    .status-badge.active { background-color: #e8f5e8; color: #2e7d32; }
    .status-badge.inactive { background-color: #ffebee; color: #c62828; }
  `]
})
export class AdminManagementComponent implements OnInit {
  private adminService = inject(AdminService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  admins: any[] = [];
  displayedColumns = ['id', 'username', 'name', 'email', 'status', 'actions'];

  ngOnInit() { this.loadAdmins(); }

  loadAdmins() {
    this.adminService.getAdmins().subscribe({
      next: (admins) => { this.admins = admins; },
      error: () => { this.snackBar.open('Failed to load admins', 'Close', { duration: 3000 }); }
    });
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(AdminCreateDialogComponent, { width: '500px' });
    dialogRef.afterClosed().subscribe(result => {
      if (result) { this.loadAdmins(); this.snackBar.open('Admin created successfully', 'Close', { duration: 3000 }); }
    });
  }

  openEditDialog(admin: any) {
    const dialogRef = this.dialog.open(AdminEditDialogComponent, { width: '500px', data: admin });
    dialogRef.afterClosed().subscribe(result => {
      if (result) { this.loadAdmins(); this.snackBar.open('Admin updated successfully', 'Close', { duration: 3000 }); }
    });
  }

  deleteAdmin(admin: any) {
    if (confirm(`Are you sure you want to delete admin "${admin.username}"?`)) {
      this.adminService.deleteAdmin(admin.id).subscribe({
        next: () => { this.loadAdmins(); this.snackBar.open('Admin deleted successfully', 'Close', { duration: 3000 }); },
        error: () => { this.snackBar.open('Failed to delete admin', 'Close', { duration: 3000 }); }
      });
    }
  }
}
