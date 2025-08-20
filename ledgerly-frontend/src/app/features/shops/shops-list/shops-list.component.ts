import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-shops-list',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="shops-container">
      <h1>Business Management</h1>
      <mat-card>
        <mat-card-content>
          <p>Business management interface will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .shops-container {
      padding: 20px;
    }
  `]
})
export class ShopsListComponent {}
