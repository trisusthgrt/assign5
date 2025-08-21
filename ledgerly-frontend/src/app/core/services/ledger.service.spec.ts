import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LedgerService } from './ledger.service';
import { LedgerEntry, LedgerSummary, PagedLedgerEntriesResponse, ApiBooleanResponse } from '../models/ledger.model';

describe('LedgerService', () => {
  let service: LedgerService;
  let httpMock: HttpTestingController;

  const mockLedgerEntry: LedgerEntry = {
    id: 1,
    customerId: 1,
    transactionDate: '2025-08-21',
    transactionType: 'CREDIT',
    amount: 1000,
    description: 'Test transaction',
    referenceNumber: 'REF123',
    isReconciled: false
  };

  const mockLedgerSummary: LedgerSummary = {
    customerId: 1,
    totalCredit: 2000,
    totalDebit: 500,
    currentBalance: 1500
  };

  const mockPagedResponse: PagedLedgerEntriesResponse = {
    success: true,
    entries: [mockLedgerEntry],
    totalElements: 1,
    totalPages: 1,
    currentPage: 0,
    size: 10
  };

  const mockApiResponse: ApiBooleanResponse = {
    success: true,
    message: 'Operation successful'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LedgerService]
    });

    service = TestBed.inject(LedgerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('createEntry', () => {
    it('should make POST request to create ledger entry', () => {
      service.createEntry(mockLedgerEntry).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/ledger/entries');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockLedgerEntry);
      req.flush({ success: true });
    });
  });

  describe('createEntryWithAttachments', () => {
    it('should make POST request with FormData for entry and files', () => {
      const files = [new File(['test'], 'test.txt')];
      const attachmentDescription = 'Test attachment';

      service.createEntryWithAttachments(mockLedgerEntry, files, attachmentDescription).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/ledger/entries/with-attachments');
      expect(req.request.method).toBe('POST');
      expect(req.request.body instanceof FormData).toBeTrue();
      req.flush({ success: true });
    });

    it('should handle entry without attachment description', () => {
      const files = [new File(['test'], 'test.txt')];

      service.createEntryWithAttachments(mockLedgerEntry, files).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/ledger/entries/with-attachments');
      req.flush({ success: true });
    });
  });

  describe('getEntryById', () => {
    it('should make GET request to retrieve ledger entry', () => {
      const entryId = 1;

      service.getEntryById(entryId).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/${entryId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockLedgerEntry);
    });
  });

  describe('updateEntry', () => {
    it('should make PUT request to update ledger entry', () => {
      const entryId = 1;
      const updateData = { description: 'Updated description' };

      service.updateEntry(entryId, updateData).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/${entryId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush({ success: true });
    });
  });

  describe('deleteEntry', () => {
    it('should make DELETE request to delete ledger entry', () => {
      const entryId = 1;

      service.deleteEntry(entryId).subscribe(response => {
        expect(response).toEqual(mockApiResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/${entryId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(mockApiResponse);
    });
  });

  describe('getCustomerEntries', () => {
    it('should make GET request with default pagination parameters', () => {
      const customerId = 1;

      service.getCustomerEntries(customerId).subscribe(response => {
        expect(response).toEqual(mockPagedResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/customer/${customerId}/entries?page=0&size=10&sortBy=transactionDate&sortDir=desc`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPagedResponse);
    });

    it('should make GET request with custom pagination parameters', () => {
      const customerId = 1;
      const page = 2;
      const size = 25;
      const sortBy = 'amount';
      const sortDir = 'desc';

      service.getCustomerEntries(customerId, page, size, sortBy, sortDir).subscribe(response => {
        expect(response).toEqual(mockPagedResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/customer/${customerId}/entries?page=2&size=25&sortBy=amount&sortDir=desc`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPagedResponse);
    });
  });

  describe('searchEntries', () => {
    it('should make GET request with search query parameters', () => {
      const searchQuery = {
        customerId: 1,
        transactionType: 'CREDIT',
        amount: 1000,
        startDate: '2025-01-01',
        endDate: '2025-12-31'
      };

      service.searchEntries(searchQuery).subscribe(response => {
        expect(response).toEqual(mockPagedResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/search?customerId=1&transactionType=CREDIT&amount=1000&startDate=2025-01-01&endDate=2025-12-31`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPagedResponse);
    });

    it('should filter out undefined, null, and empty string values', () => {
      const searchQuery = {
        customerId: 1,
        transactionType: undefined,
        amount: null,
        description: ''
      };

      service.searchEntries(searchQuery).subscribe(response => {
        expect(response).toEqual(mockPagedResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/search?customerId=1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPagedResponse);
    });
  });

  describe('getBalanceSummary', () => {
    it('should make GET request to get customer balance summary', () => {
      const customerId = 1;
      const expectedResponse = { success: true, summary: mockLedgerSummary };

      service.getBalanceSummary(customerId).subscribe(response => {
        expect(response).toEqual(expectedResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/customer/${customerId}/balance-summary`);
      expect(req.request.method).toBe('GET');
      req.flush(expectedResponse);
    });
  });

  describe('getUnreconciledEntries', () => {
    it('should make GET request to get unreconciled entries', () => {
      const customerId = 1;

      service.getUnreconciledEntries(customerId).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/customer/${customerId}/unreconciled`);
      expect(req.request.method).toBe('GET');
      req.flush([mockLedgerEntry]);
    });
  });

  describe('addAttachment', () => {
    it('should make POST request to add attachment to entry', () => {
      const entryId = 1;
      const file = new File(['test'], 'test.txt');
      const description = 'Test attachment';

      service.addAttachment(entryId, file, description).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/${entryId}/attachments`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body instanceof FormData).toBeTrue();
      req.flush({ success: true });
    });

    it('should handle attachment without description', () => {
      const entryId = 1;
      const file = new File(['test'], 'test.txt');

      service.addAttachment(entryId, file).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/${entryId}/attachments`);
      req.flush({ success: true });
    });
  });

  describe('removeAttachment', () => {
    it('should make DELETE request to remove attachment', () => {
      const entryId = 1;
      const attachmentId = 1;

      service.removeAttachment(entryId, attachmentId).subscribe(response => {
        expect(response).toEqual(mockApiResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/${entryId}/attachments/${attachmentId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(mockApiResponse);
    });
  });

  describe('API URL configuration', () => {
    it('should use correct base URL', () => {
      expect(service['apiUrl']).toBe('http://localhost:8080/api/v1/ledger');
    });
  });

  describe('FormData handling', () => {
    it('should correctly append entry data to FormData', () => {
      const files = [new File(['test'], 'test.txt')];
      const attachmentDescription = 'Test attachment';

      service.createEntryWithAttachments(mockLedgerEntry, files, attachmentDescription).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/v1/ledger/entries/with-attachments');
      const formData = req.request.body as FormData;
      
      expect(formData.get('attachmentDescription')).toBe(attachmentDescription);
      expect(formData.getAll('files')).toEqual(files);
      
      req.flush({ success: true });
    });

    it('should correctly append file data to FormData for attachments', () => {
      const entryId = 1;
      const file = new File(['test'], 'test.txt');
      const description = 'Test attachment';

      service.addAttachment(entryId, file, description).subscribe();

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/ledger/entries/${entryId}/attachments`);
      const formData = req.request.body as FormData;
      
      expect(formData.get('file')).toEqual(file);
      expect(formData.get('description')).toBe(description);
      
      req.flush({ success: true });
    });
  });
});
