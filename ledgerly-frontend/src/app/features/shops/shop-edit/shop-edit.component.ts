import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-shop-edit',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="shop-edit-container">
      <h1>Edit Business</h1>
      <mat-card>
        <mat-card-content>
          <p>Business edit form will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .shop-edit-container {
      padding: 20px;
    }
  `]
})
export class ShopEditComponent {}
