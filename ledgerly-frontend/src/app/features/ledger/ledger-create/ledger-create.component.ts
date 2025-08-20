import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-ledger-create',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="ledger-create-container">
      <h1>Create New Transaction</h1>
      <mat-card>
        <mat-card-content>
          <p>Transaction creation form will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .ledger-create-container {
      padding: 20px;
    }
  `]
})
export class LedgerCreateComponent {}
