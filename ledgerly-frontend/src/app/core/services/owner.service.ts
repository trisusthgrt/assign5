import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BasicUser } from '../models/user.model';
import { Shop } from '../models/shop.model';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/owner/staff';
  private shopApiUrl = 'http://localhost:8080/api/v1/shops';

  // Staff Management
  getStaff(): Observable<BasicUser[]> {
    // Corrected: Used backticks ``
    return this.http.get<BasicUser[]>(`${this.apiUrl}`);
  }

  createStaff(user: any): Observable<BasicUser> {
    // Corrected: Used backticks ``
    return this.http.post<BasicUser>(`${this.apiUrl}`, user);
  }

  updateStaff(id: number, user: Partial<BasicUser> & { firstName?: string; lastName?: string; email?: string }): Observable<BasicUser> {
    return this.http.put<BasicUser>(`${this.apiUrl}/${id}`, user);
  }

  deleteStaff(id: number): Observable<void> {
    // Corrected: Used backticks ``
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Shop Management
  getMyShops(): Observable<{shops: Shop[]}> {
    // Corrected: Used backticks ``
    return this.http.get<{shops: Shop[]}>(`${this.shopApiUrl}/my-shops`);
  }

  createShop(shop: any): Observable<{shop: Shop}> {
    // This line was already correct as it didn't need template literals
    return this.http.post<{shop: Shop}>(this.shopApiUrl, shop);
  }

  updateShop(id: number, shop: Partial<Shop>): Observable<{shop: Shop}> {
    return this.http.put<{shop: Shop}>(`${this.shopApiUrl}/${id}`, shop);
  }

  deleteShop(id: number): Observable<void> {
    // Corrected: Used backticks ``
    return this.http.delete<void>(`${this.shopApiUrl}/${id}`);
  }

  // Assign Staff to Shop
  assignStaffToShop(staffId: number, shopId: number): Observable<any> {
    // Corrected: Used backticks ``
    return this.http.post<any>(`${this.apiUrl}/${staffId}/assign-shop/${shopId}`, {});
  }
}