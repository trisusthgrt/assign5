import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'users',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'OWNER'] },
    loadChildren: () => import('./features/users/users.routes').then(m => m.USERS_ROUTES)
  },
  {
    path: 'shops',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN', 'OWNER'] },
    loadChildren: () => import('./features/shops/shops.routes').then(m => m.SHOPS_ROUTES)
  },
  {
    path: 'customers',
    canActivate: [authGuard],
    loadChildren: () => import('./features/customers/customers.routes').then(m => m.CUSTOMERS_ROUTES)
  },
  {
    path: 'ledger',
    canActivate: [authGuard],
    loadChildren: () => import('./features/ledger/ledger.routes').then(m => m.LEDGER_ROUTES)
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];
