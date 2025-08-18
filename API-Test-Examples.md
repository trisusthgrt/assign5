# Ledgerly API Test Examples

## Role Assignment & Access Control Endpoints

### Prerequisites
1. Start the application: `mvn spring-boot:run`
2. Ensure MySQL is running with database `ledgerly`
3. Application runs on `http://localhost:8080`

## 1. User Registration & Login

### Register Owner User
```bash
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "business_owner",
  "email": "owner@business.com",
  "password": "password123",
  "firstName": "Business",
  "lastName": "Owner",
  "role": "OWNER",
  "businessName": "My Business Ltd",
  "businessAddress": "123 Business Street, City"
}
```

### Register Staff User
```bash
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "staff_member",
  "email": "staff@business.com",
  "password": "password123",
  "firstName": "Staff",
  "lastName": "Member",
  "role": "STAFF"
}
```

### Login
```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "business_owner",
  "password": "password123"
}
```

**Response includes JWT token - save this for authenticated requests**

## 2. Role Assignment Endpoints

### Assign Role to User (Admin/Owner only)
```bash
PUT http://localhost:8080/api/v1/auth/users/assign-role
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "userId": 2,
  "role": "STAFF"
}
```

### Get User by ID (Admin/Owner only)
```bash
GET http://localhost:8080/api/v1/auth/users/2
Authorization: Bearer YOUR_JWT_TOKEN
```

### Get All Users (Admin/Owner only)
```bash
GET http://localhost:8080/api/v1/auth/users
Authorization: Bearer YOUR_JWT_TOKEN
```

### Get Users by Role (Admin only)
```bash
GET http://localhost:8080/api/v1/auth/users/role/STAFF
Authorization: Bearer YOUR_JWT_TOKEN
```

## 3. Profile Management Endpoints

### Get Current User Profile
```bash
GET http://localhost:8080/api/v1/profile
Authorization: Bearer YOUR_JWT_TOKEN
```

### Update Current User Profile
```bash
PUT http://localhost:8080/api/v1/profile
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "firstName": "Updated",
  "lastName": "Name",
  "email": "updated@business.com",
  "phoneNumber": "+1234567890",
  "businessName": "Updated Business Name",
  "businessAddress": "456 New Address, City"
}
```

### Update Profile by User ID (Admin/Owner only)
```bash
PUT http://localhost:8080/api/v1/profile/2
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "firstName": "Updated",
  "lastName": "Staff",
  "email": "updated_staff@business.com",
  "phoneNumber": "+1234567891"
}
```

### Get Profile by User ID (Admin/Owner only)
```bash
GET http://localhost:8080/api/v1/profile/2
Authorization: Bearer YOUR_JWT_TOKEN
```

### Update Business Details Only (Owner/Admin only)
```bash
PUT http://localhost:8080/api/v1/profile/business
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "businessName": "New Business Name",
  "businessAddress": "789 Business Avenue, City"
}
```

### Update Contact Info Only
```bash
PUT http://localhost:8080/api/v1/profile/contact
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "email": "new_email@business.com",
  "phoneNumber": "+9876543210"
}
```

## 4. Access Control Examples

### Current User Info
```bash
GET http://localhost:8080/api/v1/auth/me
Authorization: Bearer YOUR_JWT_TOKEN
```

### User Statistics (Admin only)
```bash
GET http://localhost:8080/api/v1/auth/stats
Authorization: Bearer YOUR_JWT_TOKEN
```

### Activate/Deactivate User (Admin/Owner only)
```bash
PUT http://localhost:8080/api/v1/auth/users/2/activate
Authorization: Bearer YOUR_JWT_TOKEN

PUT http://localhost:8080/api/v1/auth/users/2/deactivate
Authorization: Bearer YOUR_JWT_TOKEN
```

## 5. Using with Postman/Curl

### Example with curl:
```bash
# 1. Register user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_owner",
    "email": "test@test.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "Owner",
    "role": "OWNER",
    "businessName": "Test Business"
  }'

# 2. Login and get token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "test_owner",
    "password": "password123"
  }'

# 3. Use token for authenticated requests
curl -X GET http://localhost:8080/api/v1/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Response Examples

### Successful Role Assignment:
```json
{
  "message": "Role assigned successfully",
  "userId": 2,
  "username": "staff_member",
  "newRole": "STAFF",
  "timestamp": 1629123456789
}
```

### Profile Update Response:
```json
{
  "message": "Profile updated successfully",
  "profile": {
    "id": 1,
    "username": "business_owner",
    "email": "updated@business.com",
    "firstName": "Updated",
    "lastName": "Name",
    "fullName": "Updated Name",
    "phoneNumber": "+1234567890",
    "role": "OWNER",
    "businessName": "Updated Business Name",
    "businessAddress": "456 New Address, City",
    "isActive": true,
    "isEmailVerified": true,
    "isPhoneVerified": false
  },
  "timestamp": 1629123456789
}
```

## Access Control Summary

| Endpoint | OWNER | STAFF | ADMIN |
|----------|-------|-------|-------|
| GET /api/v1/profile | ✅ | ✅ | ✅ |
| PUT /api/v1/profile | ✅ | ✅ | ✅ |
| PUT /api/v1/profile/contact | ✅ | ✅ | ✅ |
| PUT /api/v1/profile/business | ✅ | ❌ | ✅ |
| GET /api/v1/profile/{id} | ✅ | ❌ | ✅ |
| PUT /api/v1/profile/{id} | ✅ | ❌ | ✅ |
| PUT /api/v1/auth/users/assign-role | ✅ | ❌ | ✅ |
| GET /api/v1/auth/users | ✅ | ❌ | ✅ |
| GET /api/v1/auth/stats | ❌ | ❌ | ✅ |
