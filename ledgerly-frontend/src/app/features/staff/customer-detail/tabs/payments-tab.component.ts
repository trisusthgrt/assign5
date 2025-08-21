import { Component, OnInit, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PaymentService } from '../../../../core/services/payment.service';

@Component({
    selector: 'app-payments-tab',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
    <div class="payments-tab">
      <h3>Payments</h3>

      <form [formGroup]="form" (ngSubmit)="record()" class="entry-form">
        <input type="number" step="0.01" formControlName="amount" placeholder="Amount" />
        <input type="date" formControlName="paymentDate" />
        <select formControlName="paymentMethod">
          <option value="CASH">CASH</option>
          <option value="BANK_TRANSFER">BANK_TRANSFER</option>
          <option value="CARD">CARD</option>
          <option value="UPI">UPI</option>
        </select>
        <input type="text" formControlName="description" placeholder="Description" />
        <input type="text" formControlName="notes" placeholder="Notes" />
        <button type="submit" [disabled]="form.invalid">Record</button>
      </form>
    </div>
    `,
    styles: [`
      .entry-form { display: grid; grid-template-columns: repeat(6, 1fr) auto; gap: 8px; margin: 12px 0; }
    `]
})
export class PaymentsTabComponent implements OnInit {
    customerId = input.required<number>();
    private fb = inject(FormBuilder);
    private paymentService = inject(PaymentService);

    form = this.fb.group({
        amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
        paymentDate: [new Date().toISOString().slice(0, 10), Validators.required],
        paymentMethod: ['CASH', Validators.required],
        description: ['', Validators.required],
        notes: ['']
    });

    ngOnInit(): void {}

    record() {
        if (this.form.invalid) return;
        const payload = { ...this.form.value as any, customerId: this.customerId() };
        this.paymentService.recordPayment(payload).subscribe(() => {
            this.form.reset({ paymentDate: new Date().toISOString().slice(0, 10), paymentMethod: 'CASH' });
        });
    }
}


