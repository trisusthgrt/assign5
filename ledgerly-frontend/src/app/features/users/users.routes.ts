import { Routes } from '@angular/router';
import { UsersListComponent } from './users-list/users-list.component';
import { UserCreateComponent } from './user-create/user-create.component';
import { UserEditComponent } from './user-edit/user-edit.component';

export const USERS_ROUTES: Routes = [
  {
    path: '',
    component: UsersListComponent
  },
  {
    path: 'create',
    component: UserCreateComponent
  },
  {
    path: 'edit/:id',
    component: UserEditComponent
  }
];
