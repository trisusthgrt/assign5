import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header class="header">
      <h1>Ledgerly</h1>
      <button class="btn-logout" (click)="logout()">Logout</button>
    </header>
  `,
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  private authService = inject(AuthService);

  logout(): void {
    this.authService.logout();
  }
}