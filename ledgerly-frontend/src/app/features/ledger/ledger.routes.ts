import { Routes } from '@angular/router';
import { LedgerListComponent } from './ledger-list/ledger-list.component';
import { LedgerCreateComponent } from './ledger-create/ledger-create.component';
import { LedgerEditComponent } from './ledger-edit/ledger-edit.component';

export const LEDGER_ROUTES: Routes = [
  {
    path: '',
    component: LedgerListComponent
  },
  {
    path: 'create',
    component: LedgerCreateComponent
  },
  {
    path: 'edit/:id',
    component: LedgerEditComponent
  }
];
