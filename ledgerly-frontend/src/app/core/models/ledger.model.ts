export interface LedgerEntry {
    id?: number;
    customerId: number;
    customerName?: string;
    transactionDate: string; // YYYY-MM-DD
    transactionType: 'CREDIT' | 'DEBIT' | 'OPENING_BALANCE' | 'ADJUSTMENT' | 'TRANSFER';
    amount: number;
    description?: string;
    notes?: string;
    referenceNumber?: string;
    invoiceNumber?: string;
    invoiceDate?: string; // YYYY-MM-DD
    paymentMethod?: string;
    balanceAfterTransaction?: number;
    isReconciled?: boolean;
    isActive?: boolean;
}

export interface LedgerSummary {
    customerId: number;
    customerName?: string;
    totalCredit: number;
    totalDebit: number;
    currentBalance: number;
    totalTransactions?: number;
    creditLimit?: number;
    isOverCreditLimit?: boolean;
}

export interface PagedLedgerEntriesResponse {
    success: boolean;
    entries: LedgerEntry[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
    size: number;
}

export interface ApiBooleanResponse {
    success: boolean;
    message?: string;
}



