import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PaymentService } from '../../../core/services/payment.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-owner-payments',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSnackBarModule],
  template: `
    <div class="payments">
      <h2>Record Payment</h2>
      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form">
        <label>Customer ID</label>
        <input type="number" formControlName="customerId" />

        <label>Amount</label>
        <input type="number" step="0.01" formControlName="amount" />

        <label>Date</label>
        <input type="date" formControlName="paymentDate" />

        <label>Method</label>
        <select formControlName="paymentMethod">
          <option value="CASH">CASH</option>
          <option value="BANK_TRANSFER">BANK_TRANSFER</option>
          <option value="CARD">CARD</option>
          <option value="UPI">UPI</option>
        </select>

        <label>Description</label>
        <input type="text" formControlName="description" placeholder="e.g. Customer payment" />

        <label>Notes</label>
        <input type="text" formControlName="notes" />

        <button type="submit" [disabled]="form.invalid">Save</button>
      </form>
    </div>
  `,
  styles: [`
    .payments { padding: 24px; max-width: 520px; }
    .form { display: grid; gap: 12px; }
    label { font-weight: 600; }
    input, select { padding: 8px; }
  `]
})
export class PaymentsComponent {
  private paymentService = inject(PaymentService);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);

  form = this.fb.group({
    customerId: [null, Validators.required],
    amount: [null, [Validators.required, Validators.min(0.01)]],
    paymentDate: [new Date().toISOString().slice(0,10), Validators.required],
    paymentMethod: ['CASH', Validators.required],
    description: ['', Validators.required],
    notes: ['']
  });

  onSubmit() {
    if (this.form.invalid) return;
    const payload = this.form.value as any;
    this.paymentService.recordPayment(payload).subscribe({
      next: () => this.snackBar.open('Payment recorded', 'Close', { duration: 3000 }),
      error: () => this.snackBar.open('Failed to record payment', 'Close', { duration: 3000 })
    });
  }
}
