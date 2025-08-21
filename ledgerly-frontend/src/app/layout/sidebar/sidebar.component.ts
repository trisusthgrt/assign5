import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="sidebar-nav">
      <div class="nav-section">
        <h3 class="nav-title">Navigation</h3>
        
        <!-- Admin Links -->
        <div class="nav-group" *ngIf="authService.currentUserRole$ | async as role">
          <div class="nav-group-title" *ngIf="role === 'ADMIN'">Admin Panel</div>
          <ul class="nav-list" *ngIf="role === 'ADMIN'">
            <li class="nav-item">
              <a routerLink="/admin/admins" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z"></path>
                </svg>
                <span>Manage Admins</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/admin/owners" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                </svg>
                <span>Manage Owners</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/admin/audit" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <span>Audit Logs</span>
              </a>
            </li>
          </ul>
        </div>

        <!-- Owner Links -->
        <div class="nav-group" *ngIf="authService.currentUserRole$ | async as role">
          <div class="nav-group-title" *ngIf="role === 'OWNER'">Business Management</div>
          <ul class="nav-list" *ngIf="role === 'OWNER'">
            <li class="nav-item">
              <a routerLink="/owner/staff" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                </svg>
                <span>Manage Staff</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/owner/shops" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                </svg>
                <span>Manage Shops</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/owner/assign-shop" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                </svg>
                <span>Assign Shop</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/owner/purchases" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
                </svg>
                <span>Purchases</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/owner/payments" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
                </svg>
                <span>Payments</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/owner/payments/status" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                </svg>
                <span>Payment Status</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/owner/exports" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <span>Exports</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/owner/profile" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
                <span>My Profile</span>
              </a>
            </li>
          </ul>
        </div>

        <!-- Staff Links -->
        <div class="nav-group" *ngIf="authService.currentUserRole$ | async as role">
          <div class="nav-group-title" *ngIf="role === 'STAFF'">Staff Operations</div>
          <ul class="nav-list" *ngIf="role === 'STAFF'">
            <li class="nav-item">
              <a routerLink="/staff/dashboard" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 5a2 2 0 012-2h4a2 2 0 012 2v2H8V5z"></path>
                </svg>
                <span>Dashboard</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/staff/customers" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                </svg>
                <span>Manage Customers</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/staff/payments" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
                </svg>
                <span>Record Payments</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/staff/payments/status" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                </svg>
                <span>Payment Status</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/staff/ledger/search" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
                <span>Ledger Search</span>
              </a>
            </li>
            <li class="nav-item">
              <a routerLink="/staff/profile" routerLinkActive="active" class="nav-link">
                <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
                <span>My Profile</span>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .sidebar-nav {
      background: var(--white);
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-lg);
      border: 1px solid var(--gray-200);
      overflow: hidden;
    }

    .nav-section {
      padding: var(--spacing-6);
    }

    .nav-title {
      font-size: var(--font-size-lg);
      font-weight: 600;
      color: var(--gray-900);
      margin-bottom: var(--spacing-6);
      padding-bottom: var(--spacing-3);
      border-bottom: 2px solid var(--gray-200);
    }

    .nav-group {
      margin-bottom: var(--spacing-8);
    }

    .nav-group-title {
      font-size: var(--font-size-sm);
      font-weight: 600;
      color: var(--gray-600);
      text-transform: uppercase;
      letter-spacing: 0.05em;
      margin-bottom: var(--spacing-3);
      padding: var(--spacing-2) var(--spacing-3);
      background: var(--gray-100);
      border-radius: var(--radius-lg);
    }

    .nav-list {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .nav-item {
      margin-bottom: var(--spacing-1);
    }

    .nav-link {
      display: flex;
      align-items: center;
      gap: var(--spacing-3);
      padding: var(--spacing-3) var(--spacing-4);
      color: var(--gray-700);
      text-decoration: none;
      border-radius: var(--radius-lg);
      transition: all var(--transition-fast);
      font-weight: 500;
    }

    .nav-link:hover {
      background: var(--gray-100);
      color: var(--gray-900);
      transform: translateX(4px);
    }

    .nav-link.active {
      background: var(--primary-color);
      color: var(--white);
      box-shadow: var(--shadow-md);
    }

    .nav-link.active:hover {
      background: var(--primary-hover);
      transform: translateX(4px);
    }

    .nav-icon {
      width: 20px;
      height: 20px;
      flex-shrink: 0;
    }

    .nav-link span {
      flex: 1;
    }

    @media (max-width: 768px) {
      .sidebar-nav {
        border-radius: var(--radius-lg);
        box-shadow: var(--shadow-md);
      }

      .nav-section {
        padding: var(--spacing-4);
      }

      .nav-title {
        font-size: var(--font-size-base);
        margin-bottom: var(--spacing-4);
      }

      .nav-group {
        margin-bottom: var(--spacing-6);
      }

      .nav-link {
        padding: var(--spacing-2) var(--spacing-3);
        font-size: var(--font-size-sm);
      }
    }
  `]
})
export class SidebarComponent {
  authService = inject(AuthService);
}