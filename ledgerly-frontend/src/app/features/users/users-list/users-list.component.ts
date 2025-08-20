import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="users-container">
      <div class="users-header">
        <h1>Staff Management</h1>
        <button mat-raised-button color="primary" routerLink="/users/create">
          <mat-icon>add</mat-icon>
          Add Staff
        </button>
      </div>
      
      <mat-card>
        <mat-card-content>
          <p>Staff management interface will be implemented here.</p>
          <p>This will include:</p>
          <ul>
            <li>List of all staff members</li>
            <li>Role assignment</li>
            <li>Access revocation</li>
            <li>Shop assignments</li>
          </ul>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .users-container {
      padding: 20px;
    }
    
    .users-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }
    
    .users-header h1 {
      margin: 0;
      color: #333;
    }
  `]
})
export class UsersListComponent {}
