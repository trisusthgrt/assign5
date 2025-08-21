import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, DecodedToken } from '../models/auth.model';
import { jwtDecode } from 'jwt-decode';
import { StorageService } from './storage.service';
import { Router } from '@angular/router';
import { Role } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private storageService = inject(StorageService);
  private router = inject(Router);

  private apiUrl = 'http://localhost:8080/api/v1/auth';

  // BehaviorSubjects are great for reacting to state changes in your UI (e.g., sidebar)
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private currentUserRoleSubject = new BehaviorSubject<Role | null>(this.getUserRole());
  currentUserRole$ = this.currentUserRoleSubject.asObservable();

  register(data: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data);
  }

  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: AuthResponse) => this.setSession(response))
    );
  }

  logout(): void {
    this.storageService.removeItem('auth_token');
    // Update the streams so the rest of the app knows the user has logged out
    this.isAuthenticatedSubject.next(false);
    this.currentUserRoleSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  private setSession(authResponse: AuthResponse): void {
    this.storageService.setItem('auth_token', authResponse.token);
    // Update the streams so the rest of the app knows the user has logged in
    this.isAuthenticatedSubject.next(true);
    this.currentUserRoleSubject.next(this.getUserRole());
  }

  getToken(): string | null {
    return this.storageService.getItem('auth_token');
  }

  // This is the NEW public, synchronous method for the guards.
  // It provides an immediate, real-time answer without relying on observables.
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    try {
      const decoded: DecodedToken = jwtDecode(token);
      const isExpired = Date.now() >= decoded.exp * 1000;
      return !isExpired; // Returns true if the token exists AND is not expired
    } catch (error) {
      // If jwt-decode fails, the token is malformed and not valid.
      return false;
    }
  }

  // This is the original private method used to initialize the BehaviorSubject.
  private hasValidToken(): boolean {
    // Its logic is identical to the new public method.
    return this.isAuthenticated();
  }

  getUserRole(): Role | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded: any = jwtDecode(token);
      // Backend sends ROLE_ADMIN, ROLE_OWNER, ROLE_STAFF format
      if (decoded.authorities && decoded.authorities.length > 0) {
        const authority = decoded.authorities[0];
        // Remove "ROLE_" prefix to get the actual role
        if (authority.startsWith('ROLE_')) {
          const role = authority.substring(5); // Remove "ROLE_" prefix
          return role as Role;
        }
      }
      return null;
    } catch (error) {
      return null;
    }
  }
}