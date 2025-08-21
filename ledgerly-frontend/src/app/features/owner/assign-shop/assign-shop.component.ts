import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { OwnerService } from '../../../core/services/owner.service';
import { BasicUser } from '../../../core/models/user.model';
import { Shop } from '../../../core/models/shop.model';

@Component({
  selector: 'app-assign-shop',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './assign-shop.component.html',
  styleUrl: './assign-shop.component.css'
})
export class AssignShopComponent implements OnInit {
  ownerService = inject(OwnerService);
  fb = inject(FormBuilder);

  staffList: BasicUser[] = [];
  shops: Shop[] = [];
  successMessage: string | null = null;
  errorMessage: string | null = null;

  assignForm = this.fb.group({
    staffId: [null, Validators.required],
    shopId: [null, Validators.required],
  });

  ngOnInit(): void {
    this.ownerService.getStaff().subscribe(data => this.staffList = data);
    this.ownerService.getMyShops().subscribe(response => this.shops = response.shops);
  }

  onSubmit(): void {
    if (this.assignForm.invalid) {
      return;
    }
    const { staffId, shopId } = this.assignForm.value;

    this.ownerService.assignStaffToShop(staffId!, shopId!).subscribe({
      next: (response) => {
        this.successMessage = response.message;
        this.errorMessage = null;
        this.assignForm.reset();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to assign staff.';
        this.successMessage = null;
      }
    });
  }
}