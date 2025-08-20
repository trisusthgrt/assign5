import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-shop-create',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="shop-create-container">
      <h1>Create New Business</h1>
      <mat-card>
        <mat-card-content>
          <p>Business creation form will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .shop-create-container {
      padding: 20px;
    }
  `]
})
export class ShopCreateComponent {}
