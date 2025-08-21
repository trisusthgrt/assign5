import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be created', () => {
    expect(authGuard).toBeTruthy();
  });

  it('should allow access when user is authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);

    const result = TestBed.runInInjectionContext(() => 
      authGuard({} as any, {} as any)
    );

    expect(result).toBe(true);
    expect(authService.isAuthenticated).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should deny access and redirect to login when user is not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);

    const result = TestBed.runInInjectionContext(() => 
      authGuard({} as any, {} as any)
    );

    expect(result).toBe(false);
    expect(authService.isAuthenticated).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
  });

  it('should handle route and state parameters', () => {
    authService.isAuthenticated.and.returnValue(true);
    const mockRoute = { url: '/dashboard' } as any;
    const mockState = { url: '/dashboard' } as any;

    const result = TestBed.runInInjectionContext(() => 
      authGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
    expect(authService.isAuthenticated).toHaveBeenCalled();
  });

  it('should work with different route configurations', () => {
    authService.isAuthenticated.and.returnValue(true);
    const mockRoute = { 
      url: '/admin/users',
      params: { id: '123' },
      queryParams: { page: '1' }
    } as any;
    const mockState = { url: '/admin/users' } as any;

    const result = TestBed.runInInjectionContext(() => 
      authGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
  });

  it('should handle authentication service errors gracefully', () => {
    authService.isAuthenticated.and.throwError('Service error');

    expect(() => {
      TestBed.runInInjectionContext(() => 
        authGuard({} as any, {} as any)
      );
    }).toThrow('Service error');
  });

  it('should work with nested routes', () => {
    authService.isAuthenticated.and.returnValue(true);
    const mockRoute = { 
      url: '/owner/shops/1/customers',
      params: { shopId: '1' }
    } as any;
    const mockState = { url: '/owner/shops/1/customers' } as any;

    const result = TestBed.runInInjectionContext(() => 
      authGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
  });
});
