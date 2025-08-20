import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-customer-create',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="customer-create-container">
      <h1>Create New Customer</h1>
      <mat-card>
        <mat-card-content>
          <p>Customer creation form will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .customer-create-container {
      padding: 20px;
    }
  `]
})
export class CustomerCreateComponent {}
