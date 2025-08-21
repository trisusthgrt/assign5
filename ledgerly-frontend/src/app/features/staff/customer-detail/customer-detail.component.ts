import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LedgerService } from '../../../core/services/ledger.service';
import { LedgerSummary } from '../../../core/models/ledger.model';
import { LedgerTabComponent } from './tabs/ledger-tab.component';
import { PaymentsTabComponent } from './tabs/payments-tab.component';

@Component({
    selector: 'app-customer-detail',
    standalone: true,
    imports: [CommonModule, RouterLink, LedgerTabComponent, PaymentsTabComponent],
    template: `
    <div class="customer-detail">
      <a routerLink="/staff/customers">← Back to Customers</a>
      <h2>Customer Detail - ID: {{ customerId() }}</h2>

      <section class="summary" *ngIf="summary() as s">
        <div class="card"><div class="label">Total Credit</div><div class="value">{{ s.totalCredit | number:'1.2-2' }}</div></div>
        <div class="card"><div class="label">Total Debit</div><div class="value">{{ s.totalDebit | number:'1.2-2' }}</div></div>
        <div class="card"><div class="label">Current Balance</div><div class="value">{{ s.currentBalance | number:'1.2-2' }}</div></div>
        <div class="card"><div class="label">Transactions</div><div class="value">{{ s.totalTransactions || 0 }}</div></div>
      </section>

      <nav class="tabs">
        <button [class.active]="activeTab() === 'overview'" (click)="setTab('overview')">Overview</button>
        <button [class.active]="activeTab() === 'ledger'" (click)="setTab('ledger')">Ledger</button>
        <button [class.active]="activeTab() === 'payments'" (click)="setTab('payments')">Payments</button>
      </nav>

      <section *ngIf="activeTab() === 'overview'">
        <p>Basic customer information and quick stats.</p>
      </section>

      <section *ngIf="activeTab() === 'ledger'">
        <app-ledger-tab [customerId]="customerId() || 0"></app-ledger-tab>
      </section>

      <section *ngIf="activeTab() === 'payments'">
        <app-payments-tab [customerId]="customerId()!"></app-payments-tab>
      </section>
    </div>
    `,
    styles: [`
      .customer-detail { padding: 20px; }
      .summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin: 12px 0 20px; }
      .card { background: #f7f7f7; padding: 12px; border-radius: 8px; }
      .label { font-size: 12px; color: #666; }
      .value { font-size: 18px; font-weight: 700; }
      .tabs { display: flex; gap: 8px; margin-bottom: 12px; }
      .tabs button { padding: 8px 12px; border: 1px solid #ddd; background: #fff; cursor: pointer; }
      .tabs button.active { background: #0b5ed7; color: #fff; border-color: #0b5ed7; }
    `]
})
export class CustomerDetailComponent implements OnInit {
    private route = inject(ActivatedRoute);
    private ledgerService = inject(LedgerService);

    customerId = signal<number | null>(null);
    summary = signal<LedgerSummary | null>(null);
    activeTab = signal<'overview' | 'ledger' | 'payments'>('overview');

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (!isNaN(id)) {
            this.customerId.set(id);
            this.loadSummary(id);
        }
    }

    setTab(tab: 'overview' | 'ledger' | 'payments') { this.activeTab.set(tab); }

    private loadSummary(id: number) {
        this.ledgerService.getBalanceSummary(id).subscribe(res => {
            if (res?.success) this.summary.set(res.summary);
        });
    }
}


