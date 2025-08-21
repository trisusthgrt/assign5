// Create this component to manage shops
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { OwnerService } from '../../../core/services/owner.service';
import { Shop } from '../../../core/models/shop.model';

@Component({
  selector: 'app-shop-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="shop-management">
      <div class="management-card">
        <div class="card-header">
          <div class="header-content">
            <div class="title-section">
              <svg class="header-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
              </svg>
              <h1 class="page-title">Shop Management</h1>
            </div>
            <button class="btn btn-primary" (click)="toggleForm()">
              <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              {{ showForm ? 'Cancel' : 'Add Shop' }}
            </button>
          </div>
        </div>

        <!-- Add Shop Form -->
        <div *ngIf="showForm" class="form-section">
          <div class="form-card">
            <h3 class="form-title">Add New Shop</h3>
            <form [formGroup]="shopForm" (ngSubmit)="onSubmit()" class="modern-form">
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">Shop Name</label>
                  <input 
                    type="text" 
                    formControlName="name" 
                    class="form-input"
                    placeholder="Enter shop name"
                    [class.error]="shopForm.get('name')?.invalid && shopForm.get('name')?.touched"
                  >
                  <div class="error-message" *ngIf="shopForm.get('name')?.invalid && shopForm.get('name')?.touched">
                    Shop name is required
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">Description</label>
                  <input 
                    type="text" 
                    formControlName="description" 
                    class="form-input"
                    placeholder="Enter shop description"
                  >
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">Address</label>
                  <input 
                    type="text" 
                    formControlName="address" 
                    class="form-input"
                    placeholder="Enter shop address"
                    [class.error]="shopForm.get('address')?.invalid && shopForm.get('address')?.touched"
                  >
                  <div class="error-message" *ngIf="shopForm.get('address')?.invalid && shopForm.get('address')?.touched">
                    Address is required
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">Phone Number</label>
                  <input 
                    type="tel" 
                    formControlName="phoneNumber" 
                    class="form-input"
                    placeholder="Enter 10-digit phone number"
                    [class.error]="shopForm.get('phoneNumber')?.invalid && shopForm.get('phoneNumber')?.touched"
                  >
                  <div class="error-message" *ngIf="shopForm.get('phoneNumber')?.invalid && shopForm.get('phoneNumber')?.touched">
                    Please enter a valid 10-digit phone number
                  </div>
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">Email</label>
                  <input 
                    type="email" 
                    formControlName="email" 
                    class="form-input"
                    placeholder="Enter shop email"
                    [class.error]="shopForm.get('email')?.invalid && shopForm.get('email')?.touched"
                  >
                  <div class="error-message" *ngIf="shopForm.get('email')?.invalid && shopForm.get('email')?.touched">
                    Please enter a valid email address
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">City</label>
                  <input 
                    type="text" 
                    formControlName="city" 
                    class="form-input"
                    placeholder="Enter city"
                  >
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">State</label>
                  <input 
                    type="text" 
                    formControlName="state" 
                    class="form-input"
                    placeholder="Enter state"
                  >
                </div>
                <div class="form-group">
                  <label class="form-label">Pincode</label>
                  <input 
                    type="text" 
                    formControlName="pincode" 
                    class="form-input"
                    placeholder="Enter pincode"
                  >
                </div>
              </div>
              <div class="form-actions">
                <button type="submit" class="btn btn-primary" [disabled]="shopForm.invalid">
                  <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                  </svg>
                  Save Shop
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Shops Table -->
        <div class="table-section">
          <div class="table-card">
            <div class="table-header">
              <h3 class="table-title">Current Shops</h3>
              <div class="table-stats">
                <span class="stat-badge">{{ shops.length }} shop{{ shops.length !== 1 ? 's' : '' }}</span>
              </div>
            </div>
            
            <div class="table-container">
              <table class="modern-table">
                <thead>
                  <tr>
                    <th>Shop Name</th>
                    <th>Address</th>
                    <th>Phone</th>
                    <th>Email</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let shop of shops" class="table-row">
                    <td class="shop-name-cell">
                      <div class="shop-info">
                        <div class="shop-avatar">
                          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                          </svg>
                        </div>
                        <div class="shop-details">
                          <span class="shop-name">{{ shop.name }}</span>
                          <span class="shop-description" *ngIf="shop.description">{{ shop.description }}</span>
                        </div>
                      </div>
                    </td>
                    <td class="address-cell">
                      <div class="address-info">
                        <div class="address-text">{{ shop.address }}</div>
                        <div class="location-info" *ngIf="shop.city || shop.state">
                          {{ shop.city }}{{ shop.city && shop.state ? ', ' : '' }}{{ shop.state }}
                          <span *ngIf="shop.pincode"> - {{ shop.pincode }}</span>
                        </div>
                      </div>
                    </td>
                    <td class="phone-cell">{{ shop.phoneNumber || 'N/A' }}</td>
                    <td class="email-cell">{{ shop.email || 'N/A' }}</td>
                    <td class="actions-cell">
                      <button class="btn btn-icon btn-secondary" (click)="openEditModal(shop)" title="Edit Shop">
                        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                        </svg>
                      </button>
                      <button class="btn btn-icon btn-danger" (click)="deleteShop(shop.id)" title="Delete Shop">
                        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                        </svg>
                      </button>
                    </td>
                  </tr>
                  <tr *ngIf="shops.length === 0" class="empty-row">
                    <td colspan="5" class="empty-message">
                      <div class="empty-state">
                        <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                        </svg>
                        <h4>No shops found</h4>
                        <p>Add your first shop to start managing your business locations.</p>
                        <button class="btn btn-primary" (click)="toggleForm()">
                          <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                          </svg>
                          Add Shop
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Shop Modal -->
    <div *ngIf="isEditModalVisible" class="modal-overlay" (click)="closeEditModal()">
      <div class="modal-content" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3 class="modal-title">
            <svg class="modal-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
            </svg>
            Edit Shop: {{ currentEditingShop?.name }}
          </h3>
          <button class="modal-close" (click)="closeEditModal()">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <form [formGroup]="editShopForm" (ngSubmit)="onUpdateSubmit()" class="modal-form">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Shop Name</label>
              <input type="text" formControlName="name" class="form-input" placeholder="Enter shop name">
            </div>
            <div class="form-group">
              <label class="form-label">Description</label>
              <input type="text" formControlName="description" class="form-input" placeholder="Enter description">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Address</label>
              <input type="text" formControlName="address" class="form-input" placeholder="Enter address">
            </div>
            <div class="form-group">
              <label class="form-label">Phone</label>
              <input type="tel" formControlName="phoneNumber" class="form-input" placeholder="Enter 10-digit phone">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Email</label>
              <input type="email" formControlName="email" class="form-input" placeholder="Enter email">
            </div>
            <div class="form-group">
              <label class="form-label">City</label>
              <input type="text" formControlName="city" class="form-input" placeholder="Enter city">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">State</label>
              <input type="text" formControlName="state" class="form-input" placeholder="Enter state">
            </div>
            <div class="form-group">
              <label class="form-label">Pincode</label>
              <input type="text" formControlName="pincode" class="form-input" placeholder="Enter pincode">
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" (click)="closeEditModal()">Cancel</button>
            <button type="submit" class="btn btn-primary" [disabled]="editShopForm.invalid">Save Changes</button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .shop-management {
      padding: var(--spacing-6);
      min-height: 100vh;
      background: var(--gray-50);
    }

    .management-card {
      background: var(--white);
      border-radius: var(--radius-2xl);
      box-shadow: var(--shadow-lg);
      overflow: hidden;
    }

    .card-header {
      background: linear-gradient(135deg, var(--warning-color) 0%, var(--warning-hover) 100%);
      padding: var(--spacing-8);
      color: var(--white);
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .title-section {
      display: flex;
      align-items: center;
      gap: var(--spacing-4);
    }

    .header-icon {
      width: 32px;
      height: 32px;
      color: var(--white);
    }

    .page-title {
      font-size: var(--font-size-3xl);
      font-weight: 700;
      margin: 0;
      color: var(--white);
    }

    .form-section {
      padding: var(--spacing-6);
      border-bottom: 1px solid var(--gray-200);
    }

    .form-card {
      background: var(--gray-50);
      border-radius: var(--radius-xl);
      padding: var(--spacing-6);
      border: 1px solid var(--gray-200);
    }

    .form-title {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0 0 var(--spacing-6) 0;
    }

    .modern-form {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-6);
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: var(--spacing-6);
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-2);
    }

    .form-label {
      font-size: var(--font-size-sm);
      font-weight: 600;
      color: var(--gray-700);
    }

    .form-input {
      padding: var(--spacing-3);
      border: 2px solid var(--gray-200);
      border-radius: var(--radius-lg);
      font-size: var(--font-size-base);
      transition: all var(--transition-fast);
      background: var(--white);
    }

    .form-input:focus {
      outline: none;
      border-color: var(--warning-color);
      box-shadow: 0 0 0 3px var(--warning-light);
    }

    .form-input.error {
      border-color: var(--danger-color);
    }

    .error-message {
      color: var(--danger-color);
      font-size: var(--font-size-sm);
      display: flex;
      align-items: center;
      gap: var(--spacing-2);
    }

    .error-message::before {
      content: "⚠";
      font-size: var(--font-size-sm);
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      padding-top: var(--spacing-4);
    }

    .table-section {
      padding: var(--spacing-6);
    }

    .table-card {
      background: var(--white);
      border-radius: var(--radius-xl);
      border: 1px solid var(--gray-200);
      overflow: hidden;
    }

    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: var(--spacing-6);
      border-bottom: 1px solid var(--gray-200);
      background: var(--gray-50);
    }

    .table-title {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0;
    }

    .table-stats {
      display: flex;
      gap: var(--spacing-3);
    }

    .stat-badge {
      background: var(--warning-color);
      color: var(--white);
      padding: var(--spacing-2) var(--spacing-3);
      border-radius: var(--radius-full);
      font-size: var(--font-size-sm);
      font-weight: 600;
    }

    .table-container {
      overflow-x: auto;
    }

    .modern-table {
      width: 100%;
      border-collapse: collapse;
    }

    .modern-table th {
      background: var(--gray-50);
      padding: var(--spacing-4);
      text-align: left;
      font-weight: 600;
      color: var(--gray-700);
      border-bottom: 1px solid var(--gray-200);
      font-size: var(--font-size-sm);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .modern-table td {
      padding: var(--spacing-4);
      border-bottom: 1px solid var(--gray-100);
      vertical-align: middle;
    }

    .table-row:hover {
      background: var(--gray-50);
    }

    .shop-name-cell {
      min-width: 250px;
    }

    .shop-info {
      display: flex;
      align-items: center;
      gap: var(--spacing-3);
    }

    .shop-avatar {
      width: 40px;
      height: 40px;
      background: var(--warning-light);
      border-radius: var(--radius-lg);
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--warning-color);
    }

    .shop-avatar svg {
      width: 20px;
      height: 20px;
    }

    .shop-details {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-1);
    }

    .shop-name {
      font-weight: 600;
      color: var(--gray-900);
    }

    .shop-description {
      font-size: var(--font-size-sm);
      color: var(--gray-600);
    }

    .address-cell {
      min-width: 200px;
    }

    .address-info {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-1);
    }

    .address-text {
      font-weight: 500;
      color: var(--gray-900);
    }

    .location-info {
      font-size: var(--font-size-sm);
      color: var(--gray-600);
    }

    .phone-cell {
      color: var(--gray-700);
      font-family: monospace;
    }

    .email-cell {
      color: var(--gray-700);
      font-family: monospace;
    }

    .actions-cell {
      display: flex;
      gap: var(--spacing-2);
      justify-content: center;
    }

    .empty-row {
      height: 200px;
    }

    .empty-message {
      text-align: center;
      padding: var(--spacing-8);
    }

    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: var(--spacing-4);
    }

    .empty-icon {
      width: 64px;
      height: 64px;
      color: var(--gray-400);
    }

    .empty-state h4 {
      font-size: var(--font-size-lg);
      font-weight: 600;
      color: var(--gray-700);
      margin: 0;
    }

    .empty-state p {
      color: var(--gray-600);
      margin: 0;
      text-align: center;
      max-width: 400px;
    }

    /* Button Styles */
    .btn {
      display: inline-flex;
      align-items: center;
      gap: var(--spacing-2);
      padding: var(--spacing-3) var(--spacing-4);
      border: none;
      border-radius: var(--radius-lg);
      font-size: var(--font-size-sm);
      font-weight: 600;
      cursor: pointer;
      transition: all var(--transition-fast);
      text-decoration: none;
      justify-content: center;
    }

    .btn-primary {
      background: linear-gradient(135deg, var(--warning-color), var(--warning-hover));
      color: var(--white);
    }

    .btn-primary:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: var(--shadow-lg);
    }

    .btn-danger {
      background: var(--danger-color);
      color: var(--white);
    }

    .btn-danger:hover {
      background: var(--danger-hover);
    }

    .btn-icon {
      padding: var(--spacing-2);
      min-width: 40px;
      height: 40px;
    }

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }

    .btn-icon svg {
      width: 18px;
      height: 18px;
    }

    /* Modal Styles */
    .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display:flex; align-items:center; justify-content:center; z-index:1000; backdrop-filter: blur(4px);} 
    .modal-content { background: var(--white); border-radius: var(--radius-2xl); box-shadow: var(--shadow-2xl); width: 90%; max-width: 700px; max-height: 90vh; overflow-y: auto; animation: slideUp 0.3s ease-out;} 
    @keyframes slideUp { from { opacity:0; transform: translateY(20px);} to { opacity:1; transform: translateY(0);} } 
    .modal-header { display:flex; justify-content: space-between; align-items:center; padding: var(--spacing-6); border-bottom: 1px solid var(--gray-200);} 
    .modal-title { font-size: var(--font-size-xl); font-weight:600; color: var(--gray-900); margin:0; display:flex; align-items:center; gap: var(--spacing-3);} 
    .modal-icon { width: 24px; height: 24px; color: var(--warning-color);} 
    .modal-close { background:none; border:none; color: var(--gray-400); cursor:pointer; padding: var(--spacing-2); border-radius: var(--radius-lg);} 
    .modal-close:hover { background: var(--gray-100); color: var(--gray-600);} 
    .modal-form { padding: var(--spacing-6);} 
    .modal-actions { display:flex; justify-content:flex-end; gap: var(--spacing-4); padding-top: var(--spacing-6); border-top: 1px solid var(--gray-200);} 

    /* Responsive Design */
    @media (max-width: 768px) {
      .shop-management {
        padding: var(--spacing-4);
      }

      .form-row {
        grid-template-columns: 1fr;
      }

      .header-content {
        flex-direction: column;
        gap: var(--spacing-4);
        text-align: center;
      }

      .page-title {
        font-size: var(--font-size-2xl);
      }

      .table-container {
        overflow-x: auto;
      }

      .modern-table th,
      .modern-table td {
        padding: var(--spacing-3);
        font-size: var(--font-size-sm);
      }

      .actions-cell {
        flex-direction: column;
        gap: var(--spacing-1);
      }
    }
  `]
})
export class ShopManagementComponent implements OnInit {
  ownerService = inject(OwnerService);
  fb = inject(FormBuilder);

  shops: Shop[] = [];
  showForm = false;
  isEditModalVisible = false;
  currentEditingShop: Shop | null = null;

  shopForm = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    address: ['', Validators.required],
    phoneNumber: ['', [Validators.pattern(/^\d{10}$/)]],
    email: ['', [Validators.pattern(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)]],
    city: [''],
    state: [''],
    pincode: ['']
  });

  editShopForm = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    address: ['', Validators.required],
    phoneNumber: ['', [Validators.pattern(/^\d{10}$/)]],
    email: ['', [Validators.pattern(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)]],
    city: [''],
    state: [''],
    pincode: ['']
  });

  ngOnInit(): void {
    this.loadShops();
  }

  loadShops(): void {
    this.ownerService.getMyShops().subscribe(response => {
      this.shops = response.shops;
    });
  }

  deleteShop(id: number): void {
    if (confirm('Are you sure you want to delete this shop?')) {
      this.ownerService.deleteShop(id).subscribe(() => {
        this.loadShops();
      });
    }
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    this.shopForm.reset();
  }

  onSubmit(): void {
    if (this.shopForm.invalid) {
      return;
    }
    this.ownerService.createShop(this.shopForm.value).subscribe(() => {
      this.loadShops();
      this.toggleForm();
    });
  }

  openEditModal(shop: Shop): void {
    this.currentEditingShop = shop;
    this.editShopForm.setValue({
      name: shop.name || '',
      description: (shop as any).description || '',
      address: shop.address || '',
      phoneNumber: shop.phoneNumber || '',
      email: shop.email || '',
      city: (shop as any).city || '',
      state: (shop as any).state || '',
      pincode: (shop as any).pincode || ''
    });
    this.isEditModalVisible = true;
  }

  closeEditModal(): void {
    this.isEditModalVisible = false;
    this.currentEditingShop = null;
    this.editShopForm.reset();
  }

  onUpdateSubmit(): void {
    if (this.editShopForm.invalid || !this.currentEditingShop) return;
    this.ownerService.updateShop((this.currentEditingShop as any).id, this.editShopForm.value as any)
      .subscribe(() => {
        this.loadShops();
        this.closeEditModal();
      });
  }
}