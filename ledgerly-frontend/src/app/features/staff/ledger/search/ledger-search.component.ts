import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { LedgerService } from '../../../../core/services/ledger.service';

@Component({
  selector: 'app-ledger-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="ledger-search">
      <h2>Ledger Search</h2>
      <form [formGroup]="form" (ngSubmit)="search()" class="filters">
        <input type="number" placeholder="Customer ID" formControlName="customerId" />
        <select formControlName="transactionType">
          <option value="">Any Type</option>
          <option value="CREDIT">CREDIT</option>
          <option value="DEBIT">DEBIT</option>
          <option value="OPENING_BALANCE">OPENING_BALANCE</option>
          <option value="ADJUSTMENT">ADJUSTMENT</option>
          <option value="TRANSFER">TRANSFER</option>
        </select>
        <input type="date" formControlName="startDate" />
        <input type="date" formControlName="endDate" />
        <input type="number" step="0.01" placeholder="Min Amount" formControlName="minAmount" />
        <input type="number" step="0.01" placeholder="Max Amount" formControlName="maxAmount" />
        <input type="text" placeholder="Description" formControlName="description" />
        <button type="submit">Search</button>
      </form>

      <table class="data-table" *ngIf="rows.length">
        <thead>
          <tr>
            <th>Date</th>
            <th>Type</th>
            <th>Amount</th>
            <th>Description</th>
            <th>Customer</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let r of rows">
            <td>{{ r.transactionDate }}</td>
            <td>{{ r.transactionType }}</td>
            <td>{{ r.amount | number:'1.2-2' }}</td>
            <td>{{ r.description }}</td>
            <td>{{ r.customerName }}</td>
          </tr>
        </tbody>
      </table>
      <p *ngIf="!rows.length">No results.</p>
    </div>
  `,
  styles: [`
    .filters { display: grid; grid-template-columns: repeat(7, 1fr); gap: 8px; margin: 12px 0; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th, .data-table td { border: 1px solid #eee; padding: 8px; }
  `]
})
export class LedgerSearchComponent {
  private fb = inject(FormBuilder);
  private ledgerService = inject(LedgerService);

  rows: any[] = [];

  form = this.fb.group({
    customerId: [null as number | null],
    transactionType: [''],
    startDate: [''],
    endDate: [''],
    minAmount: [null as number | null],
    maxAmount: [null as number | null],
    description: ['']
  });

  search() {
    const query = Object.fromEntries(Object.entries(this.form.value as any).filter(([_, v]) => v !== null && v !== ''));
    this.ledgerService.searchEntries(query).subscribe((res) => {
      this.rows = res?.entries || [];
    });
  }
}


