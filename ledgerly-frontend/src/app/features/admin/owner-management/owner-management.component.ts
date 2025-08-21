import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { BasicUser, Role } from '../../../core/models/user.model';
import { AdminUser } from '../../../core/services/admin.service';

@Component({
  selector: 'app-owner-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="owner-management">
      <div class="management-card">
        <div class="card-header">
          <div class="header-content">
            <div class="title-section">
              <svg class="header-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
              </svg>
              <h1 class="page-title">Owner Management</h1>
            </div>
            <button class="btn btn-primary" (click)="toggleForm()">
              <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              {{ showForm ? 'Cancel' : 'Add Owner' }}
            </button>
          </div>
        </div>

        <!-- Add Owner Form -->
        <div *ngIf="showForm" class="form-section">
          <div class="form-card">
            <h3 class="form-title">Add New Owner</h3>
            <form [formGroup]="ownerForm" (ngSubmit)="onSubmit()" class="modern-form">
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">Username</label>
                  <input 
                    type="text" 
                    formControlName="username" 
                    class="form-input"
                    placeholder="Enter username"
                    [class.error]="ownerForm.get('username')?.invalid && ownerForm.get('username')?.touched"
                  >
                  <div class="error-message" *ngIf="ownerForm.get('username')?.invalid && ownerForm.get('username')?.touched">
                    Username is required
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">Email</label>
                  <input 
                    type="email" 
                    formControlName="email" 
                    class="form-input"
                    placeholder="Enter email address"
                    [class.error]="ownerForm.get('email')?.invalid && ownerForm.get('email')?.touched"
                  >
                  <div class="error-message" *ngIf="ownerForm.get('email')?.invalid && ownerForm.get('email')?.touched">
                    Please enter a valid email address
                  </div>
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">First Name</label>
                  <input 
                    type="text" 
                    formControlName="firstName" 
                    class="form-input"
                    placeholder="Enter first name"
                    [class.error]="ownerForm.get('firstName')?.invalid && ownerForm.get('firstName')?.touched"
                  >
                  <div class="error-message" *ngIf="ownerForm.get('firstName')?.invalid && ownerForm.get('firstName')?.touched">
                    First name is required
                  </div>
                </div>
                <div class="form-group">
                  <label class="form-label">Last Name</label>
                  <input 
                    type="text" 
                    formControlName="lastName" 
                    class="form-input"
                    placeholder="Enter last name"
                    [class.error]="ownerForm.get('lastName')?.invalid && ownerForm.get('lastName')?.touched"
                  >
                  <div class="error-message" *ngIf="ownerForm.get('lastName')?.invalid && ownerForm.get('lastName')?.touched">
                    Last name is required
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">Password</label>
                <input 
                  type="password" 
                  formControlName="password" 
                  class="form-input"
                  placeholder="Enter password (min 6 characters)"
                  [class.error]="ownerForm.get('password')?.invalid && ownerForm.get('password')?.touched"
                >
                <div class="error-message" *ngIf="ownerForm.get('password')?.invalid && ownerForm.get('password')?.touched">
                  Password must be at least 6 characters
                </div>
              </div>
              <div class="form-actions">
                <button type="submit" class="btn btn-primary" [disabled]="ownerForm.invalid">
                  <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                  </svg>
                  Save Owner
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Owners Table -->
        <div class="table-section">
          <div class="table-card">
            <div class="table-header">
              <h3 class="table-title">Current Owners</h3>
              <div class="table-stats">
                <span class="stat-badge">{{ owners.length }} owner{{ owners.length !== 1 ? 's' : '' }}</span>
              </div>
            </div>
            
            <div class="table-container">
              <table class="modern-table">
                <thead>
                  <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Full Name</th>
                    <th>Role</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let owner of owners" class="table-row">
                    <td class="username-cell">
                      <div class="user-info">
                        <div class="user-avatar">
                          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                          </svg>
                        </div>
                        <span class="username">{{ owner.username }}</span>
                      </div>
                    </td>
                    <td class="email-cell">{{ owner.email }}</td>
                    <td class="name-cell">{{ owner.firstName }} {{ owner.lastName }}</td>
                    <td class="role-cell">
                      <span class="role-badge owner">{{ owner.role }}</span>
                    </td>
                    <td class="actions-cell">
                      <button class="btn btn-icon btn-secondary" (click)="openEditModal(owner)" title="Edit Owner">
                        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                        </svg>
                      </button>
                      <button class="btn btn-icon btn-danger" (click)="deleteOwner(owner.id)" title="Delete Owner">
                        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                        </svg>
                      </button>
                    </td>
                  </tr>
                  <tr *ngIf="owners.length === 0" class="empty-row">
                    <td colspan="5" class="empty-message">
                      <div class="empty-state">
                        <svg class="empty-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path>
                        </svg>
                        <h4>No owners found</h4>
                        <p>Add your first owner to get started managing your business network.</p>
                        <button class="btn btn-primary" (click)="toggleForm()">
                          <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                          </svg>
                          Add Owner
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

    <!-- Edit Modal -->
    <div *ngIf="isEditModalVisible" class="modal-overlay" (click)="closeEditModal()">
      <div class="modal-content" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3 class="modal-title">
            <svg class="modal-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
            </svg>
            Edit Owner: {{ currentEditingOwner?.username }}
          </h3>
          <button class="modal-close" (click)="closeEditModal()">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
        
        <form [formGroup]="editOwnerForm" (ngSubmit)="onUpdateSubmit()" class="modal-form">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">First Name</label>
              <input 
                type="text" 
                formControlName="firstName" 
                class="form-input"
                placeholder="Enter first name"
                [class.error]="editOwnerForm.get('firstName')?.invalid && editOwnerForm.get('firstName')?.touched"
              >
              <div class="error-message" *ngIf="editOwnerForm.get('firstName')?.invalid && editOwnerForm.get('firstName')?.touched">
                First name is required
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">Last Name</label>
              <input 
                type="text" 
                formControlName="lastName" 
                class="form-input"
                placeholder="Enter last name"
                [class.error]="editOwnerForm.get('lastName')?.invalid && editOwnerForm.get('lastName')?.touched"
              >
              <div class="error-message" *ngIf="editOwnerForm.get('lastName')?.invalid && editOwnerForm.get('lastName')?.touched">
                Last name is required
              </div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">Email</label>
            <input 
              type="email" 
              formControlName="email" 
              class="form-input"
              placeholder="Enter email address"
              [class.error]="editOwnerForm.get('email')?.invalid && editOwnerForm.get('email')?.touched"
            >
            <div class="error-message" *ngIf="editOwnerForm.get('email')?.invalid && editOwnerForm.get('email')?.touched">
              Please enter a valid email address
            </div>
          </div>
          
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" (click)="closeEditModal()">Cancel</button>
            <button type="submit" class="btn btn-primary" [disabled]="editOwnerForm.invalid">
              <svg class="btn-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .owner-management {
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
      background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
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
      display: flex;
      align-items: center;
      gap: var(--spacing-3);
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
      border-color: var(--primary-color);
      box-shadow: 0 0 0 3px var(--primary-light);
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
      background: var(--primary-color);
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

    .username-cell {
      min-width: 200px;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: var(--spacing-3);
    }

    .user-avatar {
      width: 32px;
      height: 32px;
      background: var(--primary-light);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--primary-color);
    }

    .user-avatar svg {
      width: 18px;
      height: 18px;
    }

    .username {
      font-weight: 600;
      color: var(--gray-900);
    }

    .email-cell {
      color: var(--gray-700);
      font-family: monospace;
    }

    .name-cell {
      font-weight: 500;
      color: var(--gray-900);
    }

    .role-cell {
      text-align: center;
    }

    .role-badge {
      display: inline-block;
      padding: var(--spacing-1) var(--spacing-3);
      border-radius: var(--radius-full);
      font-size: var(--font-size-xs);
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .role-badge.owner {
      background: var(--success-light);
      color: var(--success-color);
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

    /* Modal Styles */
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      backdrop-filter: blur(4px);
    }

    .modal-content {
      background: var(--white);
      border-radius: var(--radius-2xl);
      box-shadow: var(--shadow-2xl);
      width: 90%;
      max-width: 600px;
      max-height: 90vh;
      overflow-y: auto;
      animation: slideUp 0.3s ease-out;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: var(--spacing-6);
      border-bottom: 1px solid var(--gray-200);
    }

    .modal-title {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0;
      display: flex;
      align-items: center;
      gap: var(--spacing-3);
    }

    .modal-icon {
      width: 24px;
      height: 24px;
      color: var(--primary-color);
    }

    .modal-close {
      background: none;
      border: none;
      color: var(--gray-400);
      cursor: pointer;
      padding: var(--spacing-2);
      border-radius: var(--radius-lg);
      transition: all var(--transition-fast);
    }

    .modal-close:hover {
      background: var(--gray-100);
      color: var(--gray-600);
    }

    .modal-close svg {
      width: 20px;
      height: 20px;
    }

    .modal-form {
      padding: var(--spacing-6);
    }

    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: var(--spacing-4);
      padding-top: var(--spacing-6);
      border-top: 1px solid var(--gray-200);
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
      background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
      color: var(--white);
    }

    .btn-primary:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: var(--shadow-lg);
    }

    .btn-secondary {
      background: var(--gray-100);
      color: var(--gray-700);
      border: 1px solid var(--gray-300);
    }

    .btn-secondary:hover {
      background: var(--gray-200);
      border-color: var(--gray-400);
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

    /* Responsive Design */
    @media (max-width: 768px) {
      .owner-management {
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
export class OwnerManagementComponent implements OnInit {
  adminService = inject(AdminService);
  fb = inject(FormBuilder);

  owners: AdminUser[] = [];
  showForm = false;

  // **** NEW PROPERTIES FOR EDIT MODAL ****
  isEditModalVisible = false;
  currentEditingOwner: AdminUser | null = null;

  // Form for adding a new owner
  ownerForm = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.pattern(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    role: [Role.OWNER]
  });

  // **** NEW FORM FOR EDITING AN OWNER ****
  // This form matches the fields in your backend's UserUpdateRequest DTO
  editOwnerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.pattern(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)]]
  });

  ngOnInit(): void {
    this.loadOwners();
  }

  loadOwners(): void {
    this.adminService.getOwners().subscribe(data => {
      this.owners = data;
    });
  }

  deleteOwner(id: number): void {
    if (confirm('Are you sure you want to delete this owner?')) {
      this.adminService.deleteOwner(id).subscribe(() => {
        this.loadOwners();
      });
    }
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    this.ownerForm.reset({ role: Role.OWNER });
  }

  onSubmit(): void {
    if (this.ownerForm.invalid) {
      return;
    }
    const formValue = this.ownerForm.value;
    if (formValue.username && formValue.email && formValue.password && formValue.firstName && formValue.lastName) {
      this.adminService.createOwner(formValue as any).subscribe(() => {
        this.loadOwners();
        this.toggleForm();
      });
    }
  }

  // **** NEW METHODS FOR HANDLING THE EDIT MODAL ****

  openEditModal(owner: AdminUser): void {
    this.currentEditingOwner = owner;
    // Pre-fill the edit form with the selected owner's data
    this.editOwnerForm.setValue({
      firstName: owner.firstName,
      lastName: owner.lastName,
      email: owner.email
    });
    this.isEditModalVisible = true;
  }

  closeEditModal(): void {
    this.isEditModalVisible = false;
    this.currentEditingOwner = null;
    this.editOwnerForm.reset();
  }

  onUpdateSubmit(): void {
    if (this.editOwnerForm.invalid || !this.currentEditingOwner) {
      return;
    }
    const formValue = this.editOwnerForm.value;
    if (formValue.firstName && formValue.lastName && formValue.email) {
      this.adminService.updateOwner(this.currentEditingOwner.id, formValue as any).subscribe(() => {
        this.loadOwners();
        this.closeEditModal();
      });
    }
  }
}