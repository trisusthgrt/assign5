import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminUser {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  active: boolean;
}

export interface AdminCreateRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface AdminUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/admin/users';

  getAdmins(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/admins`);
  }

  getAdmin(id: number): Observable<AdminUser> {
    return this.http.get<AdminUser>(`${this.apiUrl}/admins/${id}`);
  }

  createAdmin(adminData: AdminCreateRequest): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${this.apiUrl}/admins`, adminData);
  }

  updateAdmin(id: number, adminData: AdminUpdateRequest): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.apiUrl}/admins/${id}`, adminData);
  }

  deleteAdmin(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admins/${id}`);
  }

  getOwners(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/owners`);
  }

  getOwner(id: number): Observable<AdminUser> {
    return this.http.get<AdminUser>(`${this.apiUrl}/owners/${id}`);
  }

  createOwner(ownerData: AdminCreateRequest): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${this.apiUrl}/owners`, ownerData);
  }

  updateOwner(id: number, ownerData: AdminUpdateRequest): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.apiUrl}/owners/${id}`, ownerData);
  }

  deleteOwner(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/owners/${id}`);
  }

  getStaff(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/staff`);
  }

  getStaffMember(id: number): Observable<AdminUser> {
    return this.http.get<AdminUser>(`${this.apiUrl}/staff/${id}`);
  }
}