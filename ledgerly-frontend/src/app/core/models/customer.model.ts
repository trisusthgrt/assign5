export interface Customer {
  id: number;
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
  businessName: string;
  shopId: number;
  shopName: string;
  // Add other fields from your CustomerResponse DTO as needed
  relationshipType: string;
  notes: string;
  creditLimit: number;
  currentBalance: number;
  isActive: boolean;
}