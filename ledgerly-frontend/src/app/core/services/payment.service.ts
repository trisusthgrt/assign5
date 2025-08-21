import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PaymentCreateRequest {
  amount: number;
  paymentDate: string; // ISO date yyyy-MM-dd
  paymentMethod: string; // e.g., CASH, BANK_TRANSFER
  status?: string; // PENDING, PAID, OVERDUE, DISPUTED
  customerId: number;
  description: string;
  notes?: string;
}

export interface PaymentStatusUpdateRequest {
  status: string;
  statusNotes?: string;
  dueDate?: string; // ISO date
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/payments';

  recordPayment(payload: PaymentCreateRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, payload);
  }

  getPaymentsByCustomer(customerId: number, page = 0, size = 20): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/customer/${customerId}?page=${page}&size=${size}`);
  }

  updatePaymentStatus(paymentId: number, status: string, statusNotes?: string, dueDate?: string): Observable<any> {
    const body: PaymentStatusUpdateRequest = { status, statusNotes, dueDate };
    return this.http.put<any>(`${this.apiUrl}/${paymentId}/status`, body);
  }

  disputePayment(paymentId: number, disputeReason: string, additionalNotes?: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${paymentId}/dispute`, { disputeReason, additionalNotes });
  }

  resolveDispute(paymentId: number, resolutionNotes: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${paymentId}/resolve-dispute?resolutionNotes=${encodeURIComponent(resolutionNotes)}`, {});
  }

  getPaymentsByStatus(status: string, page = 0, size = 10, sortBy = 'paymentDate', sortDir: 'desc'): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/status/${status}?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);
  }

  getOverdue(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/overdue`);
  }

  getDisputed(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/disputed`);
  }

  getStatusSummary(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/status-summary`);
  }
}
