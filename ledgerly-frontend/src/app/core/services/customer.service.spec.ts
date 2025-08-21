import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CustomerService } from './customer.service';
import { Customer } from '../models/customer.model';
import { ApiResponse } from '../models/api-response.model';

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;

  const mockCustomer: Customer = {
    id: 1,
    name: 'Test Customer',
    email: 'test@example.com',
    phoneNumber: '1234567890',
    businessName: 'Test Business',
    address: 'Test Address',
    shopId: 1,
    shopName: 'Test Shop',
    relationshipType: 'CUSTOMER',
    notes: 'Test notes',
    creditLimit: 1000,
    currentBalance: 500,
    isActive: true
  };

  const mockApiResponse: ApiResponse<Customer[]> = {
    success: true,
    message: 'Customers retrieved successfully',
    data: [mockCustomer]
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomerService]
    });

    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCustomers', () => {
    it('should make GET request to customers endpoint', () => {
      service.getCustomers().subscribe(response => {
        expect(response).toEqual(mockApiResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/customers');
      expect(req.request.method).toBe('GET');
      req.flush(mockApiResponse);
    });

    it('should handle empty response', () => {
      const emptyResponse: ApiResponse<Customer[]> = {
        success: true,
        message: 'No customers found',
        data: []
      };

      service.getCustomers().subscribe(response => {
        expect(response).toEqual(emptyResponse);
        expect(response.data).toEqual([]);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/customers');
      req.flush(emptyResponse);
    });

    it('should handle error response', () => {
      const errorResponse: ApiResponse<Customer[]> = {
        success: false,
        message: 'Error retrieving customers',
        data: []
      };

      service.getCustomers().subscribe(response => {
        expect(response).toEqual(errorResponse);
        expect(response.success).toBeFalse();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/customers');
      req.flush(errorResponse);
    });
  });

  describe('createCustomer', () => {
    it('should make POST request to create customer', () => {
      const customerData = {
        name: 'New Customer',
        email: 'new@example.com',
        phoneNumber: '9876543210',
        businessName: 'New Business',
        relationshipType: 'CUSTOMER'
      };

      const createResponse: ApiResponse<Customer> = {
        success: true,
        message: 'Customer created successfully',
        data: { ...mockCustomer, ...customerData, id: 2 }
      };

      service.createCustomer(customerData).subscribe(response => {
        expect(response).toEqual(createResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/customers');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(customerData);
      req.flush(createResponse);
    });

    it('should handle validation errors', () => {
      const invalidData = {
        name: '',
        email: 'invalid-email'
      };

      const errorResponse: ApiResponse<Customer> = {
        success: false,
        message: 'Validation failed',
        data: undefined
      };

      service.createCustomer(invalidData).subscribe(response => {
        expect(response).toEqual(errorResponse);
        expect(response.success).toBeFalse();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/customers');
      req.flush(errorResponse);
    });
  });

  describe('updateCustomer', () => {
    it('should make PUT request to update customer', () => {
      const customerId = 1;
      const updateData = {
        name: 'Updated Customer',
        email: 'updated@example.com'
      };

      const updateResponse: ApiResponse<Customer> = {
        success: true,
        message: 'Customer updated successfully',
        data: { ...mockCustomer, ...updateData }
      };

      service.updateCustomer(customerId, updateData).subscribe(response => {
        expect(response).toEqual(updateResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/customers/${customerId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updateResponse);
    });

    it('should handle customer not found', () => {
      const customerId = 999;
      const updateData = { name: 'Updated Customer' };

      const errorResponse: ApiResponse<Customer> = {
        success: false,
        message: 'Customer not found',
        data: undefined
      };

      service.updateCustomer(customerId, updateData).subscribe(response => {
        expect(response).toEqual(errorResponse);
        expect(response.success).toBeFalse();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/customers/${customerId}`);
      req.flush(errorResponse);
    });
  });

  describe('deleteCustomer', () => {
    it('should make DELETE request to delete customer', () => {
      const customerId = 1;

      const deleteResponse: ApiResponse<void> = {
        success: true,
        message: 'Customer deleted successfully',
        data: undefined
      };

      service.deleteCustomer(customerId).subscribe(response => {
        expect(response).toEqual(deleteResponse);
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/customers/${customerId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(deleteResponse);
    });

    it('should handle deletion of non-existent customer', () => {
      const customerId = 999;

      const errorResponse: ApiResponse<void> = {
        success: false,
        message: 'Customer not found',
        data: undefined
      };

      service.deleteCustomer(customerId).subscribe(response => {
        expect(response).toEqual(errorResponse);
        expect(response.success).toBeFalse();
      });

      const req = httpMock.expectOne(`http://localhost:8080/api/v1/customers/${customerId}`);
      req.flush(errorResponse);
    });
  });

  describe('API URL configuration', () => {
    it('should use correct base URL', () => {
      expect(service['apiUrl']).toBe('http://localhost:8080/api/v1/customers');
    });
  });
});
