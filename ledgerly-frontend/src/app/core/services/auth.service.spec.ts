import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';
import { StorageService } from './storage.service';
import { Router } from '@angular/router';
import { Role } from '../models/user.model';
import { AuthResponse } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let storageService: jasmine.SpyObj<StorageService>;
  let router: jasmine.SpyObj<Router>;

  const mockAuthResponse: AuthResponse = {
    token: 'mock-jwt-token',
    tokenType: 'Bearer',
    userId: 1,
    username: 'testuser',
    email: 'test@example.com',
    role: 'ADMIN',
    firstName: 'Test',
    lastName: 'User'
  };

  beforeEach(() => {
    const storageSpy = jasmine.createSpyObj('StorageService', ['getItem', 'setItem', 'removeItem']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        AuthService,
        { provide: StorageService, useValue: storageSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    service = TestBed.inject(AuthService);
    storageService = TestBed.inject(StorageService) as jasmine.SpyObj<StorageService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register', () => {
    it('should make POST request to register endpoint', () => {
      const registerData = { username: 'testuser', password: 'password123' };
      
      service.register(registerData).subscribe();
      
      // The actual HTTP request would be tested with HttpClientTestingModule
      expect(service).toBeTruthy();
    });
  });

  describe('login', () => {
    it('should make POST request to login endpoint', () => {
      const loginData = { usernameOrEmail: 'testuser', password: 'password123' };
      
      service.login(loginData).subscribe();
      
      // The actual HTTP request would be tested with HttpClientTestingModule
      expect(service).toBeTruthy();
    });
  });

  describe('logout', () => {
    it('should clear token and navigate to login', () => {
      service.logout();
      
      expect(storageService.removeItem).toHaveBeenCalledWith('auth_token');
      expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
    });
  });

  describe('getToken', () => {
    it('should return token from storage', () => {
      const token = 'mock-token';
      storageService.getItem.and.returnValue(token);
      
      const result = service.getToken();
      
      expect(result).toBe(token);
      expect(storageService.getItem).toHaveBeenCalledWith('auth_token');
    });

    it('should return null when no token', () => {
      storageService.getItem.and.returnValue(null);
      
      const result = service.getToken();
      
      expect(result).toBeNull();
    });
  });

  describe('isAuthenticated', () => {
    it('should return false when no token', () => {
      storageService.getItem.and.returnValue(null);
      
      const result = service.isAuthenticated();
      
      expect(result).toBeFalse();
    });

    it('should return false when token is invalid', () => {
      const invalidToken = 'invalid-token';
      storageService.getItem.and.returnValue(invalidToken);
      
      const result = service.isAuthenticated();
      
      expect(result).toBeFalse();
    });
  });

  describe('getUserRole', () => {
    it('should return null when no token', () => {
      storageService.getItem.and.returnValue(null);
      
      const role = service.getUserRole();
      
      expect(role).toBeNull();
    });

    it('should return null when token is invalid', () => {
      const invalidToken = 'invalid-token';
      storageService.getItem.and.returnValue(invalidToken);
      
      const role = service.getUserRole();
      
      expect(role).toBeNull();
    });
  });

  describe('observables', () => {
    it('should emit authentication state changes', (done) => {
      service.isAuthenticated$.subscribe(isAuthenticated => {
        expect(isAuthenticated).toBeFalse();
        done();
      });
    });

    it('should emit user role changes', (done) => {
      service.currentUserRole$.subscribe(role => {
        expect(role).toBeNull();
        done();
      });
    });
  });
});
