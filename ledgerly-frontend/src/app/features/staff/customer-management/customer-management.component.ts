import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-customer-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatSnackBarModule],
  template: `
    <div class="customer-management">
      <!-- Header -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">Customer Management</h1>
          <p class="page-subtitle">Manage your customer database and information</p>
        </div>
        <button class="btn btn-primary" (click)="showAddForm = true">
          <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
          Add Customer
        </button>
      </div>

      <!-- Add Customer Form -->
      <div class="form-section" *ngIf="showAddForm">
        <div class="form-card">
          <div class="form-header">
            <h2>Add New Customer</h2>
            <button class="close-btn" (click)="showAddForm = false">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <form [formGroup]="customerForm" (ngSubmit)="onSubmit()" class="customer-form">
            <div class="form-row">
              <div class="form-group">
                <label for="name" class="form-label">Full Name *</label>
                <input 
                  type="text" 
                  id="name" 
                  formControlName="name" 
                  class="form-input"
                  placeholder="Enter customer's full name"
                  [class.error]="customerForm.get('name')?.invalid && customerForm.get('name')?.touched"
                />
                <div class="error-message" *ngIf="customerForm.get('name')?.invalid && customerForm.get('name')?.touched">
                  <div *ngIf="customerForm.get('name')?.errors?.['required']">Full name is required</div>
                  <div *ngIf="customerForm.get('name')?.errors?.['minlength']">Name must be at least 2 characters</div>
                </div>
              </div>

              <div class="form-group">
                <label for="email" class="form-label">Email *</label>
                <input 
                  type="email" 
                  id="email" 
                  formControlName="email" 
                  class="form-input"
                  placeholder="Enter customer's email"
                  [class.error]="customerForm.get('email')?.invalid && customerForm.get('email')?.touched"
                />
                <div class="error-message" *ngIf="customerForm.get('email')?.invalid && customerForm.get('email')?.touched">
                  <div *ngIf="customerForm.get('email')?.errors?.['required']">Email is required</div>
                  <div *ngIf="customerForm.get('email')?.errors?.['pattern']">Please enter a valid email address</div>
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="phoneNumber" class="form-label">Phone Number *</label>
                <input 
                  type="tel" 
                  id="phoneNumber" 
                  formControlName="phoneNumber" 
                  class="form-input"
                  placeholder="Enter 10-digit phone number"
                  inputmode="numeric"
                  maxlength="10"
                  [class.error]="customerForm.get('phoneNumber')?.invalid && customerForm.get('phoneNumber')?.touched"
                />
                <div class="error-message" *ngIf="customerForm.get('phoneNumber')?.invalid && customerForm.get('phoneNumber')?.touched">
                  <div *ngIf="customerForm.get('phoneNumber')?.errors?.['required']">Phone number is required</div>
                  <div *ngIf="customerForm.get('phoneNumber')?.errors?.['pattern']">Phone number must be exactly 10 digits</div>
                </div>
              </div>

              <div class="form-group">
                <label for="businessName" class="form-label">Business Name</label>
                <input 
                  type="text" 
                  id="businessName" 
                  formControlName="businessName" 
                  class="form-input"
                  placeholder="Enter business name (optional)"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="creditLimit" class="form-label">Credit Limit</label>
                <input 
                  type="number" 
                  id="creditLimit" 
                  formControlName="creditLimit" 
                  class="form-input"
                  placeholder="0.00"
                  step="0.01"
                  min="0"
                />
              </div>

              <div class="form-group">
                <label for="address" class="form-label">Address</label>
                <textarea 
                  id="address" 
                  formControlName="address" 
                  class="form-input form-textarea"
                  placeholder="Enter customer's address (optional)"
                  rows="3"
                ></textarea>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="relationshipType" class="form-label">Relationship Type *</label>
                <select 
                  id="relationshipType" 
                  formControlName="relationshipType" 
                  class="form-input"
                  [class.error]="customerForm.get('relationshipType')?.invalid && customerForm.get('relationshipType')?.touched"
                >
                  <option value="CUSTOMER">Customer</option>
                  <option value="SUPPLIER">Supplier</option>
                  <option value="PARTNER">Partner</option>
                  <option value="VENDOR">Vendor</option>
                </select>
                <div class="error-message" *ngIf="customerForm.get('relationshipType')?.invalid && customerForm.get('relationshipType')?.touched">
                  Relationship type is required
                </div>
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="showAddForm = false">
                Cancel
              </button>
              <button type="submit" class="btn btn-primary" [disabled]="customerForm.invalid || isSubmitting">
                <svg *ngIf="!isSubmitting" class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                </svg>
                <svg *ngIf="isSubmitting" class="btn-icon animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                </svg>
                {{ isSubmitting ? 'Adding Customer...' : 'Add Customer' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Customer List -->
      <div class="customers-section">
        <div class="section-header">
          <h2>Customer List</h2>
          <div class="search-box">
            <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
            </svg>
            <input 
              type="text" 
              placeholder="Search customers..." 
              class="search-input"
              (input)="onSearchInput($event)"
            />
          </div>
        </div>

        <div class="table-container">
          <table class="table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Business</th>
                <th>Credit Limit</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let customer of filteredCustomers()" class="customer-row">
                <td>
                  <div class="customer-info">
                    <div class="customer-avatar">
                      {{ customer.name.charAt(0).toUpperCase() }}
                    </div>
                    <div class="customer-details">
                      <span class="customer-name">{{ customer.name }}</span>
                    </div>
                  </div>
                </td>
                <td>{{ customer.email || 'N/A' }}</td>
                <td>{{ customer.phoneNumber || 'N/A' }}</td>
                <td>{{ customer.businessName || 'N/A' }}</td>
                <td>
                  <span class="credit-limit" [class.has-credit]="customer.creditLimit > 0">
                    ₹{{ customer.creditLimit || 0 }}
                  </span>
                </td>
                <td>
                  <div class="action-buttons">
                    <button class="btn btn-sm btn-primary" [routerLink]="['/staff/customers', customer.id]">
                      Open
                    </button>
                    <button class="btn btn-sm btn-secondary" (click)="editCustomer(customer)">
                      Edit
                    </button>
                    <button class="btn btn-sm btn-danger" (click)="deleteCustomer(customer.id)">
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Empty State -->
        <div *ngIf="filteredCustomers().length === 0" class="empty-state">
          <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
          </svg>
          <h3>No customers found</h3>
          <p>Start by adding your first customer using the "Add Customer" button above.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .customer-management {
      padding: var(--spacing-6);
      max-width: 1400px;
      margin: 0 auto;
    }

    /* Page Header */
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--spacing-8);
      padding: var(--spacing-6);
      background: var(--white);
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-md);
      border: 1px solid var(--gray-200);
    }

    .page-title {
      font-size: var(--font-size-3xl);
      font-weight: 700;
      color: var(--gray-900);
      margin: 0 0 var(--spacing-2) 0;
    }

    .page-subtitle {
      font-size: var(--font-size-base);
      color: var(--gray-600);
      margin: 0;
    }

    /* Form Section */
    .form-section {
      margin-bottom: var(--spacing-8);
    }

    .form-card {
      background: var(--white);
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-lg);
      border: 1px solid var(--gray-200);
      overflow: hidden;
    }

    .form-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: var(--spacing-6);
      background: var(--gray-50);
      border-bottom: 1px solid var(--gray-200);
    }

    .form-header h2 {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0;
    }

    .close-btn {
      background: none;
      border: none;
      color: var(--gray-500);
      cursor: pointer;
      padding: var(--spacing-2);
      border-radius: var(--radius-md);
      transition: all var(--transition-fast);
    }

    .close-btn:hover {
      background: var(--gray-200);
      color: var(--gray-700);
    }

    .close-btn svg {
      width: 20px;
      height: 20px;
    }

    .customer-form {
      padding: var(--spacing-6);
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: var(--spacing-6);
      margin-bottom: var(--spacing-6);
    }

    .form-group {
      display: flex;
      flex-direction: column;
    }

    .form-label {
      display: block;
      font-size: var(--font-size-sm);
      font-weight: 600;
      color: var(--gray-700);
      margin-bottom: var(--spacing-2);
    }

    .form-input {
      width: 100%;
      padding: var(--spacing-3) var(--spacing-4);
      font-size: var(--font-size-base);
      line-height: 1.5;
      color: var(--gray-900);
      background-color: var(--white);
      border: 2px solid var(--gray-200);
      border-radius: var(--radius-lg);
      transition: all var(--transition-fast);
    }

    .form-input:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px var(--primary-light);
    }

    .form-input.error {
      border-color: var(--danger-color);
    }

    .form-textarea {
      resize: vertical;
      min-height: 6rem;
    }

    .error-message {
      color: var(--danger-color);
      font-size: var(--font-size-sm);
      margin-top: var(--spacing-2);
      padding: var(--spacing-2);
      background: #fef2f2;
      border: 1px solid #fecaca;
      border-radius: var(--radius-md);
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: var(--spacing-4);
      padding-top: var(--spacing-6);
      border-top: 1px solid var(--gray-200);
    }

    /* Customers Section */
    .customers-section {
      background: var(--white);
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-md);
      border: 1px solid var(--gray-200);
      overflow: hidden;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: var(--spacing-6);
      background: var(--gray-50);
      border-bottom: 1px solid var(--gray-200);
    }

    .section-header h2 {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0;
    }

    .search-box {
      position: relative;
      width: 300px;
    }

    .search-icon {
      position: absolute;
      left: var(--spacing-3);
      top: 50%;
      transform: translateY(-50%);
      width: 20px;
      height: 20px;
      color: var(--gray-400);
    }

    .search-input {
      width: 100%;
      padding: var(--spacing-3) var(--spacing-3) var(--spacing-3) var(--spacing-10);
      border: 2px solid var(--gray-200);
      border-radius: var(--radius-lg);
      font-size: var(--font-size-base);
      transition: all var(--transition-fast);
    }

    .search-input:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px var(--primary-light);
    }

    /* Table Styles */
    .table-container {
      overflow-x: auto;
    }

    .customer-row:hover {
      background: var(--gray-50);
    }

    .customer-info {
      display: flex;
      align-items: center;
      gap: var(--spacing-3);
    }

    .customer-avatar {
      width: 40px;
      height: 40px;
      background: var(--primary-color);
      color: var(--white);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      font-size: var(--font-size-sm);
    }

    .customer-name {
      font-weight: 600;
      color: var(--gray-900);
    }

    .credit-limit {
      font-weight: 600;
      color: var(--gray-600);
    }

    .credit-limit.has-credit {
      color: var(--success-color);
    }

    .action-buttons {
      display: flex;
      gap: var(--spacing-2);
    }

    /* Empty State */
    .empty-state {
      text-align: center;
      padding: var(--spacing-16);
      color: var(--gray-500);
    }

    .empty-icon {
      width: 64px;
      height: 64px;
      margin: 0 auto var(--spacing-4);
      color: var(--gray-300);
    }

    .empty-state h3 {
      font-size: var(--font-size-lg);
      font-weight: 600;
      color: var(--gray-700);
      margin: 0 0 var(--spacing-2) 0;
    }

    .empty-state p {
      font-size: var(--font-size-base);
      color: var(--gray-600);
      margin: 0;
    }

    /* Responsive Design */
    @media (max-width: 1024px) {
      .form-row {
        grid-template-columns: 1fr;
        gap: var(--spacing-4);
      }

      .search-box {
        width: 250px;
      }
    }

    @media (max-width: 768px) {
      .customer-management {
        padding: var(--spacing-4);
      }

      .page-header {
        flex-direction: column;
        gap: var(--spacing-4);
        text-align: center;
        padding: var(--spacing-4);
      }

      .page-title {
        font-size: var(--font-size-2xl);
      }

      .section-header {
        flex-direction: column;
        gap: var(--spacing-4);
        align-items: stretch;
      }

      .search-box {
        width: 100%;
      }

      .action-buttons {
        flex-direction: column;
        gap: var(--spacing-1);
      }

      .btn-sm {
        padding: var(--spacing-1) var(--spacing-2);
        font-size: var(--font-size-xs);
      }
    }

    @media (max-width: 480px) {
      .customer-management {
        padding: var(--spacing-3);
      }

      .form-card {
        margin: 0 calc(-1 * var(--spacing-3));
        border-radius: 0;
      }

      .customer-form {
        padding: var(--spacing-4);
      }

      .form-actions {
        flex-direction: column;
        gap: var(--spacing-3);
      }
    }
  `]
})
export class CustomerManagementComponent implements OnInit {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private snackBar = inject(MatSnackBar);

  showAddForm = false;
  isSubmitting = false;
  searchTerm = '';
  customers = signal<any[]>([]);

  customerForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.pattern(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    businessName: [''],
    creditLimit: [0, [Validators.min(0)]],
    address: [''],
    relationshipType: ['CUSTOMER', Validators.required]
  });

  filteredCustomers = signal<any[]>([]);

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.http.get<any>('http://localhost:8080/api/v1/customers').subscribe({
      next: (response) => {
        const list = Array.isArray(response)
          ? response
          : Array.isArray(response?.customers)
            ? response.customers
            : Array.isArray(response?.data)
              ? response.data
              : [];
        this.customers.set(list);
        this.filteredCustomers.set(list);
      },
      error: (error) => {
        console.error('Error loading customers:', error);
        this.snackBar.open('Failed to load customers', 'Close', { duration: 3000 });
      }
    });
  }

  onSearchInput(event: any): void {
    const value = event.target.value;
    this.searchTerm = value;
    
    if (!value) {
      this.filteredCustomers.set(this.customers());
      return;
    }

    const filtered = this.customers().filter(customer =>
      customer.name.toLowerCase().includes(value.toLowerCase()) ||
      customer.email?.toLowerCase().includes(value.toLowerCase()) ||
      customer.phoneNumber?.includes(value)
    );
    
    this.filteredCustomers.set(filtered);
  }

  onSubmit(): void {
    if (this.customerForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    const customerData = this.customerForm.value;

    this.http.post('http://localhost:8080/api/v1/customers', customerData).subscribe({
      next: (response: any) => {
        this.isSubmitting = false;
        this.snackBar.open('Customer added successfully!', 'Close', { duration: 3000 });
        this.customerForm.reset();
        this.showAddForm = false;
        this.loadCustomers();
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Error adding customer:', error);
        
        // Try to get more detailed error information
        let errorMessage = 'Failed to add customer';
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        this.snackBar.open(errorMessage, 'Close', { duration: 5000 });
      }
    });
  }

  editCustomer(customer: any): void {
    // Implement edit functionality
    console.log('Edit customer:', customer);
  }

  deleteCustomer(customerId: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.http.delete(`http://localhost:8080/api/v1/customers/${customerId}`).subscribe({
        next: () => {
          this.snackBar.open('Customer deleted successfully!', 'Close', { duration: 3000 });
          this.loadCustomers();
        },
        error: (error) => {
          console.error('Error deleting customer:', error);
          this.snackBar.open('Failed to delete customer', 'Close', { duration: 3000 });
        }
      });
    }
  }
}