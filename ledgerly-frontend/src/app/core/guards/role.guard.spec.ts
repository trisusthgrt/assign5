import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/user.model';

describe('roleGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getUserRole']);
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
    expect(roleGuard).toBeTruthy();
  });

  it('should allow access when user has required ADMIN role', () => {
    authService.getUserRole.and.returnValue(Role.ADMIN);
    const mockRoute = { data: { roles: [Role.ADMIN] } } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should allow access when user has required OWNER role', () => {
    authService.getUserRole.and.returnValue(Role.OWNER);
    const mockRoute = { data: { roles: [Role.OWNER] } } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should allow access when user has required STAFF role', () => {
    authService.getUserRole.and.returnValue(Role.STAFF);
    const mockRoute = { data: { roles: [Role.STAFF] } } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should allow access when user has one of multiple required roles', () => {
    authService.getUserRole.and.returnValue(Role.ADMIN);
    const mockRoute = { data: { roles: [Role.ADMIN, Role.OWNER] } } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should deny access when user has no role', () => {
    authService.getUserRole.and.returnValue(null);
    const mockRoute = { data: { roles: [Role.ADMIN] } } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(false);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should deny access when user role does not match required roles', () => {
    authService.getUserRole.and.returnValue(Role.STAFF);
    const mockRoute = { data: { roles: [Role.ADMIN, Role.OWNER] } } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(false);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should deny access when route has no roles defined', () => {
    authService.getUserRole.and.returnValue(Role.ADMIN);
    const mockRoute = { data: {} } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(false);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should deny access when route has empty roles array', () => {
    authService.getUserRole.and.returnValue(Role.ADMIN);
    const mockRoute = { data: { roles: [] } } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(false);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should handle route with undefined data', () => {
    authService.getUserRole.and.returnValue(Role.ADMIN);
    const mockRoute = { data: undefined } as any;
    const mockState = {} as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(false);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should work with complex route configurations', () => {
    authService.getUserRole.and.returnValue(Role.OWNER);
    const mockRoute = { 
      data: { 
        roles: [Role.OWNER, Role.ADMIN],
        title: 'Admin Panel',
        requiresAuth: true
      },
      url: '/admin/dashboard',
      params: { section: 'users' }
    } as any;
    const mockState = { url: '/admin/dashboard' } as any;

    const result = TestBed.runInInjectionContext(() => 
      roleGuard(mockRoute, mockState)
    );

    expect(result).toBe(true);
    expect(authService.getUserRole).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should handle authentication service errors gracefully', () => {
    authService.getUserRole.and.throwError('Service error');
    const mockRoute = { data: { roles: [Role.ADMIN] } } as any;
    const mockState = {} as any;

    expect(() => {
      TestBed.runInInjectionContext(() => 
        roleGuard(mockRoute, mockState)
      );
    }).toThrow('Service error');
  });
});
