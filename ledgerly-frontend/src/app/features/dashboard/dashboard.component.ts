import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Welcome to your Dashboard</h2>
    <p>Select an option from the sidebar to get started.</p>
  `,
})
export class DashboardComponent {}