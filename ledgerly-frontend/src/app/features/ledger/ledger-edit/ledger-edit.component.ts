import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-ledger-edit',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="ledger-edit-container">
      <h1>Edit Transaction</h1>
      <mat-card>
        <mat-card-content>
          <p>Transaction edit form will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .ledger-edit-container {
      padding: 20px;
    }
  `]
})
export class LedgerEditComponent {}
