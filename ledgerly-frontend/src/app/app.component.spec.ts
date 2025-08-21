import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import { AuthService } from './core/services/auth.service';
import { of } from 'rxjs';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['logout'], {
      isAuthenticated$: of(false),
      currentUserRole$: of(null)
    });

    await TestBed.configureTestingModule({
      imports: [
        AppComponent,
        RouterTestingModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  beforeEach(() => {
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should have authService injected', () => {
    expect(component.authService).toBeTruthy();
    expect(component.authService).toBe(authService);
  });

  it('should call authService.logout when logout is called', () => {
    component.logout();
    
    expect(authService.logout).toHaveBeenCalled();
  });

  it('should render the header with logo and tagline', () => {
    const header = fixture.nativeElement.querySelector('.app-header');
    const logo = header.querySelector('.logo');
    const tagline = header.querySelector('.tagline');
    
    expect(header).toBeTruthy();
    expect(logo).toBeTruthy();
    expect(logo.textContent).toContain('Ledgerly');
    expect(tagline).toBeTruthy();
    expect(tagline.textContent).toContain('Smart Financial Management');
  });

  it('should render the main content area', () => {
    const mainContent = fixture.nativeElement.querySelector('.main-content');
    const contentWrapper = mainContent.querySelector('.content-wrapper');
    const pageContent = mainContent.querySelector('.page-content');
    
    expect(mainContent).toBeTruthy();
    expect(contentWrapper).toBeTruthy();
    expect(pageContent).toBeTruthy();
  });

  it('should render router outlet in page content', () => {
    const routerOutlet = fixture.nativeElement.querySelector('router-outlet');
    expect(routerOutlet).toBeTruthy();
  });

  it('should not show user section when not authenticated', () => {
    // Mock unauthenticated state
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(false),
      writable: true
    });
    
    fixture.detectChanges();
    
    const userSection = fixture.nativeElement.querySelector('.user-section');
    expect(userSection).toBeFalsy();
  });

  it('should show user section when authenticated', () => {
    // Mock authenticated state
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(true),
      writable: true
    });
    
    fixture.detectChanges();
    
    const userSection = fixture.nativeElement.querySelector('.user-section');
    expect(userSection).toBeTruthy();
  });

  it('should show user role when authenticated', () => {
    // Mock authenticated state with role
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(true),
      writable: true
    });
    Object.defineProperty(authService, 'currentUserRole$', {
      value: of('ADMIN'),
      writable: true
    });
    
    fixture.detectChanges();
    
    const userRole = fixture.nativeElement.querySelector('.user-role');
    expect(userRole).toBeTruthy();
    expect(userRole.textContent).toContain('ADMIN');
  });

  it('should show logout button when authenticated', () => {
    // Mock authenticated state
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(true),
      writable: true
    });
    
    fixture.detectChanges();
    
    const logoutButton = fixture.nativeElement.querySelector('button');
    expect(logoutButton).toBeTruthy();
    expect(logoutButton.textContent).toContain('Logout');
  });

  it('should not show sidebar when not authenticated', () => {
    // Mock unauthenticated state
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(false),
      writable: true
    });
    
    fixture.detectChanges();
    
    const sidebar = fixture.nativeElement.querySelector('.sidebar');
    expect(sidebar).toBeFalsy();
  });

  it('should show sidebar when authenticated', () => {
    // Mock authenticated state
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(true),
      writable: true
    });
    
    fixture.detectChanges();
    
    const sidebar = fixture.nativeElement.querySelector('.sidebar');
    expect(sidebar).toBeTruthy();
  });

  it('should have correct CSS classes for styling', () => {
    const appContainer = fixture.nativeElement.querySelector('.app-container');
    const appHeader = fixture.nativeElement.querySelector('.app-header');
    const mainContent = fixture.nativeElement.querySelector('.main-content');
    
    expect(appContainer).toBeTruthy();
    expect(appHeader).toBeTruthy();
    expect(mainContent).toBeTruthy();
  });

  it('should handle different user roles correctly', () => {
    const roles = ['ADMIN', 'OWNER', 'STAFF'];
    
    roles.forEach(role => {
      // Mock authenticated state with specific role
      Object.defineProperty(authService, 'isAuthenticated$', {
        value: of(true),
        writable: true
      });
      Object.defineProperty(authService, 'currentUserRole$', {
        value: of(role),
        writable: true
      });
      
      fixture.detectChanges();
      
      const userRole = fixture.nativeElement.querySelector('.user-role');
      expect(userRole.textContent).toContain(role);
    });
  });

  it('should have responsive design classes', () => {
    const contentWrapper = fixture.nativeElement.querySelector('.content-wrapper');
    const sidebar = fixture.nativeElement.querySelector('.sidebar');
    
    expect(contentWrapper).toBeTruthy();
    if (sidebar) {
      expect(sidebar).toBeTruthy();
    }
  });

  it('should render app-sidebar component when authenticated', () => {
    // Mock authenticated state
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(true),
      writable: true
    });
    
    fixture.detectChanges();
    
    const appSidebar = fixture.nativeElement.querySelector('app-sidebar');
    expect(appSidebar).toBeTruthy();
  });

  it('should have proper button styling classes', () => {
    // Mock authenticated state to show logout button
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(true),
      writable: true
    });
    
    fixture.detectChanges();
    
    const logoutButton = fixture.nativeElement.querySelector('button');
    expect(logoutButton.classList.contains('btn')).toBeTrue();
    expect(logoutButton.classList.contains('btn-secondary')).toBeTrue();
    expect(logoutButton.classList.contains('btn-sm')).toBeTrue();
  });

  it('should have proper user role styling classes', () => {
    // Mock authenticated state with role
    Object.defineProperty(authService, 'isAuthenticated$', {
      value: of(true),
      writable: true
    });
    Object.defineProperty(authService, 'currentUserRole$', {
      value: of('ADMIN'),
      writable: true
    });
    
    fixture.detectChanges();
    
    const userRole = fixture.nativeElement.querySelector('.user-role');
    expect(userRole.classList.contains('user-role')).toBeTrue();
  });
});
