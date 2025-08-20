import { Routes } from '@angular/router';
import { ShopsListComponent } from './shops-list/shops-list.component';
import { ShopCreateComponent } from './shop-create/shop-create.component';
import { ShopEditComponent } from './shop-edit/shop-edit.component';

export const SHOPS_ROUTES: Routes = [
  {
    path: '',
    component: ShopsListComponent
  },
  {
    path: 'create',
    component: ShopCreateComponent
  },
  {
    path: 'edit/:id',
    component: ShopEditComponent
  }
];
