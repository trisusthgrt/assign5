import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Use the direct, synchronous check instead of the observable
  if (authService.isAuthenticated()) {
    return true; // User is logged in and token is valid, allow access.
  }

  // If the user is not authenticated, redirect to the login page.
  router.navigate(['/auth/login']);
  return false; // Block access to the protected route.
};