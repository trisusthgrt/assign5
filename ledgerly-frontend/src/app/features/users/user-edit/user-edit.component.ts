import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="user-edit-container">
      <h1>Edit Staff Member</h1>
      <mat-card>
        <mat-card-content>
          <p>Staff edit form will be implemented here.</p>
          <a routerLink="/users">Back to Users</a>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .user-edit-container {
      padding: 20px;
    }
  `]
})
export class UserEditComponent {}
