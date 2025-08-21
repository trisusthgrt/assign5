import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthResponse } from '../../../core/models/auth.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
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

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['login']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        BrowserAnimationsModule,
        MatSnackBarModule
      ],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.loginForm).toBeTruthy();
    expect(component.loginForm.get('usernameOrEmail')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
    expect(component.isSubmitting).toBeFalse();
    expect(component.errorMessage).toBe('');
  });

  it('should have required validators on form controls', () => {
    const usernameControl = component.loginForm.get('usernameOrEmail');
    const passwordControl = component.loginForm.get('password');

    expect(usernameControl?.hasValidator(Validators.required)).toBeTrue();
    expect(passwordControl?.hasValidator(Validators.required)).toBeTrue();
  });

  it('should mark form as invalid when fields are empty', () => {
    expect(component.loginForm.valid).toBeFalse();
    expect(component.loginForm.get('usernameOrEmail')?.valid).toBeFalse();
    expect(component.loginForm.get('password')?.valid).toBeFalse();
  });

  it('should mark form as valid when fields are filled', () => {
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    expect(component.loginForm.valid).toBeTrue();
  });

  it('should call authService.login when form is submitted', () => {
    authService.login.and.returnValue(of(mockAuthResponse));
    
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });
  });

  it('should navigate to dashboard on successful login', () => {
    authService.login.and.returnValue(of(mockAuthResponse));
    
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should set isSubmitting to true during login', () => {
    authService.login.and.returnValue(of(mockAuthResponse));
    
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.isSubmitting).toBeTrue();
  });

  it('should handle login error', () => {
    const errorMessage = 'Invalid credentials';
    authService.login.and.returnValue(throwError(() => ({ error: { message: errorMessage } })));
    
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe(errorMessage);
    expect(component.isSubmitting).toBeFalse();
  });

  it('should handle network error', () => {
    authService.login.and.returnValue(throwError(() => new Error('Network error')));
    
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('An error occurred during login. Please try again.');
    expect(component.isSubmitting).toBeFalse();
  });

  it('should not submit form when invalid', () => {
    component.onSubmit();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should not submit form when already submitting', () => {
    component.isSubmitting = true;
    
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should display error message for invalid username/email field', () => {
    const usernameControl = component.loginForm.get('usernameOrEmail');
    usernameControl?.markAsTouched();
    usernameControl?.setValue('');

    fixture.detectChanges();

    const errorElement = fixture.nativeElement.querySelector('.error-message');
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent).toContain('Username or email is required');
  });

  it('should display error message for invalid password field', () => {
    const passwordControl = component.loginForm.get('password');
    passwordControl?.markAsTouched();
    passwordControl?.setValue('');

    fixture.detectChanges();

    const errorElement = fixture.nativeElement.querySelector('.error-message');
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent).toContain('Password is required');
  });

  it('should apply error class to input fields when invalid', () => {
    const usernameControl = component.loginForm.get('usernameOrEmail');
    usernameControl?.markAsTouched();
    usernameControl?.setValue('');

    fixture.detectChanges();

    const inputElement = fixture.nativeElement.querySelector('#usernameOrEmail');
    expect(inputElement.classList.contains('error')).toBeTrue();
  });

  it('should disable submit button when form is invalid', () => {
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBeTrue();
  });

  it('should disable submit button when submitting', () => {
    component.isSubmitting = true;
    fixture.detectChanges();

    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBeTrue();
  });

  it('should show loading state in submit button', () => {
    component.isSubmitting = true;
    fixture.detectChanges();

    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.textContent).toContain('Signing In...');
  });

  it('should show normal state in submit button when not submitting', () => {
    component.isSubmitting = false;
    fixture.detectChanges();

    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.textContent).toContain('Sign In');
  });

  it('should have correct form structure', () => {
    const form = fixture.nativeElement.querySelector('form');
    const usernameInput = form.querySelector('#usernameOrEmail');
    const passwordInput = form.querySelector('#password');
    const submitButton = form.querySelector('button[type="submit"]');

    expect(form).toBeTruthy();
    expect(usernameInput).toBeTruthy();
    expect(passwordInput).toBeTruthy();
    expect(submitButton).toBeTruthy();
  });

  it('should have correct navigation links', () => {
    const registerLink = fixture.nativeElement.querySelector('a[routerLink="/auth/register"]');
    const changePasswordLink = fixture.nativeElement.querySelector('a[routerLink="/auth/change-password"]');

    expect(registerLink).toBeTruthy();
    expect(changePasswordLink).toBeTruthy();
    expect(registerLink.textContent).toContain('Register here');
    expect(changePasswordLink.textContent).toContain('Change Password');
  });

  it('should display error message when errorMessage is set', () => {
    component.errorMessage = 'Test error message';
    fixture.detectChanges();

    const errorElement = fixture.nativeElement.querySelector('.alert.alert-error');
    expect(errorElement).toBeTruthy();
    expect(errorElement.textContent).toContain('Test error message');
  });

  it('should clear error message on new login attempt', () => {
    component.errorMessage = 'Previous error';
    
    authService.login.and.returnValue(of(mockAuthResponse));
    
    component.loginForm.patchValue({
      usernameOrEmail: 'testuser',
      password: 'password123'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('');
  });

  it('should handle form submission with different username formats', () => {
    const testCases = [
      { username: 'testuser', expected: 'testuser' },
      { username: 'test@example.com', expected: 'test@example.com' },
      { username: 'user123', expected: 'user123' }
    ];

    testCases.forEach(testCase => {
      authService.login.and.returnValue(of(mockAuthResponse));
      
      component.loginForm.patchValue({
        usernameOrEmail: testCase.username,
        password: 'password123'
      });

      component.onSubmit();

      expect(authService.login).toHaveBeenCalledWith({
        usernameOrEmail: testCase.expected,
        password: 'password123'
      });
    });
  });
});
