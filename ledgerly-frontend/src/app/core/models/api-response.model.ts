export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  // Use generic property names that can be reused
  data?: T;
  customer?: T; // For specific customer responses
  customers?: T; // For customer list responses
}