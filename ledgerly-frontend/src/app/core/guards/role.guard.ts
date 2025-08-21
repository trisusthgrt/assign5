import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/user.model';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const expectedRoles = route.data['roles'] as Role[];
  const userRole = authService.getUserRole();

  if (!userRole || !expectedRoles.includes(userRole)) {
    // Redirect to a default page if the role doesn't match
    // You could create a specific 'unauthorized' component
    router.navigate(['/']); 
    return false;
  }
  
  return true;
};