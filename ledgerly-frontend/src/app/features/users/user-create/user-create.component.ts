import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-user-create',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="user-create-container">
      <h1>Create New Staff Member</h1>
      <mat-card>
        <mat-card-content>
          <p>Staff creation form will be implemented here.</p>
          <a routerLink="/users">Back to Users</a>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .user-create-container {
      padding: 20px;
    }
  `]
})
export class UserCreateComponent {}
