import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';

import { AuthService } from './core/services/auth.service';
import { User } from './core/models/user.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatMenuModule,
    MatBadgeModule
  ],
  template: `
    <mat-toolbar color="primary" class="toolbar">
      <button mat-icon-button (click)="sidenav.toggle()" class="toolbar-menu-button">
        <mat-icon>menu</mat-icon>
      </button>

      <span class="toolbar-title">Ledgerly</span>

      <span class="toolbar-spacer"></span>

      <ng-container *ngIf="currentUser$ | async as user; else authButtons">
        <button mat-icon-button [matMenuTriggerFor]="userMenu" class="toolbar-user-button">
          <mat-icon>account_circle</mat-icon>
        </button>
        <mat-menu #userMenu="matMenu">
          <button mat-menu-item routerLink="/profile">
            <mat-icon>person</mat-icon>
            <span>Profile</span>
          </button>
          <button mat-menu-item (click)="logout()">
            <mat-icon>exit_to_app</mat-icon>
            <span>Logout</span>
          </button>
        </mat-menu>
      </ng-container>

      <ng-template #authButtons>
        <button mat-button routerLink="/auth/login">Login</button>
        <button mat-button routerLink="/auth/register">Register</button>
      </ng-template>
    </mat-toolbar>

    <mat-sidenav-container class="sidenav-container">
      <mat-sidenav #sidenav class="sidenav" mode="side" opened>
        <mat-nav-list>
          <a mat-list-item routerLink="/dashboard" routerLinkActive="active">
            <mat-icon>dashboard</mat-icon>
            <span>Dashboard</span>
          </a>

          <a mat-list-item routerLink="/customers" routerLinkActive="active">
            <mat-icon>people</mat-icon>
            <span>Customers</span>
          </a>

          <a mat-list-item routerLink="/ledger" routerLinkActive="active">
            <mat-icon>account_balance</mat-icon>
            <span>Ledger</span>
          </a>

          <ng-container *ngIf="(currentUser$ | async)?.role === 'ADMIN' || (currentUser$ | async)?.role === 'OWNER'">
            <a mat-list-item routerLink="/users" routerLinkActive="active">
              <mat-icon>group</mat-icon>
              <span>Users</span>
            </a>

            <a mat-list-item routerLink="/shops" routerLinkActive="active">
              <mat-icon>store</mat-icon>
              <span>Shops</span>
            </a>
          </ng-container>
        </mat-nav-list>
      </mat-sidenav>

      <mat-sidenav-content class="sidenav-content">
        <div class="content-container">
          <router-outlet></router-outlet>
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [`
    .toolbar {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 1000;
    }

    .toolbar-menu-button {
      margin-right: 16px;
    }

    .toolbar-title {
      font-size: 20px;
      font-weight: 500;
    }

    .toolbar-spacer {
      flex: 1 1 auto;
    }

    .toolbar-user-button {
      margin-left: 8px;
    }

    .sidenav-container {
      height: 100vh;
      margin-top: 64px;
    }

    .sidenav {
      width: 250px;
      background-color: #fafafa;
    }

    .sidenav-content {
      background-color: #f5f5f5;
      min-height: calc(100vh - 64px);
    }

    .content-container {
      padding: 20px;
    }

    .mat-list-item.active {
      background-color: rgba(63, 81, 181, 0.1);
      color: #3f51b5;
    }

    .mat-list-item mat-icon {
      margin-right: 16px;
    }
  `]
})
export class AppComponent {
  constructor(private authService: AuthService) {}

  get currentUser$() {
    return this.authService.currentUser$;
  }

  logout(): void {
    this.authService.logout();
  }
}
