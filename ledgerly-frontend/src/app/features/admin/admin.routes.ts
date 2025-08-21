import { Routes } from '@angular/router';
import { OwnerManagementComponent } from './owner-management/owner-management.component';
import { AdminManagementComponent } from './admin-management/admin-management.component';

export const ADMIN_ROUTES: Routes = [
  { path: 'admins', component: AdminManagementComponent },
  { path: 'owners', component: OwnerManagementComponent },
  { path: '', redirectTo: 'admins', pathMatch: 'full' }
];