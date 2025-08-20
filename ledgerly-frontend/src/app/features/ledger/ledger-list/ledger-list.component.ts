import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-ledger-list',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <div class="ledger-container">
      <h1>Ledger Management</h1>
      <mat-card>
        <mat-card-content>
          <p>Ledger management interface will be implemented here.</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .ledger-container {
      padding: 20px;
    }
  `]
})
export class LedgerListComponent {}
