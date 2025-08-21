# Frontend Testing Guide

This document provides comprehensive information about testing the Ledgerly frontend application.

## Overview

The frontend testing suite is built with:
- **Jasmine** - Testing framework
- **Karma** - Test runner
- **Angular Testing Utilities** - Component and service testing
- **Coverage Reporting** - Code coverage analysis

## Test Coverage Target

**Target: >70% test coverage**
- Statements: 70%
- Branches: 70%
- Functions: 70%
- Lines: 70%

## Running Tests

### Quick Start
```bash
# Run tests once with coverage
ng test --code-coverage --watch=false

# Run tests in watch mode (development)
ng test

# Run tests with specific browser
ng test --browsers=ChromeHeadless
```

### Windows Users
```bash
# Use the provided batch file
run-tests.bat
```

### Coverage Report
After running tests with coverage, view the report at:
```
ledgerly-frontend/coverage/ledgerly-frontend/index.html
```

## Test Structure

### Services Tests
- **AuthService** - Authentication and authorization logic
- **StorageService** - Local storage operations
- **CustomerService** - Customer CRUD operations
- **PaymentService** - Payment management
- **LedgerService** - Ledger entry management

### Guards Tests
- **AuthGuard** - Route protection based on authentication
- **RoleGuard** - Route protection based on user roles

### Interceptors Tests
- **AuthInterceptor** - JWT token injection

### Component Tests
- **AppComponent** - Main application component
- **LoginComponent** - User authentication
- **CustomerManagementComponent** - Customer operations

## Test Categories

### Unit Tests
- Individual service methods
- Component logic
- Guard logic
- Interceptor behavior

### Integration Tests
- Service interactions
- Component-service communication
- HTTP request handling

### Edge Cases
- Error handling
- Boundary conditions
- Invalid input validation
- Network failures

## Writing New Tests

### Service Test Template
```typescript
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { YourService } from './your.service';

describe('YourService', () => {
  let service: YourService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [YourService]
    });

    service = TestBed.inject(YourService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // Add your test cases here
});
```

### Component Test Template
```typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { YourComponent } from './your.component';

describe('YourComponent', () => {
  let component: YourComponent;
  let fixture: ComponentFixture<YourComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [YourComponent, ReactiveFormsModule],
      // Add other necessary imports and providers
    }).compileComponents();

    fixture = TestBed.createComponent(YourComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Add your test cases here
});
```

## Mocking Strategies

### HTTP Requests
```typescript
// Mock successful response
const mockResponse = { data: 'test' };
httpMock.expectOne('/api/endpoint').flush(mockResponse);

// Mock error response
httpMock.expectOne('/api/endpoint').error(new ErrorEvent('Network error'));
```

### Services
```typescript
const mockService = jasmine.createSpyObj('ServiceName', ['method1', 'method2']);
mockService.method1.and.returnValue('mocked value');
```

### Observables
```typescript
import { of, throwError } from 'rxjs';

// Mock success
mockService.method.and.returnValue(of({ success: true }));

// Mock error
mockService.method.and.returnValue(throwError(() => new Error('Error message')));
```

## Common Testing Patterns

### Form Validation Testing
```typescript
it('should validate required fields', () => {
  const control = component.form.get('fieldName');
  control?.setValue('');
  expect(control?.valid).toBeFalse();
  
  control?.setValue('valid value');
  expect(control?.valid).toBeTrue();
});
```

### Async Operation Testing
```typescript
it('should handle async operations', (done) => {
  service.asyncMethod().subscribe(result => {
    expect(result).toBeTruthy();
    done();
  });
});
```

### Error Handling Testing
```typescript
it('should handle errors gracefully', () => {
  service.methodThatThrows().subscribe({
    error: (error) => {
      expect(error).toBeTruthy();
      expect(component.errorMessage).toBe('Expected error message');
    }
  });
});
```

## Best Practices

1. **Test Isolation**: Each test should be independent
2. **Descriptive Names**: Use clear, descriptive test names
3. **Arrange-Act-Assert**: Structure tests in this pattern
4. **Mock External Dependencies**: Don't test external services
5. **Cover Edge Cases**: Test error conditions and boundaries
6. **Clean Setup/Teardown**: Use beforeEach/afterEach properly

## Troubleshooting

### Common Issues

1. **Test Timeouts**: Increase timeout in karma.conf.js
2. **Import Errors**: Ensure all dependencies are properly imported
3. **Async Issues**: Use done() callback or fakeAsync for async tests
4. **Component Rendering**: Use fixture.detectChanges() after state changes

### Debug Mode
```bash
# Run tests in debug mode
ng test --browsers=Chrome --single-run=false
```

## Coverage Analysis

### Understanding Coverage
- **Statements**: Individual code statements executed
- **Branches**: Conditional branches taken
- **Functions**: Functions called during execution
- **Lines**: Lines of code executed

### Improving Coverage
1. Add tests for untested code paths
2. Test error handling branches
3. Test edge cases and boundary conditions
4. Mock external dependencies properly

## Continuous Integration

Tests are configured to run automatically in CI/CD pipelines with:
- Headless Chrome browser
- Coverage reporting
- Fail-fast on test failures
- Coverage threshold enforcement

## Resources

- [Angular Testing Guide](https://angular.io/guide/testing)
- [Jasmine Documentation](https://jasmine.github.io/)
- [Karma Configuration](https://karma-runner.github.io/latest/config/configuration-file.html)
- [Angular Material Testing](https://material.angular.io/guide/testing)
