import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-business-details',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="business-details">
      <h2>Business Details</h2>
      <form [formGroup]="form" (ngSubmit)="save()" class="form">
        <label>Business Name</label>
        <input formControlName="businessName" />
        <label>GST Number</label>
        <input formControlName="gstNumber" />
        <label>Address</label>
        <input formControlName="address" />
        <button type="submit" [disabled]="form.invalid">Save</button>
      </form>
    </div>`
})
export class BusinessDetailsComponent {
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);

  form = this.fb.group({
    businessName: ['', Validators.required],
    gstNumber: [''],
    address: ['']
  });

  constructor() {
    this.http.get<any>('http://localhost:8080/api/v1/owner/business').subscribe(res => {
      if (res?.success && res.business) this.form.patchValue(res.business);
    });
  }

  save() {
    if (this.form.invalid) return;
    this.http.put<any>('http://localhost:8080/api/v1/owner/business', this.form.value).subscribe();
  }
}


