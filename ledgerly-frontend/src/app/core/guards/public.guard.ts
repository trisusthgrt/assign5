import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/user.model';

export const publicGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Use the direct, synchronous check
  if (authService.isAuthenticated()) {
    // If user is already logged in, redirect them away from login/register pages
    const role = authService.getUserRole();
    
    if (role === Role.ADMIN) {
      router.navigate(['/admin']);
    } else if (role === Role.OWNER) {
      router.navigate(['/owner']);
    } else {
      router.navigate(['/dashboard']);
    }
    
    return false; // Block access to the login/register page
  }

  // If user is not authenticated, allow access to login/register
  return true;
};