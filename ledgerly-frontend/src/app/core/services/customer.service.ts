import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/customer.model'; // 1. IMPORT THE MODEL

// 2. CREATE A DEDICATED MODEL FILE FOR API RESPONSES
// (We will create this file in the next step)
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/customers';

  getCustomers(): Observable<ApiResponse<Customer[]>> {
    return this.http.get<ApiResponse<Customer[]>>(`${this.apiUrl}`);
  }

  createCustomer(customerData: any): Observable<ApiResponse<Customer>> {
    return this.http.post<ApiResponse<Customer>>(`${this.apiUrl}`, customerData);
  }

  updateCustomer(id: number, customerData: any): Observable<ApiResponse<Customer>> {
    return this.http.put<ApiResponse<Customer>>(`${this.apiUrl}/${id}`, customerData);
  }

  deleteCustomer(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}