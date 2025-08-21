import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-exports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSnackBarModule],
  template: `
    <div class="exports">
      <h2>Export Customer Statements</h2>
      <form [formGroup]="form" (ngSubmit)="exportStatement('pdf')" class="form">
        <label>Customer ID</label>
        <input type="number" formControlName="customerId" />

        <label>Start Date</label>
        <input type="date" formControlName="startDate" />

        <label>End Date</label>
        <input type="date" formControlName="endDate" />

        <div class="actions">
          <button type="button" (click)="exportStatement('pdf')" [disabled]="form.invalid">Export PDF</button>
          <button type="button" (click)="exportStatement('csv')" [disabled]="form.invalid">Export CSV</button>
          <button type="button" (click)="exportCurrentMonth('pdf')">Current Month PDF</button>
          <button type="button" (click)="exportCurrentMonth('csv')">Current Month CSV</button>
        </div>
      </form>

      <h3 style="margin-top:24px">Export Full History</h3>
      <div class="actions">
        <input type="number" [value]="form.value.customerId || ''" placeholder="Customer ID" (input)="syncCustomerId($event)" />
        <button type="button" (click)="exportHistory('pdf')" [disabled]="!form.value.customerId">History PDF</button>
        <button type="button" (click)="exportHistory('csv')" [disabled]="!form.value.customerId">History CSV</button>
      </div>
    </div>
  `,
  styles: [`
    .exports { padding: 24px; max-width: 640px; }
    .form { display: grid; gap: 12px; }
    .actions { display: flex; gap: 10px; flex-wrap: wrap; }
    label { font-weight: 600; }
    input { padding: 8px; }
  `]
})
export class ExportsComponent {
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);

  private baseUrl = 'http://localhost:8080/api/v1/export';

  form = this.fb.group({
    customerId: [null as number | null, Validators.required],
    startDate: [new Date().toISOString().slice(0,10), Validators.required],
    endDate: [new Date().toISOString().slice(0,10), Validators.required]
  });

  private download(blob: Blob, filename: string) {
    const link = document.createElement('a');
    const url = window.URL.createObjectURL(blob);
    link.href = url; link.download = filename; link.click();
    window.URL.revokeObjectURL(url);
  }

  exportStatement(type: 'pdf'|'csv') {
    if (this.form.invalid) return;
    const { customerId, startDate, endDate } = this.form.value as any;
    const url = `${this.baseUrl}/customers/${customerId}/statement/${type}?startDate=${startDate}&endDate=${endDate}`;
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: (blob) => this.download(blob, `customer_statement_${customerId}_${startDate}_to_${endDate}.${type}`),
      error: () => this.snackBar.open('Export failed', 'Close', { duration: 3000 })
    });
  }

  exportCurrentMonth(type: 'pdf'|'csv') {
    const customerId = (this.form.value as any).customerId;
    if (!customerId) return;
    const url = `${this.baseUrl}/customers/${customerId}/statement/current-month/${type}`;
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: (blob) => this.download(blob, `customer_statement_${customerId}_current_month.${type}`),
      error: () => this.snackBar.open('Export failed', 'Close', { duration: 3000 })
    });
  }

  exportHistory(type: 'pdf'|'csv') {
    const customerId = (this.form.value as any).customerId;
    if (!customerId) return;
    const url = `${this.baseUrl}/customers/${customerId}/history/${type}`;
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: (blob) => this.download(blob, `customer_history_${customerId}.${type}`),
      error: () => this.snackBar.open('Export failed', 'Close', { duration: 3000 })
    });
  }

  syncCustomerId(evt: Event) {
    const n = Number((evt.target as HTMLInputElement).value);
    this.form.patchValue({ customerId: (isNaN(n) ? null : (n as number | null)) });
  }
}
