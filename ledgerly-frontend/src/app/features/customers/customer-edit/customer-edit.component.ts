import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-customer-edit',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="customer-edit-container">
      <h1>Edit Customer</h1>
      <mat-card>
        <mat-card-content>
          <p>Customer edit form will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .customer-edit-container {
      padding: 20px;
    }
  `]
})
export class CustomerEditComponent {}
