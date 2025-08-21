import { Component, OnInit, inject, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, FormsModule, FormControl } from '@angular/forms';
import { LedgerService } from '../../../../core/services/ledger.service';
import { LedgerEntry, PagedLedgerEntriesResponse } from '../../../../core/models/ledger.model';

@Component({
    selector: 'app-ledger-tab',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule],
    template: `
    <div class="ledger-tab">
      <h3>Ledger</h3>

      <form [formGroup]="entryForm" (ngSubmit)="addEntry()" class="entry-form">
        <select formControlName="transactionType">
          <option value="CREDIT">CREDIT</option>
          <option value="DEBIT">DEBIT</option>
          <option value="OPENING_BALANCE">OPENING_BALANCE</option>
          <option value="ADJUSTMENT">ADJUSTMENT</option>
          <option value="TRANSFER">TRANSFER</option>
        </select>
        <input type="date" formControlName="transactionDate" />
        <input type="number" step="0.01" formControlName="amount" placeholder="Amount" />
        <input type="text" formControlName="description" placeholder="Description" />
        <button type="submit" [disabled]="entryForm.invalid">Add</button>
      </form>

      <table class="data-table" *ngIf="entries().length > 0">
        <thead>
          <tr>
            <th>Date</th>
            <th>Type</th>
            <th>Amount</th>
            <th>Description</th>
            <th>Balance After</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let e of entries()">
            <td>{{ e.transactionDate }}</td>
            <td>{{ e.transactionType }}</td>
            <td>{{ e.amount | number:'1.2-2' }}</td>
            <td>{{ e.description }}</td>
            <td>{{ e.balanceAfterTransaction | number:'1.2-2' }}</td>
          </tr>
        </tbody>
      </table>

      <p *ngIf="entries().length === 0">No entries yet.</p>

      <h4 style="margin-top:16px">Unreconciled Entries</h4>
      <ul>
        <li *ngFor="let u of unreconciled()">{{ u.transactionDate }} • {{ u.transactionType }} • {{ u.amount | number:'1.2-2' }} • {{ u.description }}</li>
      </ul>

      <h4 style="margin-top:16px">Add Attachment to Last Entry</h4>
      <input type="file" (change)="onFile($event)" />
      <input type="text" placeholder="Description" [formControl]="attachmentDescriptionControl" />
      <button (click)="uploadAttachment()" [disabled]="!selectedFile">Upload</button>
    </div>
    `,
    styles: [`
      .entry-form { display: grid; grid-template-columns: repeat(5, 1fr) auto; gap: 8px; margin: 12px 0; }
      .data-table { width: 100%; border-collapse: collapse; }
      .data-table th, .data-table td { border: 1px solid #eee; padding: 8px; }
    `]
})
export class LedgerTabComponent implements OnInit {
    @Input() customerId!: number;
    private ledgerService = inject(LedgerService);
    private fb = inject(FormBuilder);

    entries = signal<LedgerEntry[]>([]);
    unreconciled = signal<LedgerEntry[]>([]);
    selectedFile: File | null = null;
    attachmentDescriptionControl = new FormControl<string>('');

    entryForm = this.fb.group({
        transactionType: ['CREDIT', Validators.required],
        transactionDate: [new Date().toISOString().slice(0, 10), Validators.required],
        amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
        description: ['']
    });

    ngOnInit(): void {
        this.loadEntries();
    }

    private loadEntries() {
        const id = this.customerId;
        this.ledgerService.getCustomerEntries(id, 0, 20).subscribe((res: PagedLedgerEntriesResponse) => {
            this.entries.set(res.entries || []);
        });
        this.ledgerService.getUnreconciledEntries(id).subscribe(res => {
            this.unreconciled.set(res?.entries || []);
        });
    }

    addEntry() {
        if (this.entryForm.invalid) return;
        const entry: LedgerEntry = {
            ...this.entryForm.value as any,
            customerId: this.customerId
        };
        this.ledgerService.createEntry(entry).subscribe(() => {
            this.entryForm.reset({ transactionType: 'CREDIT', transactionDate: new Date().toISOString().slice(0, 10) });
            this.loadEntries();
        });
    }

    onFile(evt: Event) {
        const files = (evt.target as HTMLInputElement).files;
        this.selectedFile = files && files.length ? files[0] : null;
    }

    uploadAttachment() {
        if (!this.selectedFile || this.entries().length === 0) return;
        const last = this.entries()[0];
        const desc = this.attachmentDescriptionControl.value || '';
        this.ledgerService.addAttachment(last.id!, this.selectedFile, desc).subscribe(() => {
            this.selectedFile = null; this.attachmentDescriptionControl.setValue('');
            this.loadEntries();
        });
    }
}


