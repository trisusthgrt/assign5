# 📝 **Complete Ledgerly API Examples**

## Overview
This document contains all the API endpoints for the Ledgerly Small Business Ledger Management System, organized by functional areas with complete request/response examples.

---

## 1. **Health Check**

### **GET** `/api/v1/health`

**Headers:** None required

**Expected Response (200 OK):**
```json
{
    "status": "UP",
    "message": "Ledgerly application is running successfully",
    "timestamp": "2024-01-15T10:30:00",
    "application": "Ledgerly",
    "version": "0.0.1-SNAPSHOT"
}
```

---

### **GET** `/api/v1/health/detailed`

**Headers:** None required

**Expected Response (200 OK):**
```json
{
    "status": "UP",
    "application": "Ledgerly - Small Business Ledger & Finance Manager",
    "version": "0.0.1-SNAPSHOT",
    "timestamp": "2024-01-15T10:30:00",
    "system": {
        "java_version": "17.0.2",
        "os_name": "Windows 10",
        "available_processors": 8,
        "max_memory_mb": 2048
    }
}
```

---

## 2. 📝 **User Registration**

### **POST** `/api/v1/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "staffuser",
    "email": "staff@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STAFF"
}
```

**Expected Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "staffuser",
    "email": "staff@example.com",
    "role": "STAFF",
    "message": "User registered successfully"
}
```

---

## 3. 📝 **User Login**

### **POST** `/api/v1/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "staffuser",
    "password": "password123"
}
```

**Expected Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "staffuser",
    "email": "staff@example.com",
    "role": "STAFF",
    "message": "Login successful"
}
```

---

## 4. **Get Current User**

### **GET** `/api/v1/auth/me`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "username": "staffuser",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STAFF",
    "fullName": "John Doe",
    "emailVerified": true,
    "phoneVerified": false,
    "createdAt": "2024-01-15T10:30:00",
    "lastLogin": "2024-01-15T10:30:00"
}
```

---

## 5. 📝 **Create Shop**

### **POST** `/api/v1/shops`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}}
```

**Body (JSON):**
```json
{
    "name": "Main Store",
    "description": "Primary business location",
    "address": "123 Main Street",
    "phoneNumber": "+1234567890",
    "email": "main@store.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10001"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 1,
    "name": "Main Store",
    "description": "Primary business location",
    "address": "123 Main Street",
    "phoneNumber": "+1234567890",
    "email": "main@store.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10001",
    "isActive": true,
    "ownerId": 1,
    "ownerName": "John Doe"
}
```

---

## 6. 📝 **Create Customer**

### **POST** `/api/v1/customers`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}}
```

**Body (JSON):**
```json
{
    "name": "John Customer",
    "email": "john.customer@example.com",
    "phoneNumber": "+1234567890",
    "address": "456 Customer Street",
    "city": "New York",
    "state": "NY",
    "pincode": "10002",
    "gstNumber": "GST987654321",
    "panNumber": "CUST1234A",
    "creditLimit": 10000.00,
    "relationshipType": "REGULAR",
    "notes": "Regular customer with good payment history"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 1,
    "name": "John Customer",
    "email": "john.customer@example.com",
    "phoneNumber": "+1234567890",
    "address": "456 Customer Street",
    "city": "New York",
    "state": "NY",
    "pincode": "10002",
    "gstNumber": "GST987654321",
    "panNumber": "CUST1234A",
    "creditLimit": 10000.00,
    "relationshipType": "REGULAR",
    "notes": "Regular customer with good payment history",
    "isActive": true,
    "shopId": 1,
    "shopName": "Main Store",
    "createdAt": "2024-01-15T10:30:00"
}
```

---

## 7. 📝 **Create Ledger Entry**

### **POST** `/api/v1/ledger`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}}
```

**Body (JSON):**
```json
{
    "customerId": 1,
    "transactionDate": "2024-01-15",
    "transactionType": "CREDIT",
    "amount": 1500.00,
    "description": "Invoice #INV001 for services rendered",
    "notes": "Monthly service fee",
    "referenceNumber": "INV001",
    "invoiceNumber": "INV001",
    "paymentMethod": "CASH"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 1,
    "customerId": 1,
    "customerName": "John Customer",
    "transactionDate": "2024-01-15",
    "transactionType": "CREDIT",
    "amount": 1500.00,
    "description": "Invoice #INV001 for services rendered",
    "notes": "Monthly service fee",
    "referenceNumber": "INV001",
    "invoiceNumber": "INV001",
    "paymentMethod": "CASH",
    "isReconciled": false,
    "shopId": 1,
    "shopName": "Main Store",
    "createdAt": "2024-01-15T10:30:00"
}
```

---

## 8. 📝 **Create Payment**

### **POST** `/api/v1/payments`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}}
```

**Body (JSON):**
```json
{
    "customerId": 1,
    "paymentDate": "2024-01-20",
    "amount": 1500.00,
    "description": "Payment for Invoice #INV001",
    "notes": "Customer payment received",
    "referenceNumber": "PAY001",
    "paymentMethod": "BANK_TRANSFER",
    "dueDate": "2024-02-15",
    "isAdvancePayment": false
}
```

**Expected Response (201 Created):**
```json
{
    "id": 1,
    "customerId": 1,
    "customerName": "John Customer",
    "paymentDate": "2024-01-20",
    "amount": 1500.00,
    "appliedAmount": 0.00,
    "remainingAmount": 1500.00,
    "description": "Payment for Invoice #INV001",
    "notes": "Customer payment received",
    "referenceNumber": "PAY001",
    "paymentMethod": "BANK_TRANSFER",
    "status": "PENDING",
    "dueDate": "2024-02-15",
    "isAdvancePayment": false,
    "shopId": 1,
    "shopName": "Main Store",
    "createdAt": "2024-01-15T10:30:00"
}
```

---

## 9. 📝 **Get Customer Balance**

### **GET** `/api/v1/ledger/customer/1/balance`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Expected Response (200 OK):**
```json
{
    "customerId": 1,
    "customerName": "John Customer",
    "totalCredit": 1500.00,
    "totalDebit": 0.00,
    "currentBalance": 1500.00,
    "lastTransactionDate": "2024-01-15",
    "unreconciledAmount": 1500.00
}
```

---

## 10. **Search Customers**

### **GET** `/api/v1/customers/search?query=John&city=New York&relationshipType=REGULAR`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Expected Response (200 OK):**
```json
{
    "customers": [
        {
            "id": 1,
            "name": "John Customer",
            "email": "john.customer@example.com",
            "phoneNumber": "+1234567890",
            "city": "New York",
            "relationshipType": "REGULAR",
            "isActive": true
        }
    ],
    "totalCount": 1,
    "pageNumber": 0,
    "pageSize": 20
}
```

---

## 11. 📝 **Get Business Rules**

### **GET** `/api/v1/business-rules/configuration`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "configuration": {
        "allowNegativeBalance": false,
        "maxTransactionAmount": 100000.00,
        "minTransactionAmount": 0.01,
        "maxDailyTransactionLimit": 50000.00,
        "requireFutureDateValidation": true
    }
}
```

---

## 12. 📝 **Get Audit Logs**

### **GET** `/api/v1/audit-logs`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Expected Response (200 OK):**
```json
{
    "auditLogs": [
        {
            "id": 1,
            "userId": 1,
            "username": "staffuser",
            "action": "CREATE",
            "entityType": "Customer",
            "entityId": 1,
            "description": "Customer created: John Customer",
            "timestamp": "2024-01-15T10:30:00",
            "success": true
        }
    ],
    "totalCount": 1,
    "pageNumber": 0,
    "pageSize": 20
}
```

---

## 13. 📝 **Update Customer**

### **PUT** `/api/v1/customers/1`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}}
```

**Body (JSON):**
```json
{
    "name": "John Customer Updated",
    "email": "john.updated@example.com",
    "phoneNumber": "+1234567890",
    "address": "456 Customer Street, Apt 5",
    "city": "New York",
    "state": "NY",
    "pincode": "10002",
    "gstNumber": "GST987654321",
    "panNumber": "CUST1234A",
    "creditLimit": 15000.00,
    "relationshipType": "PREMIUM",
    "notes": "Updated customer information"
}
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "name": "John Customer Updated",
    "email": "john.updated@example.com",
    "phoneNumber": "+1234567890",
    "address": "456 Customer Street, Apt 5",
    "city": "New York",
    "state": "NY",
    "pincode": "10002",
    "gstNumber": "GST987654321",
    "panNumber": "CUST1234A",
    "creditLimit": 15000.00,
    "relationshipType": "PREMIUM",
    "notes": "Updated customer information",
    "isActive": true,
    "shopId": 1,
    "shopName": "Main Store",
    "updatedAt": "2024-01-15T10:30:00"
}
```

---

## 14. 📝 **Mark Payment as Paid**

### **PUT** `/api/v1/payments/1/mark-paid`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}}
```

**Body (JSON):**
```json
{
    "notes": "Payment confirmed and processed"
}
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "status": "PAID",
    "processedDate": "2024-01-20",
    "statusNotes": "Payment confirmed and processed",
    "message": "Payment marked as paid successfully"
}
```

---

## 15. **Get File Storage Stats**

### **GET** `/api/v1/files/stats`

**Headers:**
```
Authorization: Bearer {{authToken}}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "stats": {
        "totalFiles": 5,
        "totalSizeBytes": 1048576,
        "formattedTotalSize": "1.0 MB",
        "imageFiles": 3,
        "documentFiles": 2
    }
}
```

---

## **ADMIN Management APIs**

### 1. **Create Owner (ADMIN only)**

#### **POST** `/api/v1/admin/users/owners`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (ADMIN role required)
```

**Body (JSON):**
```json
{
    "username": "owneruser",
    "email": "owner@example.com",
    "password": "password123",
    "firstName": "Owner",
    "lastName": "User",
    "role": "OWNER"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 2,
    "username": "owneruser",
    "email": "owner@example.com",
    "firstName": "Owner",
    "lastName": "User",
    "role": "OWNER",
    "active": true,
    "message": "Owner user created successfully"
}
```

---

### 2. **Update Owner (ADMIN only)**

#### **PUT** `/api/v1/admin/users/owners/2`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (ADMIN role required)
```

**Body (JSON):**
```json
{
    "firstName": "Owner Updated",
    "lastName": "User Updated",
    "email": "owner.updated@example.com",
    "phoneNumber": "+1234567890"
}
```

**Expected Response (200 OK):**
```json
{
    "id": 2,
    "username": "owneruser",
    "firstName": "Owner Updated",
    "lastName": "User Updated",
    "email": "owner.updated@example.com",
    "phoneNumber": "+1234567890",
    "role": "OWNER",
    "active": true,
    "message": "Owner updated successfully"
}
```

---

### 3. **Delete Owner (ADMIN only)**

#### **DELETE** `/api/v1/admin/users/owners/2`

**Headers:**
```
Authorization: Bearer {{authToken}} (ADMIN role required)
```

**Expected Response (204 No Content):**
```
Owner deleted successfully
```

---

### 4. 📝 **List All Owners (ADMIN only)**

#### **GET** `/api/v1/admin/users/owners`

**Headers:**
```
Authorization: Bearer {{authToken}} (ADMIN role required)
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 2,
        "username": "owneruser",
        "email": "owner@example.com",
        "firstName": "Owner",
        "lastName": "User",
        "role": "OWNER",
        "active": true
    }
]
```

---

## **OWNER Management APIs**

### 5. 📝 **Create Staff (OWNER only)**

#### **POST** `/api/v1/owner/users`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Body (JSON):**
```json
{
    "username": "newstaff",
    "email": "newstaff@example.com",
    "password": "password123",
    "firstName": "New",
    "lastName": "Staff",
    "role": "STAFF"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 3,
    "username": "newstaff",
    "email": "newstaff@example.com",
    "firstName": "New",
    "lastName": "Staff",
    "role": "STAFF",
    "active": true,
    "message": "Staff user created successfully"
}
```

---

### 6. 📝 **Get All Staff (OWNER only)**

#### **GET** `/api/v1/owner/users`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 3,
        "username": "newstaff",
        "email": "newstaff@example.com",
        "firstName": "New",
        "lastName": "Staff",
        "role": "STAFF",
        "active": true
    }
]
```

---

### 7. 📝 **Update Staff (OWNER only)**

#### **PUT** `/api/v1/owner/users/3`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Body (JSON):**
```json
{
    "firstName": "Updated Staff",
    "lastName": "Member",
    "email": "updated.staff@example.com",
    "phoneNumber": "+1234567890"
}
```

**Expected Response (200 OK):**
```json
{
    "id": 3,
    "username": "newstaff",
    "firstName": "Updated Staff",
    "lastName": "Member",
    "email": "updated.staff@example.com",
    "phoneNumber": "+1234567890",
    "role": "STAFF",
    "active": true,
    "message": "Staff updated successfully"
}
```

---

### 8. **Deactivate Staff (OWNER only)**

#### **PUT** `/api/v1/owner/users/3/deactivate`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
{
    "id": 3,
    "active": false,
    "message": "Staff deactivated successfully"
}
```

---

### 9. 📝 **Assign Staff to Shop (OWNER only)**

#### **POST** `/api/v1/owner/staff-shop-assignments`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Body (JSON):**
```json
{
    "staffId": 3,
    "shopId": 1
}
```

**Expected Response (201 Created):**
```json
{
    "id": 1,
    "staffId": 3,
    "staffName": "New Staff",
    "shopId": 1,
    "shopName": "Main Store",
    "assignedAt": "2024-01-15T10:30:00",
    "isActive": true,
    "message": "Staff assigned to shop successfully"
}
```

---

### 10. 📝 **Remove Staff from Shop (OWNER only)**

#### **DELETE** `/api/v1/owner/staff-shop-assignments/1`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (204 No Content):**
```
Staff removed from shop successfully
```

---

### 11. 📝 **Get Staff Shop Assignments (OWNER only)**

#### **GET** `/api/v1/owner/staff-shop-assignments`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "staffId": 3,
        "staffName": "New Staff",
        "shopId": 1,
        "shopName": "Main Store",
        "assignedAt": "2024-01-15T10:30:00",
        "isActive": true
    }
]
```

---

## **OWNER Creates SHOP APIs**

### 1. **Create Shop (OWNER only)**

#### **POST** `/api/v1/shops`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Body (JSON):**
```json
{
    "name": "Main Hardware Store",
    "description": "Primary hardware and tools business location",
    "address": "123 Main Street, Downtown",
    "phoneNumber": "+1-234-567-8900",
    "email": "main@hardwarestore.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10001"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 1,
    "name": "Main Hardware Store",
    "description": "Primary hardware and tools business location",
    "address": "123 Main Street, Downtown",
    "phoneNumber": "+1-234-567-8900",
    "email": "main@hardwarestore.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10001",
    "isActive": true,
    "ownerId": 2,
    "ownerName": "Business Owner",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 2. 📝 **Create Second Shop (OWNER only)**

#### **POST** `/api/v1/shops`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Body (JSON):**
```json
{
    "name": "Downtown Branch",
    "description": "Second location in downtown area",
    "address": "456 Downtown Avenue, Suite 100",
    "phoneNumber": "+1-234-567-8901",
    "email": "downtown@hardwarestore.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10002"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 2,
    "name": "Downtown Branch",
    "description": "Second location in downtown area",
    "address": "456 Downtown Avenue, Suite 100",
    "phoneNumber": "+1-234-567-8901",
    "email": "downtown@hardwarestore.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10002",
    "isActive": true,
    "ownerId": 2,
    "ownerName": "Business Owner",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 3. **Create Shop with Minimal Data (OWNER only)**

#### **POST** `/api/v1/shops`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Body (JSON):**
```json
{
    "name": "Quick Mart",
    "description": "Small convenience store",
    "address": "789 Side Street",
    "phoneNumber": "+1-234-567-8902",
    "city": "New York",
    "state": "NY",
    "pincode": "10003"
}
```

**Expected Response (201 Created):**
```json
{
    "id": 3,
    "name": "Quick Mart",
    "description": "Small convenience store",
    "address": "789 Side Street",
    "phoneNumber": "+1-234-567-8902",
    "email": null,
    "gstNumber": null,
    "panNumber": null,
    "city": "New York",
    "state": "NY",
    "pincode": "10003",
    "isActive": true,
    "ownerId": 2,
    "ownerName": "Business Owner",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 4. **Update Shop (OWNER only)**

#### **PUT** `/api/v1/shops/1`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Body (JSON):**
```json
{
    "name": "Main Hardware Store - Updated",
    "description": "Updated primary hardware and tools business location",
    "address": "123 Main Street, Downtown, Floor 2",
    "phoneNumber": "+1-234-567-8900",
    "email": "main.updated@hardwarestore.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10001"
}
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "name": "Main Hardware Store - Updated",
    "description": "Updated primary hardware and tools business location",
    "address": "123 Main Street, Downtown, Floor 2",
    "phoneNumber": "+1-234-567-8900",
    "email": "main.updated@hardwarestore.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10001",
    "isActive": true,
    "ownerId": 2,
    "ownerName": "Business Owner",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 5. 📝 **Get All Shops (OWNER only)**

#### **GET** `/api/v1/shops`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "Main Hardware Store - Updated",
        "description": "Updated primary hardware and tools business location",
        "address": "123 Main Street, Downtown, Floor 2",
        "phoneNumber": "+1-234-567-8900",
        "email": "main.updated@hardwarestore.com",
        "city": "New York",
        "state": "NY",
        "isActive": true
    },
    {
        "id": 2,
        "name": "Downtown Branch",
        "description": "Second location in downtown area",
        "address": "456 Downtown Avenue, Suite 100",
        "phoneNumber": "+1-234-567-8901",
        "email": "downtown@hardwarestore.com",
        "city": "New York",
        "state": "NY",
        "isActive": true
    },
    {
        "id": 3,
        "name": "Quick Mart",
        "description": "Small convenience store",
        "address": "789 Side Street",
        "phoneNumber": "+1-234-567-8902",
        "city": "New York",
        "state": "NY",
        "isActive": true
    }
]
```

---

### 6. **Get Shop by ID (OWNER only)**

#### **GET** `/api/v1/shops/1`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "name": "Main Hardware Store - Updated",
    "description": "Updated primary hardware and tools business location",
    "address": "123 Main Street, Downtown, Floor 2",
    "phoneNumber": "+1-234-567-8900",
    "email": "main.updated@hardwarestore.com",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "city": "New York",
    "state": "NY",
    "pincode": "10001",
    "isActive": true,
    "ownerId": 2,
    "ownerName": "Business Owner",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 7. 📝 **Deactivate Shop (OWNER only)**

#### **PUT** `/api/v1/shops/3/deactivate`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
{
    "id": 3,
    "isActive": false,
    "message": "Shop deactivated successfully"
}
```

---

### 8. 📝 **Get Shops by City (OWNER only)**

#### **GET** `/api/v1/shops/city/New York`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "Main Hardware Store - Updated",
        "city": "New York",
        "state": "NY",
        "isActive": true
    },
    {
        "id": 2,
        "name": "Downtown Branch",
        "city": "New York",
        "state": "NY",
        "isActive": true
    },
    {
        "id": 3,
        "name": "Quick Mart",
        "city": "New York",
        "state": "NY",
        "isActive": false
    }
]
```

---

### 9. 📝 **Get Shops by State (OWNER only)**

#### **GET** `/api/v1/shops/state/NY`

**Headers:**
```
Authorization: Bearer {{authToken}} (OWNER role required)
```

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "Main Hardware Store - Updated",
        "city": "New York",
        "state": "NY",
        "isActive": true
    },
    {
        "id": 2,
        "name": "Downtown Branch",
        "city": "New York",
        "state": "NY",
        "isActive": true
    },
    {
        "id": 3,
        "name": "Quick Mart",
        "city": "New York",
        "state": "NY",
        "isActive": false
    }
]
```

---

## 🔧 **Environment Variables Setup**

In Postman, create these environment variables:

```
baseUrl: http://localhost:8080/api/v1
authToken: (leave empty, will be set after login)
```

## **Testing Workflow**

1. **Start with Health Check** → Verify backend is running
2. **Register Staff User** → Create test account
3. **Login** → Get JWT token and set `authToken` variable
4. **Create Shop** → Set up business location
5. **Create Customer** → Add customer data
6. **Create Ledger Entry** → Record transaction
7. **Create Payment** → Record payment
8. **Test all other endpoints** → Verify functionality

## 🏗️ **Complete User Hierarchy Flow**

### **Step 1: ADMIN Operations**
1. **ADMIN creates OWNER** → `POST /api/v1/admin/users/owners`
2. **ADMIN manages OWNER accounts** → Update, Delete, List

### **Step 2: OWNER Operations**
1. **OWNER creates SHOP** → `POST /api/v1/shops`
2. **OWNER creates STAFF** → `POST /api/v1/owner/users`
3. **OWNER assigns STAFF to SHOP** → `POST /api/v1/owner/staff-shop-assignments`

### **Step 3: STAFF Operations**
1. **STAFF creates CUSTOMERS** → `POST /api/v1/customers`
2. **STAFF manages LEDGER** → `POST /api/v1/ledger`
3. **STAFF records PAYMENTS** → `POST /api/v1/payments`

## 🔑 **Role-Based Access Control**

- **ADMIN**: Can create/manage OWNER users
- **OWNER**: Can create/manage SHOPS and STAFF users
- **STAFF**: Can manage CUSTOMERS, LEDGER, and PAYMENTS in assigned shop

## 📋 **Test Data for Complete Flow**

**1. Create ADMIN (manually in database first):**
```json
{
    "username": "adminuser",
    "email": "admin@ledgerly.com",
    "password": "admin123",
    "firstName": "Admin",
    "lastName": "User",
    "role": "ADMIN"
}
```

**2. ADMIN creates OWNER:**
```json
{
    "username": "owneruser",
    "email": "owner@business.com",
    "password": "owner123",
    "firstName": "Business",
    "lastName": "Owner",
    "role": "OWNER"
}
```

**3. OWNER creates SHOP:**
```json
{
    "name": "Main Business",
    "description": "Primary business location",
    "address": "123 Business Street",
    "phoneNumber": "+1234567890",
    "email": "business@example.com",
    "city": "Business City",
    "state": "BC",
    "pincode": "12345"
}
```

**4. OWNER creates STAFF:**
```json
{
    "username": "staffuser",
    "email": "staff@business.com",
    "password": "staff123",
    "firstName": "Business",
    "lastName": "Staff",
    "role": "STAFF"
}
```

## 📋 **Quick Test Data**

**Test User:**
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

**Test Shop:**
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

**Test Customer:**
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

## 📋 **Test Data Variations**

**Hardware Store:**
```json
{
    "name": "Hardware Plus",
    "description": "Complete hardware solutions",
    "address": "321 Tool Street",
    "phoneNumber": "+1-555-123-4567",
    "email": "info@hardwareplus.com",
    "gstNumber": "GST987654321",
    "panNumber": "HARD1234W",
    "city": "Chicago",
    "state": "IL",
    "pincode": "60601"
}
```

**Restaurant:**
```json
{
    "name": "Tasty Bites",
    "description": "Family restaurant",
    "address": "654 Food Avenue",
    "phoneNumber": "+1-555-987-6543",
    "email": "contact@tastybites.com",
    "gstNumber": "GST456789123",
    "panNumber": "REST5678T",
    "city": "Los Angeles",
    "state": "CA",
    "pincode": "90210"
}
```

**Electronics Store:**
```json
{
    "name": "Tech World",
    "description": "Latest electronics and gadgets",
    "address": "987 Tech Boulevard",
    "phoneNumber": "+1-555-456-7890",
    "email": "sales@techworld.com",
    "gstNumber": "GST789123456",
    "panNumber": "TECH9012E",
    "city": "Miami",
    "state": "FL",
    "pincode": "33101"
}
```

## 📋 **Shop Creation Workflow**

### **Step 1: OWNER Login**
```json
POST /api/v1/auth/login
{
    "username": "owneruser",
    "password": "owner123"
}
```

### **Step 2: Create Multiple Shops**
1. **Main Store** → Primary business location
2. **Downtown Branch** → Second location
3. **Quick Mart** → Small convenience store

### **Step 3: Manage Shops**
- Update shop details
- Deactivate inactive shops
- Search shops by location
- View all shops

## Notes
- All dates should be in ISO format: `YYYY-MM-DD`
- Amounts should be decimal numbers (e.g., `1500.00`)
- IDs in URLs should be replaced with actual entity IDs
- The collection uses Bearer token authentication automatically
- Some endpoints may require specific user roles to access
