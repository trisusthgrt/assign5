import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-staff-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="profile">
      <h2>My Profile</h2>
      <div *ngIf="profile as p">
        <p><b>Username:</b> {{ p.username }}</p>
        <p><b>Full Name:</b> {{ p.fullName || (p.firstName + ' ' + p.lastName) }}</p>
        <p><b>Role:</b> {{ p.role }}</p>
        <p><b>Created:</b> {{ p.createdAt | date:'medium' }}</p>
        <p><b>Last Login:</b> {{ p.lastLogin | date:'medium' }}</p>
      </div>
    </div>
  `
})
export class StaffProfileComponent {
  private http = inject(HttpClient);
  profile: any = null;

  constructor() {
    this.http.get<any>('http://localhost:8080/api/v1/auth/me').subscribe(res => {
      this.profile = res; // endpoint returns the user object directly
    });
  }
}


