# Ledgerly API Postman Collection Usage Guide

## Overview
This Postman collection contains all the APIs for the Ledgerly Small Business Ledger Management System. The collection is organized by functional areas and includes sample request bodies for testing.

## Setup Instructions

### 1. Import the Collection
1. Open Postman
2. Click "Import" button
3. Select the `Ledgerly-API-Postman-Collection.json` file
4. The collection will be imported with all endpoints organized by category

### 2. Configure Environment Variables
The collection uses two environment variables:
- `baseUrl`: Set to `http://localhost:8080/api/v1` (default)
- `authToken`: Will store your JWT token after login

### 3. Authentication Flow
1. **Start with Health Check**: Test if the backend is running
   - Use "Basic Health Check" endpoint
   - Should return status "UP"

2. **Register a Staff User**: 
   - Use "User Registration" endpoint
   - Set role to "STAFF" (only STAFF can self-register)
   - Note: ADMIN creates OWNER, OWNER creates STAFF

3. **Login to Get Token**:
   - Use "User Login" endpoint
   - Copy the `token` from the response
   - Set the `authToken` environment variable

4. **Test Protected Endpoints**:
   - All subsequent requests will automatically include the token

## API Categories

### 1. Health Check
- **Basic Health Check**: `GET /health` - Basic application status
- **Detailed Health Check**: `GET /health/detailed` - System information

### 2. Authentication
- **User Registration**: `POST /auth/register` - Create new STAFF user
- **User Login**: `POST /auth/login` - Authenticate and get JWT token
- **Get Current User**: `GET /auth/me` - Get logged-in user info
- **Get All Users**: `GET /auth/users` - List all users (ADMIN/OWNER only)
- **User Management**: Various endpoints for user CRUD operations

### 3. Profile Management
- **Get Profile**: `GET /profile` - Get current user profile
- **Update Profile**: `PUT /profile` - Update current user profile
- **Contact Info**: `PUT /profile/contact` - Update contact information

### 4. Admin Management
- **Create Owner**: `POST /admin/users/owners` - ADMIN creates OWNER users
- **Owner Management**: CRUD operations for OWNER accounts

### 5. Shop Management
- **Create Shop**: `POST /shops` - OWNER creates business locations
- **Shop Operations**: CRUD operations for shops
- **Location Queries**: Find shops by city, state, etc.

### 6. Owner Management
- **Create Staff**: `POST /owner/users` - OWNER creates STAFF users
- **Staff Management**: CRUD operations for STAFF accounts
- **Shop Assignments**: Assign/remove staff from specific shops

### 7. Customer Management
- **Create Customer**: `POST /customers` - STAFF/OWNER creates customers
- **Customer Operations**: CRUD operations for customers
- **Search & Filter**: Find customers by various criteria
- **Statistics**: Get customer-related metrics

### 8. Ledger Management
- **Create Entry**: `POST /ledger` - Create credit/debit transactions
- **Ledger Operations**: CRUD operations for ledger entries
- **Balance Queries**: Get customer balances and history
- **Search & Filter**: Find entries by various criteria

### 9. Payment Management
- **Create Payment**: `POST /payments` - Record customer payments
- **Payment Operations**: CRUD operations for payments
- **Status Management**: Mark payments as paid, overdue, disputed
- **Payment Queries**: Find payments by various criteria

### 10. File Management
- **Download Files**: `GET /files/download/{id}` - Download attachments
- **Storage Stats**: `GET /files/stats` - Get file storage information

### 11. Business Rules
- **Configuration**: `GET /business-rules/configuration` - Get rule settings
- **Descriptions**: `GET /business-rules/descriptions` - Get rule explanations

### 12. Audit Logs
- **View Logs**: Various endpoints to view system audit trails
- **Filtering**: Filter logs by user, action, date, entity

## Testing Workflow

### Step 1: Backend Setup
1. Ensure MySQL is running with database `ledgerly`
2. Start Spring Boot application
3. Verify health check endpoint returns "UP"

### Step 2: User Creation Hierarchy
1. **ADMIN creates OWNER**:
   - Use Admin Management → Create Owner
   - Set role to "OWNER"
   - Note: You'll need an ADMIN user first (create manually in database)

2. **OWNER creates SHOP**:
   - Login as OWNER
   - Use Shop Management → Create Shop
   - Provide business details

3. **OWNER creates STAFF**:
   - Use Owner Management → Create Staff
   - Set role to "STAFF"

4. **STAFF creates CUSTOMERS**:
   - Login as STAFF
   - Use Customer Management → Create Customer

### Step 3: Business Operations
1. **Create Ledger Entries**:
   - Use Ledger Management → Create Ledger Entry
   - Link to customer and shop

2. **Record Payments**:
   - Use Payment Management → Create Payment
   - Link to customer and shop

3. **Monitor Operations**:
   - Use various GET endpoints to view data
   - Check audit logs for activity tracking

## Common Test Scenarios

### Scenario 1: Complete Customer Transaction
1. Create a customer
2. Create a ledger entry (credit - invoice)
3. Create a payment
4. Check customer balance
5. View audit logs

### Scenario 2: Multi-Shop Operations
1. Create multiple shops
2. Assign staff to different shops
3. Create customers in different shops
4. Verify data isolation between shops

### Scenario 3: Business Rule Testing
1. Try to create negative balance (should fail)
2. Test transaction amount limits
3. Verify role-based access control

## Troubleshooting

### Common Issues
1. **401 Unauthorized**: Check if JWT token is set and valid
2. **403 Forbidden**: Verify user has required role for the endpoint
3. **400 Bad Request**: Check request body format and validation
4. **500 Internal Server Error**: Check backend logs for details

### Database Issues
1. Ensure MySQL is running on port 3306
2. Verify database `ledgerly` exists
3. Check connection credentials in `application.properties`

### Role-Based Access Issues
- **ADMIN**: Can access all endpoints
- **OWNER**: Can manage their shops, staff, and related data
- **STAFF**: Can manage customers and ledger entries in assigned shop

## Sample Data for Testing

### Test Users
```json
{
  "username": "teststaff",
  "email": "staff@test.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "Staff",
  "role": "STAFF"
}
```

### Test Shop
```json
{
  "name": "Test Store",
  "description": "Test business location",
  "address": "123 Test Street",
  "phoneNumber": "+1234567890",
  "email": "test@store.com",
  "city": "Test City",
  "state": "TS",
  "pincode": "12345"
}
```

### Test Customer
```json
{
  "name": "Test Customer",
  "email": "customer@test.com",
  "phoneNumber": "+0987654321",
  "address": "456 Customer Ave",
  "city": "Test City",
  "state": "TS",
  "pincode": "12345"
}
```

## Notes
- All dates should be in ISO format: `YYYY-MM-DD`
- Amounts should be decimal numbers (e.g., `1500.00`)
- IDs in URLs should be replaced with actual entity IDs
- The collection uses Bearer token authentication automatically
- Some endpoints may require specific user roles to access
