# 🔐 **Admin Management API Documentation - Ledgerly**

## 🚀 **Overview**

This document describes the comprehensive admin management functionality that allows **ADMIN** users to create, update, delete, and manage other **ADMIN**, **OWNER**, and **STAFF** users in the Ledgerly system.

## 🔑 **Access Control**

- **Required Role**: `ADMIN` only
- **Authentication**: JWT Bearer token required
- **Base Path**: `/api/v1/admin/users`

## 📋 **API Endpoints Overview**

### **👥 Admin User Management**
- `POST /admins` - Create a new admin user
- `GET /admins` - List all admin users
- `GET /admins/{id}` - Get admin user by ID
- `PUT /admins/{id}` - Update admin user
- `DELETE /admins/{id}` - Delete (deactivate) admin user

### **🏢 Owner User Management**
- `POST /owners` - Create a new owner user
- `GET /owners` - List all owner users
- `GET /owners/{id}` - Get owner user by ID
- `PUT /owners/{id}` - Update owner user
- `DELETE /owners/{id}` - Delete (deactivate) owner user

### **👨‍💼 Staff User Management**
- `GET /staff` - List all staff users
- `GET /staff/{id}` - Get staff user by ID

## 🛠️ **Detailed API Endpoints**

### **1. Create Admin User**

**Endpoint**: `POST /api/v1/admin/users/admins`

**Description**: Creates a new admin user. Only existing admins can create other admin users.

**Request Body**:
```json
{
  "username": "newadmin",
  "email": "newadmin@ledgerly.com",
  "password": "securepassword123",
  "firstName": "New",
  "lastName": "Admin",
  "role": "ADMIN"
}
```

**Response**:
- **201 Created**: Admin user created successfully
- **400 Bad Request**: Invalid request data
- **409 Conflict**: Username or email already exists
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (201)**:
```json
{
  "id": 5,
  "username": "newadmin",
  "email": "newadmin@ledgerly.com",
  "firstName": "New",
  "lastName": "Admin",
  "role": "ADMIN",
  "active": true
}
```

### **2. List All Admin Users**

**Endpoint**: `GET /api/v1/admin/users/admins`

**Description**: Retrieves a list of all admin users. Only admins can view this list.

**Response**:
- **200 OK**: Admin users retrieved successfully
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (200)**:
```json
[
  {
    "id": 1,
    "username": "superadmin",
    "email": "superadmin@ledgerly.com",
    "firstName": "Super",
    "lastName": "Admin",
    "role": "ADMIN",
    "active": true
  },
  {
    "id": 5,
    "username": "newadmin",
    "email": "newadmin@ledgerly.com",
    "firstName": "New",
    "lastName": "Admin",
    "role": "ADMIN",
    "active": true
  }
]
```

### **3. Get Admin User by ID**

**Endpoint**: `GET /api/v1/admin/users/admins/{id}`

**Description**: Retrieves a specific admin user by ID. Only admins can view admin details.

**Path Parameters**:
- `id` (Long): ID of the admin user to retrieve

**Response**:
- **200 OK**: Admin user retrieved successfully
- **404 Not Found**: Admin user not found
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (200)**:
```json
{
  "id": 5,
  "username": "newadmin",
  "email": "newadmin@ledgerly.com",
  "firstName": "New",
  "lastName": "Admin",
  "role": "ADMIN",
  "active": true
}
```

### **4. Update Admin User**

**Endpoint**: `PUT /api/v1/admin/users/admins/{id}`

**Description**: Updates an existing admin user. Only admins can update other admin users.

**Path Parameters**:
- `id` (Long): ID of the admin user to update

**Request Body**:
```json
{
  "firstName": "Updated",
  "lastName": "Admin",
  "email": "updatedadmin@ledgerly.com",
  "phoneNumber": "+1234567890"
}
```

**Response**:
- **200 OK**: Admin user updated successfully
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Admin user not found
- **409 Conflict**: Email already exists
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (200)**:
```json
{
  "id": 5,
  "username": "newadmin",
  "email": "updatedadmin@ledgerly.com",
  "firstName": "Updated",
  "lastName": "Admin",
  "role": "ADMIN",
  "active": true
}
```

### **5. Delete Admin User**

**Endpoint**: `DELETE /api/v1/admin/users/admins/{id}`

**Description**: Deletes (deactivates) an admin user. Only admins can delete other admin users. Uses soft delete (sets user as inactive).

**Path Parameters**:
- `id` (Long): ID of the admin user to delete

**Response**:
- **200 OK**: Admin user deleted successfully
- **404 Not Found**: Admin user not found
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (200)**:
```json
{
  "message": "Admin user deactivated successfully"
}
```

### **6. Create Owner User**

**Endpoint**: `POST /api/v1/admin/users/owners`

**Description**: Creates a new owner user. Only admins can create owner accounts.

**Request Body**:
```json
{
  "username": "newowner",
  "email": "newowner@ledgerly.com",
  "password": "securepassword123",
  "firstName": "New",
  "lastName": "Owner",
  "role": "OWNER"
}
```

**Response**:
- **201 Created**: Owner user created successfully
- **400 Bad Request**: Invalid request data
- **409 Conflict**: Username or email already exists
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (201)**:
```json
{
  "id": 6,
  "username": "newowner",
  "email": "newowner@ledgerly.com",
  "firstName": "New",
  "lastName": "Owner",
  "role": "OWNER",
  "active": true
}
```

### **7. List All Owner Users**

**Endpoint**: `GET /api/v1/admin/users/owners`

**Description**: Retrieves a list of all owner users. Only admins can view this list.

**Response**:
- **200 OK**: Owner users retrieved successfully
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (200)**:
```json
[
  {
    "id": 2,
    "username": "businessowner1",
    "email": "owner1@business.com",
    "firstName": "Business",
    "lastName": "Owner",
    "role": "OWNER",
    "active": true
  },
  {
    "id": 6,
    "username": "newowner",
    "email": "newowner@ledgerly.com",
    "firstName": "New",
    "lastName": "Owner",
    "role": "OWNER",
    "active": true
  }
]
```

### **8. Get Owner User by ID**

**Endpoint**: `GET /api/v1/admin/users/owners/{id}`

**Description**: Retrieves a specific owner user by ID. Only admins can view owner details.

**Path Parameters**:
- `id` (Long): ID of the owner user to retrieve

**Response**:
- **200 OK**: Owner user retrieved successfully
- **404 Not Found**: Owner user not found
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

### **9. Update Owner User**

**Endpoint**: `PUT /api/v1/admin/users/owners/{id}`

**Description**: Updates an existing owner user. Only admins can update owner accounts.

**Path Parameters**:
- `id` (Long): ID of the owner user to update

**Request Body**:
```json
{
  "firstName": "Updated",
  "lastName": "Owner",
  "email": "updatedowner@ledgerly.com",
  "phoneNumber": "+1234567890"
}
```

**Response**:
- **200 OK**: Owner user updated successfully
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Owner user not found
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

### **10. Delete Owner User**

**Endpoint**: `DELETE /api/v1/admin/users/owners/{id}`

**Description**: Deletes (deactivates) an owner user. Only admins can delete owner accounts. Uses soft delete.

**Path Parameters**:
- `id` (Long): ID of the owner user to delete

**Response**:
- **200 OK**: Owner user deleted successfully
- **404 Not Found**: Owner user not found
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (200)**:
```json
{
  "message": "Owner user deactivated successfully"
}
```

### **11. List All Staff Users**

**Endpoint**: `GET /api/v1/admin/users/staff`

**Description**: Retrieves a list of all staff users. Only admins can view this list.

**Response**:
- **200 OK**: Staff users retrieved successfully
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

**Example Response (200)**:
```json
[
  {
    "id": 3,
    "username": "staffuser1",
    "email": "staff1@business.com",
    "firstName": "Staff",
    "lastName": "User",
    "role": "STAFF",
    "active": true
  },
  {
    "id": 4,
    "username": "staffuser2",
    "email": "staff2@business.com",
    "firstName": "Another",
    "lastName": "Staff",
    "role": "STAFF",
    "active": true
  }
]
```

### **12. Get Staff User by ID**

**Endpoint**: `GET /api/v1/admin/users/staff/{id}`

**Description**: Retrieves a specific staff user by ID. Only admins can view staff details.

**Path Parameters**:
- `id` (Long): ID of the staff user to retrieve

**Response**:
- **200 OK**: Staff user retrieved successfully
- **404 Not Found**: Staff user not found
- **401 Unauthorized**: Missing or invalid token
- **403 Forbidden**: Not an admin user

## 🔒 **Security Features**

### **Role-Based Access Control**
- All endpoints require `ADMIN` role
- JWT Bearer token authentication
- Method-level security with `@PreAuthorize("hasRole('ADMIN')")`

### **Data Validation**
- Input validation using Jakarta Validation
- Username and email uniqueness checks
- Role validation for user creation

### **Soft Delete**
- Users are deactivated rather than permanently deleted
- Maintains data integrity and audit trail
- Allows for potential reactivation

## 📊 **Data Models**

### **UserCreateRequest**
```json
{
  "username": "string (3-50 chars, unique)",
  "email": "string (valid email, unique)",
  "password": "string (6-100 chars)",
  "firstName": "string (required)",
  "lastName": "string (required)",
  "role": "ADMIN | OWNER | STAFF"
}
```

### **UserUpdateRequest**
```json
{
  "firstName": "string (required)",
  "lastName": "string (required)",
  "email": "string (valid email, optional)",
  "phoneNumber": "string (optional)"
}
```

### **BasicUserResponse**
```json
{
  "id": "number",
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "role": "ADMIN | OWNER | STAFF",
  "active": "boolean"
}
```

## 🚨 **Important Notes**

### **Admin Self-Management**
- Admins can manage other admin users
- Cannot delete themselves (prevents system lockout)
- Soft delete maintains system integrity

### **User Hierarchy**
- **ADMIN**: Can manage all users (ADMIN, OWNER, STAFF)
- **OWNER**: Can manage STAFF users and create shops
- **STAFF**: Can manage customers and ledger entries

### **Email Verification**
- All admin-created users are automatically email verified
- This is for demo/testing purposes
- In production, implement proper email verification flow

## 🔍 **Testing Examples**

### **1. Create Admin User**
```bash
curl -X POST "http://localhost:8080/api/v1/admin/users/admins" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newadmin",
    "email": "newadmin@ledgerly.com",
    "password": "securepassword123",
    "firstName": "New",
    "lastName": "Admin",
    "role": "ADMIN"
  }'
```

### **2. List All Admins**
```bash
curl -X GET "http://localhost:8080/api/v1/admin/users/admins" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### **3. Update Admin User**
```bash
curl -X PUT "http://localhost:8080/api/v1/admin/users/admins/5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Updated",
    "lastName": "Admin",
    "email": "updatedadmin@ledgerly.com"
  }'
```

### **4. Delete Admin User**
```bash
curl -X DELETE "http://localhost:8080/api/v1/admin/users/admins/5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🎯 **Next Steps**

### **Immediate Enhancements**
1. **Self-Protection**: Prevent admins from deleting themselves
2. **Audit Logging**: Log all admin management operations
3. **Bulk Operations**: Support for bulk user management

### **Future Features**
1. **User Reactivation**: Ability to reactivate deactivated users
2. **Password Reset**: Admin-initiated password resets
3. **User Import/Export**: Bulk user management via CSV/Excel
4. **Advanced Filtering**: Search and filter users by various criteria

---

**Admin Management API is now fully implemented and ready for use! 🚀**

This comprehensive system provides secure, role-based user management with full CRUD operations for all user types.

