import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandler, HttpEvent, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let mockHandler: jasmine.SpyObj<HttpHandler>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getToken']);
    const handlerSpy = jasmine.createSpyObj('HttpHandler', ['handle']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authSpy }
      ]
    });

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    mockHandler = handlerSpy;
  });

  it('should be created', () => {
    expect(authInterceptor).toBeTruthy();
  });

  it('should add Authorization header when token exists', () => {
    const token = 'mock-jwt-token';
    authService.getToken.and.returnValue(token);

    const mockRequest = new HttpRequest('GET', '/api/test');
    const mockResponse = new HttpResponse({ body: 'test response' });
    mockHandler.handle.and.returnValue(of(mockResponse));

    TestBed.runInInjectionContext(() => {
      const result = authInterceptor(mockRequest, mockHandler.handle);
      
      result.subscribe(response => {
        expect(response).toEqual(mockResponse);
      });
    });

    expect(authService.getToken).toHaveBeenCalled();
    expect(mockHandler.handle).toHaveBeenCalled();
    
    const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
    expect(capturedRequest.headers.get('Authorization')).toBe(`Bearer ${token}`);
  });

  it('should not add Authorization header when no token exists', () => {
    authService.getToken.and.returnValue(null);

    const mockRequest = new HttpRequest('GET', '/api/test');
    const mockResponse = new HttpResponse({ body: 'test response' });
    mockHandler.handle.and.returnValue(of(mockResponse));

    TestBed.runInInjectionContext(() => {
      const result = authInterceptor(mockRequest, mockHandler.handle);
      
      result.subscribe(response => {
        expect(response).toEqual(mockResponse);
      });
    });

    expect(authService.getToken).toHaveBeenCalled();
    expect(mockHandler.handle).toHaveBeenCalled();
    
    const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
    expect(capturedRequest.headers.get('Authorization')).toBeNull();
  });

  it('should preserve existing headers when adding Authorization', () => {
    const token = 'mock-jwt-token';
    authService.getToken.and.returnValue(token);

    const existingHeaders = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    const mockRequest = new HttpRequest('POST', '/api/test', {}, { headers: existingHeaders });
    const mockResponse = new HttpResponse({ body: 'test response' });
    mockHandler.handle.and.returnValue(of(mockResponse));

    TestBed.runInInjectionContext(() => {
      const result = authInterceptor(mockRequest, mockHandler.handle);
      
      result.subscribe(response => {
        expect(response).toEqual(mockResponse);
      });
    });

    const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
    expect(capturedRequest.headers.get('Authorization')).toBe(`Bearer ${token}`);
    expect(capturedRequest.headers.get('Content-Type')).toBe('application/json');
    expect(capturedRequest.headers.get('Accept')).toBe('application/json');
  });

  it('should handle different HTTP methods', () => {
    const token = 'mock-jwt-token';
    authService.getToken.and.returnValue(token);

    const methods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'];
    
    methods.forEach(method => {
      const mockRequest = new HttpRequest(method as any, '/api/test');
      const mockResponse = new HttpResponse({ body: `${method} response` });
      mockHandler.handle.and.returnValue(of(mockResponse));

      TestBed.runInInjectionContext(() => {
        const result = authInterceptor(mockRequest, mockHandler.handle);
        
        result.subscribe(response => {
          expect(response).toEqual(mockResponse);
        });
      });

      const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
      expect(capturedRequest.headers.get('Authorization')).toBe(`Bearer ${token}`);
    });
  });

  it('should handle requests with different URLs', () => {
    const token = 'mock-jwt-token';
    authService.getToken.and.returnValue(token);

    const urls = [
      '/api/v1/auth/login',
      '/api/v1/customers',
      '/api/v1/payments',
      '/api/v1/admin/users'
    ];
    
    urls.forEach(url => {
      const mockRequest = new HttpRequest('GET', url);
      const mockResponse = new HttpResponse({ body: `Response from ${url}` });
      mockHandler.handle.and.returnValue(of(mockResponse));

      TestBed.runInInjectionContext(() => {
        const result = authInterceptor(mockRequest, mockHandler.handle);
        
        result.subscribe(response => {
          expect(response).toEqual(mockResponse);
        });
      });

      const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
      expect(capturedRequest.headers.get('Authorization')).toBe(`Bearer ${token}`);
    });
  });

  it('should handle requests with body data', () => {
    const token = 'mock-jwt-token';
    authService.getToken.and.returnValue(token);

    const bodyData = { name: 'Test User', email: 'test@example.com' };
    const mockRequest = new HttpRequest('POST', '/api/test', bodyData);
    const mockResponse = new HttpResponse({ body: 'test response' });
    mockHandler.handle.and.returnValue(of(mockResponse));

    TestBed.runInInjectionContext(() => {
      const result = authInterceptor(mockRequest, mockHandler.handle);
      
      result.subscribe(response => {
        expect(response).toEqual(mockResponse);
      });
    });

    const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
    expect(capturedRequest.headers.get('Authorization')).toBe(`Bearer ${token}`);
    expect(capturedRequest.body).toEqual(bodyData);
  });

  it('should handle requests with query parameters', () => {
    const token = 'mock-jwt-token';
    authService.getToken.and.returnValue(token);

    const mockRequest = new HttpRequest('GET', '/api/test?page=1&size=10&sort=name');
    const mockResponse = new HttpResponse({ body: 'test response' });
    mockHandler.handle.and.returnValue(of(mockResponse));

    TestBed.runInInjectionContext(() => {
      const result = authInterceptor(mockRequest, mockHandler.handle);
      
      result.subscribe(response => {
        expect(response).toEqual(mockResponse);
      });
    });

    const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
    expect(capturedRequest.headers.get('Authorization')).toBe(`Bearer ${token}`);
    expect(capturedRequest.url).toBe('/api/test?page=1&size=10&sort=name');
  });

  it('should handle empty token string', () => {
    authService.getToken.and.returnValue('');

    const mockRequest = new HttpRequest('GET', '/api/test');
    const mockResponse = new HttpResponse({ body: 'test response' });
    mockHandler.handle.and.returnValue(of(mockResponse));

    TestBed.runInInjectionContext(() => {
      const result = authInterceptor(mockRequest, mockHandler.handle);
      
      result.subscribe(response => {
        expect(response).toEqual(mockResponse);
      });
    });

    const capturedRequest = mockHandler.handle.calls.mostRecent().args[0] as HttpRequest<any>;
    expect(capturedRequest.headers.get('Authorization')).toBeNull();
  });

  it('should handle authentication service errors gracefully', () => {
    authService.getToken.and.throwError('Service error');

    const mockRequest = new HttpRequest('GET', '/api/test');
    const mockResponse = new HttpResponse({ body: 'test response' });
    mockHandler.handle.and.returnValue(of(mockResponse));

    expect(() => {
      TestBed.runInInjectionContext(() => {
        authInterceptor(mockRequest, mockHandler.handle);
      });
    }).toThrow('Service error');
  });
});
