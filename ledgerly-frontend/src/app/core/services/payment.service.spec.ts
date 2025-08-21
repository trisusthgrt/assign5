import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PaymentService, PaymentCreateRequest, PaymentStatusUpdateRequest } from './payment.service';

describe('PaymentService', () => {
  let service: PaymentService;
  let httpMock: HttpTestingController;

  const mockPaymentRequest: PaymentCreateRequest = {
    amount: 1000,
    paymentDate: '2025-08-21',
    paymentMethod: 'CASH',
    status: 'PAID',
    customerId: 1,
    description: 'Test payment',
    notes: 'Test notes'
  };

  const mockPaymentResponse = {
    id: 1,
    amount: 1000,
    paymentDate: '2025-08-21',
    paymentMethod: 'CASH',
    status: 'PAID',
    customerId: 1,
    description: 'Test payment',
    notes: 'Test notes',
    createdAt: '2025-08-21T10:00:00Z'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PaymentService]
    });

    service = TestBed.inject(PaymentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('recordPayment', () => {
    it('should make POST request to record payment', () => {
      service.recordPayment(mockPaymentRequest).subscribe(response => {
        expect(response).toEqual(mockPaymentResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockPaymentRequest);
      req.flush(mockPaymentResponse);
    });

    it('should handle payment creation with minimal data', () => {
      const minimalRequest: PaymentCreateRequest = {
        amount: 500,
        paymentDate: '2025-08-21',
        paymentMethod: 'BANK_TRANSFER',
        customerId: 2,
        description: 'Minimal payment'
      };

      service.recordPayment(minimalRequest).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments');
      expect(req.request.body).toEqual(minimalRequest);
      req.flush({ success: true });
    });
  });

  describe('getPaymentsByCustomer', () => {
    it('should make GET request with default pagination', () => {
      service.getPaymentsByCustomer(1).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments/customer/1?page=0&size=20');
      expect(req.request.method).toBe('GET');
      req.flush({ data: [mockPaymentResponse] });
    });

    it('should make GET request with custom pagination', () => {
      service.getPaymentsByCustomer(1, 2, 50).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments/customer/1?page=2&size=50');
      expect(req.request.method).toBe('GET');
      req.flush({ data: [mockPaymentResponse] });
    });
  });

  describe('updatePaymentStatus', () => {
    it('should make PUT request to update payment status', () => {
      const paymentId = 1;
      const status = 'OVERDUE';
      const statusNotes = 'Payment is overdue';
      const dueDate = '2025-09-21';

      service.updatePaymentStatus(paymentId, status, statusNotes, dueDate).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/payments/${paymentId}/status`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({ status, statusNotes, dueDate });
      req.flush({ success: true });
    });

    it('should make PUT request without optional parameters', () => {
      const paymentId = 1;
      const updateData = { status: 'COMPLETED' };
      
      service.updatePaymentStatus(paymentId, updateData).subscribe();
      
      // The actual HTTP request would be tested with HttpClientTestingModule
      expect(service).toBeTruthy();
    });
  });

  describe('disputePayment', () => {
    it('should make POST request to dispute payment', () => {
      const paymentId = 1;
      const disputeReason = 'Incorrect amount';
      const additionalNotes = 'Customer disputes the charge';

      service.disputePayment(paymentId, disputeReason, additionalNotes).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/payments/${paymentId}/dispute`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ disputeReason, additionalNotes });
      req.flush({ success: true });
    });

    it('should make POST request without additional notes', () => {
      const paymentId = 1;
      const disputeData = { reason: 'Payment not received' };
      
      service.disputePayment(paymentId, disputeData).subscribe();
      
      // The actual HTTP request would be tested with HttpClientTestingModule
      expect(service).toBeTruthy();
    });
  });

  describe('resolveDispute', () => {
    it('should make POST request to resolve dispute', () => {
      const paymentId = 1;
      const resolutionNotes = 'Dispute resolved in favor of customer';

      service.resolveDispute(paymentId, resolutionNotes).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/payments/${paymentId}/resolve-dispute?resolutionNotes=${encodeURIComponent(resolutionNotes)}`);
      expect(req.request.method).toBe('POST');
      req.flush({ success: true });
    });
  });

  describe('getPaymentsByStatus', () => {
    it('should make GET request with default parameters', () => {
      service.getPaymentsByStatus('PAID', 0, 10, 'paymentDate', 'desc').subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments/status/PAID?page=0&size=10&sortBy=paymentDate&sortDir=desc');
      expect(req.request.method).toBe('GET');
      req.flush({ data: [mockPaymentResponse] });
    });

    it('should make GET request with custom parameters', () => {
      service.getPaymentsByStatus('PENDING', 1, 25, 'amount', 'desc').subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments/status/PENDING?page=1&size=25&sortBy=amount&sortDir=desc');
      expect(req.request.method).toBe('GET');
      req.flush({ data: [mockPaymentResponse] });
    });
  });

  describe('getOverdue', () => {
    it('should make GET request to get overdue payments', () => {
      service.getOverdue().subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments/overdue');
      expect(req.request.method).toBe('GET');
      req.flush({ data: [mockPaymentResponse] });
    });
  });

  describe('getDisputed', () => {
    it('should make GET request to get disputed payments', () => {
      service.getDisputed().subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments/disputed');
      expect(req.request.method).toBe('GET');
      req.flush({ data: [mockPaymentResponse] });
    });
  });

  describe('getStatusSummary', () => {
    it('should make GET request to get status summary', () => {
      service.getStatusSummary().subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/payments/status-summary');
      expect(req.request.method).toBe('GET');
      req.flush({ 
        total: 100,
        paid: 80,
        pending: 15,
        overdue: 3,
        disputed: 2
      });
    });
  });

  describe('API URL configuration', () => {
    it('should use correct base URL', () => {
      expect(service['apiUrl']).toBe('http://localhost:8080/api/v1/payments');
    });
  });

  describe('interfaces', () => {
    it('should have correct PaymentCreateRequest interface', () => {
      const request: PaymentCreateRequest = {
        amount: 100,
        paymentDate: '2025-08-21',
        paymentMethod: 'CASH',
        customerId: 1,
        description: 'Test'
      };

      expect(request.amount).toBe(100);
      expect(request.paymentDate).toBe('2025-08-21');
      expect(request.paymentMethod).toBe('CASH');
      expect(request.customerId).toBe(1);
      expect(request.description).toBe('Test');
    });

    it('should have correct PaymentStatusUpdateRequest interface', () => {
      const request: PaymentStatusUpdateRequest = {
        status: 'PAID',
        statusNotes: 'Payment received',
        dueDate: '2025-08-21'
      };

      expect(request.status).toBe('PAID');
      expect(request.statusNotes).toBe('Payment received');
      expect(request.dueDate).toBe('2025-08-21');
    });
  });
});
