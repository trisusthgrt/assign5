import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent],
  template: `
    <div class="app-container">
      <!-- Header -->
      <header class="app-header">
        <div class="container">
          <div class="header-content">
            <div class="logo-section">
              <h1 class="logo">Ledgerly</h1>
              <p class="tagline">Smart Financial Management</p>
            </div>
            <div class="user-section" *ngIf="authService.isAuthenticated$ | async">
              <div class="user-info">
                <span class="user-role">{{ authService.currentUserRole$ | async }}</span>
                <button class="btn btn-secondary btn-sm" (click)="logout()">Logout</button>
              </div>
            </div>
          </div>
        </div>
      </header>

      <!-- Main Content Area -->
      <div class="main-content">
        <div class="container">
          <div class="content-wrapper">
            <!-- Sidebar -->
            <aside class="sidebar" *ngIf="authService.isAuthenticated$ | async">
              <app-sidebar></app-sidebar>
            </aside>
            
            <!-- Page Content -->
            <main class="page-content">
              <router-outlet></router-outlet>
            </main>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      background: linear-gradient(135deg, var(--gray-50) 0%, var(--gray-100) 100%);
    }

    .app-header {
      background: var(--white);
      border-bottom: 1px solid var(--gray-200);
      box-shadow: var(--shadow-sm);
      position: sticky;
      top: 0;
      z-index: 40;
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: var(--spacing-4) 0;
    }

    .logo-section {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-1);
    }

    .logo {
      font-size: var(--font-size-2xl);
      font-weight: 700;
      color: var(--primary-color);
      margin: 0;
      background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .tagline {
      font-size: var(--font-size-sm);
      color: var(--gray-600);
      margin: 0;
      font-weight: 500;
    }

    .user-section {
      display: flex;
      align-items: center;
      gap: var(--spacing-4);
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: var(--spacing-3);
    }

    .user-role {
      background: var(--primary-light);
      color: var(--primary-color);
      padding: var(--spacing-2) var(--spacing-3);
      border-radius: var(--radius-2xl);
      font-size: var(--font-size-xs);
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .main-content {
      padding: var(--spacing-8) 0;
    }

    .content-wrapper {
      display: flex;
      gap: var(--spacing-8);
      align-items: flex-start;
    }

    .sidebar {
      flex: 0 0 280px;
      position: sticky;
      top: calc(80px + var(--spacing-8));
    }

    .page-content {
      flex: 1;
      min-height: calc(100vh - 200px);
    }

    @media (max-width: 1024px) {
      .sidebar {
        flex: 0 0 240px;
      }
    }

    @media (max-width: 768px) {
      .content-wrapper {
        flex-direction: column;
        gap: var(--spacing-6);
      }

      .sidebar {
        flex: none;
        width: 100%;
        position: static;
      }

      .header-content {
        flex-direction: column;
        gap: var(--spacing-4);
        text-align: center;
      }

      .user-section {
        justify-content: center;
      }
    }
  `]
})
export class AppComponent {
  authService = inject(AuthService);

  logout(): void {
    this.authService.logout();
  }
}