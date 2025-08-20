import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';

import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    MatChipsModule,
    MatProgressBarModule,
    MatDividerModule
  ],
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <h1>Welcome back, {{ currentUser?.firstName }}!</h1>
        <p class="subtitle">Here's what's happening with your business today</p>
      </div>

      <div class="metrics-grid">
        <mat-card class="metric-card">
          <mat-card-content>
            <div class="metric-content">
              <div class="metric-icon customers">
                <mat-icon>people</mat-icon>
              </div>
              <div class="metric-details">
                <h3>{{ metrics.totalCustomers }}</h3>
                <p>Total Customers</p>
                <span class="metric-change positive">+12% from last month</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card class="metric-card">
          <mat-card-content>
            <div class="metric-content">
              <div class="metric-icon revenue">
                <mat-icon>account_balance_wallet</mat-icon>
              </div>
              <div class="metric-details">
                <h3>₹{{ metrics.totalRevenue | number:'1.0-0' }}</h3>
                <p>Total Revenue</p>
                <span class="metric-change positive">+8% from last month</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card class="metric-card">
          <mat-card-content>
            <div class="metric-content">
              <div class="metric-icon pending">
                <mat-icon>pending_actions</mat-icon>
              </div>
              <div class="metric-details">
                <h3>{{ metrics.pendingPayments }}</h3>
                <p>Pending Payments</p>
                <span class="metric-change negative">₹{{ metrics.pendingAmount | number:'1.0-0' }}</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card class="metric-card">
          <mat-card-content>
            <div class="metric-content">
              <div class="metric-icon transactions">
                <mat-icon>swap_horiz</mat-icon>
              </div>
              <div class="metric-details">
                <h3>{{ metrics.totalTransactions }}</h3>
                <p>Total Transactions</p>
                <span class="metric-change positive">+15% from last month</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>
      </div>

      <div class="dashboard-content">
        <div class="left-column">
          <mat-card class="recent-activity">
            <mat-card-header>
              <mat-card-title>Recent Activity</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <mat-list>
                <mat-list-item *ngFor="let activity of recentActivities">
                  <mat-icon matListItemIcon [class]="activity.type">{{ activity.icon }}</mat-icon>
                  <div matListItemTitle>{{ activity.title }}</div>
                  <div matListItemLine>{{ activity.description }}</div>
                  <div matListItemMeta>
                    <span class="activity-time">{{ activity.time }}</span>
                  </div>
                </mat-list-item>
              </mat-list>
            </mat-card-content>
          </mat-card>
        </div>

        <div class="right-column">
          <mat-card class="quick-actions">
            <mat-card-header>
              <mat-card-title>Quick Actions</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <div class="action-buttons">
                <button mat-raised-button color="primary" class="action-btn">
                  <mat-icon>add</mat-icon>
                  Add Customer
                </button>
                <button mat-raised-button color="accent" class="action-btn">
                  <mat-icon>receipt</mat-icon>
                  New Transaction
                </button>
                <button mat-raised-button color="warn" class="action-btn">
                  <mat-icon>payment</mat-icon>
                  Record Payment
                </button>
                <button mat-raised-button class="action-btn">
                  <mat-icon>assessment</mat-icon>
                  View Reports
                </button>
              </div>
            </mat-card-content>
          </mat-card>

          <mat-card class="notifications">
            <mat-card-header>
              <mat-card-title>Notifications</mat-card-title>
            </mat-card-header>
            <mat-card-content>
              <div class="notification-item" *ngFor="let notification of notifications">
                <div class="notification-content">
                  <p class="notification-text">{{ notification.message }}</p>
                  <span class="notification-time">{{ notification.time }}</span>
                </div>
                <mat-chip [color]="notification.priority === 'high' ? 'warn' : 'primary'" selected>
                  {{ notification.priority }}
                </mat-chip>
              </div>
            </mat-card-content>
          </mat-card>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 20px;
    }

    .dashboard-header {
      margin-bottom: 30px;
    }

    .dashboard-header h1 {
      margin: 0 0 8px 0;
      color: #333;
      font-size: 28px;
      font-weight: 500;
    }

    .subtitle {
      margin: 0;
      color: #666;
      font-size: 16px;
    }

    .metrics-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }

    .metric-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .metric-content {
      display: flex;
      align-items: center;
      gap: 20px;
    }

    .metric-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255, 255, 255, 0.2);
    }

    .metric-icon mat-icon {
      font-size: 30px;
      width: 30px;
      height: 30px;
    }

    .metric-details h3 {
      margin: 0 0 4px 0;
      font-size: 24px;
      font-weight: 600;
    }

    .metric-details p {
      margin: 0 0 8px 0;
      opacity: 0.9;
    }

    .metric-change {
      font-size: 12px;
      font-weight: 500;
    }

    .metric-change.positive {
      color: #4caf50;
    }

    .metric-change.negative {
      color: #ff9800;
    }

    .dashboard-content {
      display: grid;
      grid-template-columns: 2fr 1fr;
      gap: 20px;
    }

    .recent-activity, .quick-actions, .notifications {
      margin-bottom: 20px;
    }

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .action-btn {
      justify-content: flex-start;
      height: 48px;
      padding: 0 16px;
    }

    .action-btn mat-icon {
      margin-right: 12px;
    }

    .notification-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #eee;
    }

    .notification-item:last-child {
      border-bottom: none;
    }

    .notification-text {
      margin: 0 0 4px 0;
      font-size: 14px;
    }

    .notification-time {
      font-size: 12px;
      color: #666;
    }

    .activity-time {
      font-size: 12px;
      color: #666;
    }

    @media (max-width: 768px) {
      .dashboard-content {
        grid-template-columns: 1fr;
      }
      
      .metrics-grid {
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      }
    }
  `]
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;

  metrics = {
    totalCustomers: 156,
    totalRevenue: 1250000,
    pendingPayments: 23,
    pendingAmount: 450000,
    totalTransactions: 892
  };

  recentActivities = [
    {
      type: 'customer',
      icon: 'person_add',
      title: 'New Customer Added',
      description: 'John Doe was added to the system',
      time: '2 hours ago'
    },
    {
      type: 'payment',
      icon: 'payment',
      title: 'Payment Received',
      description: '₹25,000 received from ABC Company',
      time: '4 hours ago'
    },
    {
      type: 'transaction',
      icon: 'receipt',
      title: 'New Transaction',
      description: 'Credit entry of ₹15,000 recorded',
      time: '6 hours ago'
    },
    {
      type: 'reminder',
      icon: 'notifications',
      title: 'Payment Reminder Sent',
      description: 'Reminder sent to XYZ Corp for overdue payment',
      time: '1 day ago'
    }
  ];

  notifications = [
    {
      message: 'Payment of ₹50,000 is overdue by 5 days',
      time: '1 hour ago',
      priority: 'high'
    },
    {
      message: 'New customer registration requires approval',
      time: '3 hours ago',
      priority: 'medium'
    },
    {
      message: 'Monthly report is ready for review',
      time: '1 day ago',
      priority: 'low'
    }
  ];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
  }
}
