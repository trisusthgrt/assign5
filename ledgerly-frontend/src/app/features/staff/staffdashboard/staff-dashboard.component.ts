import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <!-- Dashboard Header -->
      <div class="dashboard-header">
        <div class="header-content">
          <h1 class="dashboard-title">Staff Dashboard</h1>
          <p class="dashboard-subtitle">Welcome back! Here's your business overview</p>
        </div>
        <div class="user-info">
          <div class="user-avatar">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
          </div>
          <div class="user-details">
            <span class="user-role">{{ userRole() }}</span>
            <span class="user-status">{{ status() }}</span>
          </div>
        </div>
      </div>

      <!-- KPI Cards -->
      <div class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-icon customers-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
            </svg>
          </div>
          <div class="kpi-content">
            <h3 class="kpi-value">{{ stats()?.totalCustomers || 0 }}</h3>
            <p class="kpi-label">Total Customers</p>
          </div>
          <div class="kpi-trend positive">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
            </svg>
            <span>+12%</span>
          </div>
        </div>

        <div class="kpi-card">
          <div class="kpi-icon balance-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
            </svg>
          </div>
          <div class="kpi-content">
            <h3 class="kpi-value">₹{{ (stats()?.totalBalance || 0).toLocaleString() }}</h3>
            <p class="kpi-label">Total Balance</p>
          </div>
          <div class="kpi-trend negative">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 17h8m0 0V9m0 8l-8-8-4 4-6-6"></path>
            </svg>
            <span>-5%</span>
          </div>
        </div>

        <div class="kpi-card">
          <div class="kpi-icon shop-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
            </svg>
          </div>
          <div class="kpi-content">
            <h3 class="kpi-value">{{ shopName() || 'N/A' }}</h3>
            <p class="kpi-label">Assigned Shop</p>
          </div>
          <div class="kpi-status active">
            <span class="status-dot"></span>
            <span>Active</span>
          </div>
        </div>

        <div class="kpi-card">
          <div class="kpi-icon activity-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
            </svg>
          </div>
          <div class="kpi-content">
            <h3 class="kpi-value">24</h3>
            <p class="kpi-label">Today's Transactions</p>
          </div>
          <div class="kpi-trend positive">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
            </svg>
            <span>+8%</span>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="quick-actions">
        <h2 class="section-title">Quick Actions</h2>
        <div class="actions-grid">
          <a routerLink="/staff/customers" class="action-card">
            <div class="action-icon">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
            </div>
            <h3>Add Customer</h3>
            <p>Register a new customer</p>
          </a>

          <a routerLink="/staff/payments" class="action-card">
            <div class="action-icon">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
              </svg>
            </div>
            <h3>Record Payment</h3>
            <p>Process customer payment</p>
          </a>

          <a routerLink="/staff/ledger/search" class="action-card">
            <div class="action-icon">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
            <h3>Search Ledger</h3>
            <p>Find transaction details</p>
          </a>

          <a routerLink="/staff/profile" class="action-card">
            <div class="action-icon">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </div>
            <h3>My Profile</h3>
            <p>Update your information</p>
          </a>
        </div>
      </div>

      <!-- Recent Activity -->
      <div class="recent-activity">
        <h2 class="section-title">Recent Activity</h2>
        <div class="activity-list">
          <div class="activity-item">
            <div class="activity-icon payment">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
              </svg>
            </div>
            <div class="activity-content">
              <h4>Payment Received</h4>
              <p>Customer John Doe paid ₹5,000</p>
              <span class="activity-time">2 hours ago</span>
            </div>
          </div>

          <div class="activity-item">
            <div class="activity-icon customer">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
            </div>
            <div class="activity-content">
              <h4>New Customer Added</h4>
              <p>Jane Smith registered successfully</p>
              <span class="activity-time">4 hours ago</span>
            </div>
          </div>

          <div class="activity-item">
            <div class="activity-icon ledger">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
            </div>
            <div class="activity-content">
              <h4>Ledger Entry Created</h4>
              <p>New transaction recorded for Shop A</p>
              <span class="activity-time">6 hours ago</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: var(--spacing-6);
      max-width: 1400px;
      margin: 0 auto;
    }

    /* Dashboard Header */
    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--spacing-8);
      padding: var(--spacing-6);
      background: var(--white);
      border-radius: var(--radius-xl);
      box-shadow: var(--shadow-md);
      border: 1px solid var(--gray-200);
    }

    .dashboard-title {
      font-size: var(--font-size-3xl);
      font-weight: 700;
      color: var(--gray-900);
      margin: 0 0 var(--spacing-2) 0;
    }

    .dashboard-subtitle {
      font-size: var(--font-size-base);
      color: var(--gray-600);
      margin: 0;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: var(--spacing-4);
    }

    .user-avatar {
      width: 48px;
      height: 48px;
      background: var(--primary-light);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--primary-color);
    }

    .user-avatar svg {
      width: 24px;
      height: 24px;
    }

    .user-details {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-1);
    }

    .user-role {
      font-size: var(--font-size-sm);
      font-weight: 600;
      color: var(--primary-color);
      background: var(--primary-light);
      padding: var(--spacing-1) var(--spacing-3);
      border-radius: var(--radius-2xl);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .user-status {
      font-size: var(--font-size-xs);
      color: var(--success-color);
      font-weight: 500;
    }

    /* KPI Grid */
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: var(--spacing-6);
      margin-bottom: var(--spacing-8);
    }

    .kpi-card {
      background: var(--white);
      border-radius: var(--radius-xl);
      padding: var(--spacing-6);
      box-shadow: var(--shadow-md);
      border: 1px solid var(--gray-200);
      transition: all var(--transition-normal);
      position: relative;
      overflow: hidden;
    }

    .kpi-card:hover {
      transform: translateY(-4px);
      box-shadow: var(--shadow-lg);
    }

    .kpi-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, var(--primary-color), var(--primary-hover));
    }

    .kpi-icon {
      width: 56px;
      height: 56px;
      border-radius: var(--radius-xl);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: var(--spacing-4);
    }

    .kpi-icon svg {
      width: 28px;
      height: 28px;
      color: var(--white);
    }

    .customers-icon { background: linear-gradient(135deg, #10b981, #059669); }
    .balance-icon { background: linear-gradient(135deg, #3b82f6, #1d4ed8); }
    .shop-icon { background: linear-gradient(135deg, #f59e0b, #d97706); }
    .activity-icon { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }

    .kpi-content {
      margin-bottom: var(--spacing-4);
    }

    .kpi-value {
      font-size: var(--font-size-3xl);
      font-weight: 700;
      color: var(--gray-900);
      margin: 0 0 var(--spacing-2) 0;
    }

    .kpi-label {
      font-size: var(--font-size-sm);
      color: var(--gray-600);
      margin: 0;
      font-weight: 500;
    }

    .kpi-trend {
      display: flex;
      align-items: center;
      gap: var(--spacing-2);
      font-size: var(--font-size-sm);
      font-weight: 600;
    }

    .kpi-trend.positive {
      color: var(--success-color);
    }

    .kpi-trend.negative {
      color: var(--danger-color);
    }

    .kpi-trend svg {
      width: 16px;
      height: 16px;
    }

    .kpi-status {
      display: flex;
      align-items: center;
      gap: var(--spacing-2);
      font-size: var(--font-size-sm);
      font-weight: 600;
      color: var(--success-color);
    }

    .status-dot {
      width: 8px;
      height: 8px;
      background: var(--success-color);
      border-radius: 50%;
      animation: pulse 2s infinite;
    }

    /* Quick Actions */
    .quick-actions {
      margin-bottom: var(--spacing-8);
    }

    .section-title {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0 0 var(--spacing-6) 0;
    }

    .actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: var(--spacing-4);
    }

    .action-card {
      background: var(--white);
      border-radius: var(--radius-lg);
      padding: var(--spacing-6);
      box-shadow: var(--shadow-sm);
      border: 1px solid var(--gray-200);
      transition: all var(--transition-fast);
      text-decoration: none;
      color: inherit;
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: var(--spacing-4);
    }

    .action-card:hover {
      transform: translateY(-2px);
      box-shadow: var(--shadow-md);
      border-color: var(--primary-color);
    }

    .action-icon {
      width: 48px;
      height: 48px;
      background: var(--primary-light);
      border-radius: var(--radius-lg);
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--primary-color);
    }

    .action-icon svg {
      width: 24px;
      height: 24px;
    }

    .action-card h3 {
      font-size: var(--font-size-lg);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0;
    }

    .action-card p {
      font-size: var(--font-size-sm);
      color: var(--gray-600);
      margin: 0;
    }

    /* Recent Activity */
    .recent-activity {
      margin-bottom: var(--spacing-8);
    }

    .activity-list {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-4);
    }

    .activity-item {
      background: var(--white);
      border-radius: var(--radius-lg);
      padding: var(--spacing-4);
      box-shadow: var(--shadow-sm);
      border: 1px solid var(--gray-200);
      display: flex;
      align-items: center;
      gap: var(--spacing-4);
      transition: all var(--transition-fast);
    }

    .activity-item:hover {
      box-shadow: var(--shadow-md);
      transform: translateX(4px);
    }

    .activity-icon {
      width: 40px;
      height: 40px;
      border-radius: var(--radius-lg);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .activity-icon svg {
      width: 20px;
      height: 20px;
      color: var(--white);
    }

    .activity-icon.payment { background: var(--success-color); }
    .activity-icon.customer { background: var(--primary-color); }
    .activity-icon.ledger { background: var(--warning-color); }

    .activity-content {
      flex: 1;
    }

    .activity-content h4 {
      font-size: var(--font-size-base);
      font-weight: 600;
      color: var(--gray-900);
      margin: 0 0 var(--spacing-1) 0;
    }

    .activity-content p {
      font-size: var(--font-size-sm);
      color: var(--gray-600);
      margin: 0 0 var(--spacing-1) 0;
    }

    .activity-time {
      font-size: var(--font-size-xs);
      color: var(--gray-500);
      font-weight: 500;
    }

    /* Responsive Design */
    @media (max-width: 1024px) {
      .kpi-grid {
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: var(--spacing-4);
      }

      .actions-grid {
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      }
    }

    @media (max-width: 768px) {
      .dashboard-container {
        padding: var(--spacing-4);
      }

      .dashboard-header {
        flex-direction: column;
        gap: var(--spacing-4);
        text-align: center;
        padding: var(--spacing-4);
      }

      .dashboard-title {
        font-size: var(--font-size-2xl);
      }

      .kpi-grid {
        grid-template-columns: 1fr;
        gap: var(--spacing-4);
      }

      .actions-grid {
        grid-template-columns: 1fr;
      }

      .activity-item {
        flex-direction: column;
        text-align: center;
        gap: var(--spacing-3);
      }
    }

    @media (max-width: 480px) {
      .dashboard-container {
        padding: var(--spacing-3);
      }

      .kpi-card {
        padding: var(--spacing-4);
      }

      .action-card {
        padding: var(--spacing-4);
      }
    }
  `]
})
export class StaffDashboardComponent implements OnInit {
  private http = inject(HttpClient);
  
  stats = signal<{ totalCustomers: number; totalBalance: number } | null>(null);
  shopName = signal<string | null>(null);
  userRole = signal<string | null>(null);
  status = signal<string | null>(null);

  ngOnInit(): void {
    this.http.get<{ success: boolean; statistics?: { totalCustomers: number; totalBalance: number } }>('http://localhost:8080/api/v1/customers/stats').subscribe({
      next: (res: { success: boolean; statistics?: { totalCustomers: number; totalBalance: number } }) => {
        console.log('API Response:', res);
        if (res?.success) {
          const statistics = res.statistics;
          console.log('Statistics:', statistics);
          if (statistics) {
            this.stats.set({
              totalCustomers: statistics.totalCustomers ?? 0,
              totalBalance: statistics.totalBalance ?? 0
            });
          }
        } else {
          console.warn('API returned success: false');
        }
      },
      error: (error: any) => {
        console.error('Error fetching customer stats:', error);
        this.stats.set({ totalCustomers: 0, totalBalance: 0 });
      }
    });

    // Set some basic info for now
    this.shopName.set('Assigned Shop');
    this.userRole.set('STAFF');
    this.status.set('Active');
  }
}


