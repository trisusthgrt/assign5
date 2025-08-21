import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { Role } from './core/models/user.model';
import { publicGuard } from './core/guards/public.guard';

export const routes: Routes = [
    // Auth routes (login, register)
    {
        path: 'auth',
        canActivate: [publicGuard],
        loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
    },

    // Main application layout
    {
        path: '',
        component: MainLayoutComponent,
        canActivate: [authGuard],
        children: [
            // Default route for a logged-in user
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            
            // Dashboard route
            {
                path: 'dashboard',
                loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
            },

            // Admin-only routes
            {
                path: 'admin',
                canActivate: [roleGuard],
                data: { roles: [Role.ADMIN] },
                loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
            },

            // Owner routes
            {
                path: 'owner',
                canActivate: [roleGuard],
                data: { roles: [Role.OWNER, Role.ADMIN] },
                loadChildren: () => import('./features/owner/owner.routes').then(m => m.OWNER_ROUTES)
            },

            // **** THIS IS THE MISSING PIECE ****
            // Add the route configuration for the Staff role
            {
                path: 'staff',
                canActivate: [roleGuard],
                data: { roles: [Role.STAFF] }, // Only accessible to STAFF
                loadChildren: () => import('./features/staff/staff.routes').then(m => m.STAFF_ROUTES)
            },
        ]
    },

    // Catch-all route
    { path: '**', redirectTo: '', pathMatch: 'full' }
];