import { Routes } from '@angular/router';
import { CustomerManagementComponent } from './customer-management/customer-management.component';

export const STAFF_ROUTES: Routes = [
  { path: 'dashboard', loadComponent: () => import('./staffdashboard/staff-dashboard.component').then(m => m.StaffDashboardComponent) },
  { path: 'customers', component: CustomerManagementComponent },
  { path: 'customers/:id', loadComponent: () => import('./customer-detail/customer-detail.component').then(m => m.CustomerDetailComponent) },
  { path: 'profile', loadComponent: () => import('./profile/staff-profile.component').then(m => m.StaffProfileComponent) },
  { path: 'payments', loadComponent: () => import('./payments/payments.component').then(m => m.PaymentsComponent) },
  { path: 'payments/status', loadComponent: () => import('./payments/status-dashboard.component').then(m => m.PaymentStatusDashboardComponent) },
  { path: 'ledger/search', loadComponent: () => import('./ledger/search/ledger-search.component').then(m => m.LedgerSearchComponent) },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];