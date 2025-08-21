import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-owner-purchases',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatSnackBarModule],
  template: `
    <div class="purchases">
      <h2>Record Customer Purchase</h2>
      <p class="description">Record purchases or services that customers owe money for (creates DEBIT entries)</p>
      
      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form">
        <div class="form-row">
          <div class="form-group">
            <label for="customerId">Customer ID *</label>
            <input type="number" id="customerId" formControlName="customerId" placeholder="Enter customer ID" />
            <div *ngIf="form.get('customerId')?.invalid && form.get('customerId')?.touched" class="error-message">
              Customer ID is required
            </div>
          </div>

          <div class="form-group">
            <label for="shopId">Shop ID *</label>
            <input type="number" id="shopId" formControlName="shopId" placeholder="Enter shop ID" />
            <div *ngIf="form.get('shopId')?.invalid && form.get('shopId')?.touched" class="error-message">
              Shop ID is required
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="amount">Amount *</label>
          <input type="number" id="amount" formControlName="amount" step="0.01" min="0.01" placeholder="0.00" />
          <div *ngIf="form.get('amount')?.invalid && form.get('amount')?.touched" class="error-message">
            <div *ngIf="form.get('amount')?.errors?.['required']">Amount is required</div>
            <div *ngIf="form.get('amount')?.errors?.['min']">Amount must be greater than 0</div>
          </div>
        </div>

        <div class="form-group">
          <label for="description">Description *</label>
          <input type="text" id="description" formControlName="description" placeholder="e.g., Product purchase, Service charge" />
          <div *ngIf="form.get('description')?.invalid && form.get('description')?.touched" class="error-message">
            Description is required
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="category">Category</label>
            <select id="category" formControlName="category">
              <option value="PRODUCT">Product</option>
              <option value="SERVICE">Service</option>
              <option value="RENTAL">Rental</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          <div class="form-group">
            <label for="transactionDate">Transaction Date *</label>
            <input type="date" id="transactionDate" formControlName="transactionDate" />
            <div *ngIf="form.get('transactionDate')?.invalid && form.get('transactionDate')?.touched" class="error-message">
              Transaction date is required
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="notes">Notes</label>
          <textarea id="notes" formControlName="notes" rows="3" placeholder="Additional details about the purchase..."></textarea>
        </div>

        <button type="submit" [disabled]="form.invalid || isSubmitting" class="submit-btn">
          {{ isSubmitting ? 'Recording Purchase...' : 'Record Purchase' }}
        </button>
      </form>

      <div *ngIf="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div *ngIf="successMessage" class="success-message">{{ successMessage }}</div>
    </div>
  `,
  styles: [`
    .purchases {
      padding: 24px;
      max-width: 800px;
      margin: 0 auto;
    }

    h2 {
      color: #333;
      margin-bottom: 8px;
    }

    .description {
      color: #666;
      margin-bottom: 24px;
      font-style: italic;
    }

    .form {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }

    .form-group {
      display: flex;
      flex-direction: column;
    }

    label {
      font-weight: 600;
      margin-bottom: 8px;
      color: #333;
    }

    input, select, textarea {
      padding: 12px;
      border: 1px solid #ddd;
      border-radius: 6px;
      font-size: 14px;
      transition: border-color 0.3s;
    }

    input:focus, select:focus, textarea:focus {
      outline: none;
      border-color: #007bff;
      box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
    }

    textarea {
      resize: vertical;
      min-height: 80px;
    }

    .submit-btn {
      padding: 14px 28px;
      background-color: #dc3545;
      color: white;
      border: none;
      border-radius: 6px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: background-color 0.3s;
      margin-top: 20px;
    }

    .submit-btn:hover:not(:disabled) {
      background-color: #c82333;
    }

    .submit-btn:disabled {
      background-color: #6c757d;
      cursor: not-allowed;
    }

    .error-message {
      color: #dc3545;
      font-size: 14px;
      margin-top: 8px;
      padding: 8px;
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      border-radius: 4px;
    }

    .success-message {
      color: #28a745;
      font-size: 14px;
      margin-top: 8px;
      padding: 8px;
      background-color: #d4edda;
      border: 1px solid #c3e6cb;
      border-radius: 4px;
    }

    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class PurchasesComponent {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private snackBar = inject(MatSnackBar);

  errorMessage: string | null = null;
  successMessage: string | null = null;
  isSubmitting = false;

  form = this.fb.group({
    customerId: [null as number | null, [Validators.required, Validators.min(1)]],
    shopId: [null as number | null, [Validators.required, Validators.min(1)]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    description: ['', [Validators.required, Validators.minLength(3)]],
    category: ['PRODUCT'], // Optional since backend doesn't store it
    transactionDate: [new Date().toISOString().slice(0, 10), Validators.required],
    notes: ['']
  });

  onSubmit(): void {
    if (this.form.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = null;
    this.successMessage = null;

    const payload = {
      customerId: this.form.value.customerId,
      shopId: this.form.value.shopId,
      amount: this.form.value.amount,
      description: this.form.value.description,
      transactionDate: this.form.value.transactionDate,
      notes: this.form.value.notes || ''
    };

    this.http.post('http://localhost:8080/api/v1/ledger/debit', payload).subscribe({
      next: (response: any) => {
        this.isSubmitting = false;
        if (response.success) {
          this.successMessage = 'Purchase recorded successfully! DEBIT entry created.';
          this.snackBar.open('Purchase recorded! DEBIT entry created.', 'Close', { duration: 3000 });
          
          // Clear form
          this.form.reset({
            category: 'PRODUCT',
            transactionDate: new Date().toISOString().slice(0, 10)
          });
        } else {
          this.errorMessage = response.message || 'Failed to record purchase';
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else if (err.status === 400) {
          this.errorMessage = 'Invalid request data. Please check your inputs.';
        } else if (err.status === 404) {
          this.errorMessage = 'Customer or Shop not found. Please check the IDs.';
        } else {
          this.errorMessage = 'An error occurred while recording the purchase.';
        }
        console.error('Purchase recording error:', err);
      }
    });
  }
}
