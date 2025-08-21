import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../../core/services/payment.service';

@Component({
  selector: 'app-owner-payment-status-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="payments-status">
      <h2>Payments Status</h2>
      <div class="kpis" *ngIf="summary() as s">
        <div class="kpi"><div class="label">Total</div><div class="value">{{ s.totalPayments }}</div></div>
        <div class="kpi"><div class="label">Problematic</div><div class="value">{{ s.problematicPayments }}</div></div>
        <div class="kpi" *ngFor="let k of statusKeys">
          <div class="label">{{ k }}</div>
          <div class="value">{{ s.statusCounts[k] || 0 }}</div>
        </div>
      </div>

      <h3>Overdue</h3>
      <ul>
        <li *ngFor="let p of overdue()">#{{ p.id }} • {{ p.customerName }} • {{ p.amount | number:'1.2-2' }} • overdue {{ p.overdueDays }}d</li>
      </ul>

      <h3>Disputed</h3>
      <ul>
        <li *ngFor="let p of disputed()">#{{ p.id }} • {{ p.customerName }} • {{ p.amount | number:'1.2-2' }} • {{ p.disputeReason }}</li>
      </ul>
    </div>
  `,
  styles: [`
    .kpis { display: grid; grid-template-columns: repeat(6, 1fr); gap: 10px; margin: 12px 0; }
    .kpi { background: #f7f7f7; border-radius: 8px; padding: 10px; }
    .label { font-size: 12px; color: #666; }
    .value { font-size: 18px; font-weight: 700; }
  `]
})
export class PaymentStatusDashboardComponent implements OnInit {
  private paymentService = inject(PaymentService);

  summary = signal<any>(null);
  overdue = signal<any[]>([]);
  disputed = signal<any[]>([]);

  statusKeys = ['PENDING', 'PAID', 'OVERDUE', 'DISPUTED'];

  ngOnInit(): void {
    this.paymentService.getStatusSummary().subscribe((res) => { if (res?.success) this.summary.set(res.summary); });
    this.paymentService.getOverdue().subscribe((res) => { if (res?.success) this.overdue.set(res.overduePayments || []); });
    this.paymentService.getDisputed().subscribe((res) => { if (res?.success) this.disputed.set(res.disputedPayments || []); });
  }
}

