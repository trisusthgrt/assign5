import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LedgerEntry, LedgerSummary, PagedLedgerEntriesResponse, ApiBooleanResponse } from '../models/ledger.model';

@Injectable({ providedIn: 'root' })
export class LedgerService {
    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:8080/api/v1/ledger';

    createEntry(entry: LedgerEntry): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/entries`, entry);
    }

    createEntryWithAttachments(entry: LedgerEntry, files: File[], attachmentDescription?: string): Observable<any> {
        const form = new FormData();
        form.append('entry', new Blob([JSON.stringify(entry)], { type: 'application/json' }));
        for (const file of files) form.append('files', file);
        if (attachmentDescription) form.append('attachmentDescription', attachmentDescription);
        return this.http.post<any>(`${this.apiUrl}/entries/with-attachments`, form);
    }

    getEntryById(id: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/entries/${id}`);
    }

    updateEntry(id: number, partial: Partial<LedgerEntry>): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/entries/${id}`, partial);
    }

    deleteEntry(id: number): Observable<ApiBooleanResponse> {
        return this.http.delete<ApiBooleanResponse>(`${this.apiUrl}/entries/${id}`);
    }

    getCustomerEntries(customerId: number, page = 0, size = 10, sortBy = 'transactionDate', sortDir: 'asc' | 'desc' = 'desc'): Observable<PagedLedgerEntriesResponse> {
        const params = new HttpParams()
            .set('page', page)
            .set('size', size)
            .set('sortBy', sortBy)
            .set('sortDir', sortDir);
        return this.http.get<PagedLedgerEntriesResponse>(`${this.apiUrl}/customer/${customerId}/entries`, { params });
    }

    searchEntries(query: Record<string, any>): Observable<PagedLedgerEntriesResponse> {
        let params = new HttpParams();
        Object.entries(query).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') params = params.set(k, String(v)); });
        return this.http.get<PagedLedgerEntriesResponse>(`${this.apiUrl}/entries/search`, { params });
    }

    getBalanceSummary(customerId: number): Observable<{ success: boolean; summary: LedgerSummary }>{
        return this.http.get<{ success: boolean; summary: LedgerSummary }>(`${this.apiUrl}/customer/${customerId}/balance-summary`);
    }

    getUnreconciledEntries(customerId: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/customer/${customerId}/unreconciled`);
    }

    addAttachment(entryId: number, file: File, description?: string): Observable<any> {
        const form = new FormData();
        form.append('file', file);
        if (description) form.append('description', description);
        return this.http.post<any>(`${this.apiUrl}/entries/${entryId}/attachments`, form);
    }

    removeAttachment(entryId: number, attachmentId: number): Observable<ApiBooleanResponse> {
        return this.http.delete<ApiBooleanResponse>(`${this.apiUrl}/entries/${entryId}/attachments/${attachmentId}`);
    }
}


