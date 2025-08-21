import { Routes } from '@angular/router';
import { StaffManagementComponent } from './staff-management/staff-management.component';
import { ShopManagementComponent } from './shop-management/shop-management.component';
import { AssignShopComponent } from './assign-shop/assign-shop.component';

export const OWNER_ROUTES: Routes = [
  { path: 'staff', component: StaffManagementComponent },
  { path: 'shops', component: ShopManagementComponent },
  { path: 'assign-shop', component: AssignShopComponent },
  { path: 'purchases', loadComponent: () => import('./purchases/purchases.component').then(m => m.PurchasesComponent) },
  { path: 'payments', loadComponent: () => import('./payments/payments.component').then(m => m.PaymentsComponent) },
  { path: 'payments/status', loadComponent: () => import('./payments/status-dashboard.component').then(m => m.PaymentStatusDashboardComponent) },
  { path: 'exports', loadComponent: () => import('./exports/exports.component').then(m => m.ExportsComponent) },
  { path: 'profile', loadComponent: () => import('./profile/owner-profile.component').then(m => m.OwnerProfileComponent) },
  { path: 'business', loadComponent: () => import('./profile/business-details.component').then(m => m.BusinessDetailsComponent) },
  { path: '', redirectTo: 'staff', pathMatch: 'full' }
];