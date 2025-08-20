import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-customers-list',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="customers-container">
      <h1>Customer Management</h1>
      <mat-card>
        <mat-card-content>
          <p>Customer management interface will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .customers-container {
      padding: 20px;
    }
  `]
})
export class CustomersListComponent {}
