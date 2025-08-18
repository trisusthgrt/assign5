# 🧑‍💼 Customer Management API - Postman Examples

## Base URL
```
http://localhost:8080/api/v1/customers
```

## 🔐 Authentication Required
**All endpoints require JWT token in Authorization header:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

---

## 1. 📝 **Create Customer**

### **POST** `/api/v1/customers`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**JSON Body:**
```json
{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1-234-567-8900",
    "address": "123 Main Street, City, State 12345",
    "businessName": "Doe Enterprises",
    "gstNumber": "GST123456789",
    "panNumber": "ABCDE1234F",
    "relationshipType": "CUSTOMER",
    "notes": "Regular customer, prefers email communication",
    "creditLimit": 50000.00
}
```

**Expected Response (201 Created):**
```json
{
    "success": true,
    "message": "Customer created successfully",
    "customer": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "phoneNumber": "+1-234-567-8900",
        "address": "123 Main Street, City, State 12345",
        "businessName": "Doe Enterprises",
        "gstNumber": "GST123456789",
        "panNumber": "ABCDE1234F",
        "relationshipType": "CUSTOMER",
        "notes": "Regular customer, prefers email communication",
        "creditLimit": 50000.00,
        "currentBalance": 0.00,
        "isActive": true,
        "createdAt": "2025-08-19T00:50:00",
        "updatedAt": "2025-08-19T00:50:00",
        "createdByUsername": "owner"
    }
}
```

---

## 2. 📋 **Get Customer by ID**

### **GET** `/api/v1/customers/{id}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example:** `GET /api/v1/customers/1`

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customer": {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "phoneNumber": "+1-234-567-8900",
        "address": "123 Main Street, City, State 12345",
        "businessName": "Doe Enterprises",
        "relationshipType": "CUSTOMER",
        "notes": "Regular customer, prefers email communication",
        "creditLimit": 50000.00,
        "currentBalance": 0.00,
        "isActive": true,
        "createdAt": "2025-08-19T00:50:00",
        "updatedAt": "2025-08-19T00:50:00",
        "createdByUsername": "owner"
    }
}
```

---

## 3. ✏️ **Update Customer**

### **PUT** `/api/v1/customers/{id}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**JSON Body (Partial Update):**
```json
{
    "name": "John Smith",
    "phoneNumber": "+1-234-567-8901",
    "notes": "Updated notes - VIP customer",
    "creditLimit": 75000.00
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Customer updated successfully",
    "customer": {
        "id": 1,
        "name": "John Smith",
        "email": "john.doe@example.com",
        "phoneNumber": "+1-234-567-8901",
        "notes": "Updated notes - VIP customer",
        "creditLimit": 75000.00,
        "currentBalance": 0.00,
        "isActive": true,
        "updatedAt": "2025-08-19T00:55:00"
    }
}
```

---

## 4. 🗑️ **Delete Customer** (Admin/Owner Only)

### **DELETE** `/api/v1/customers/{id}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Customer deleted successfully"
}
```

---

## 5. 📑 **Get All Customers (Paginated)**

### **GET** `/api/v1/customers?page=0&size=10&sortBy=name&sortDir=asc`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Query Parameters:**
- `page`: Page number (default: 0)
- `size`: Items per page (default: 10)
- `sortBy`: Sort field (default: name)
- `sortDir`: Sort direction (asc/desc, default: asc)

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customers": [
        {
            "id": 1,
            "name": "John Smith",
            "email": "john.doe@example.com",
            "relationshipType": "CUSTOMER",
            "isActive": true
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "size": 10
}
```

---

## 6. ✅ **Get Active Customers Only**

### **GET** `/api/v1/customers/active`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customers": [
        {
            "id": 1,
            "name": "John Smith",
            "email": "john.doe@example.com",
            "relationshipType": "CUSTOMER",
            "isActive": true
        }
    ],
    "count": 1
}
```

---

## 7. 🔍 **Search Customers**

### **GET** `/api/v1/customers/search?name=John&relationshipType=CUSTOMER&isActive=true`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Query Parameters:**
- `name`: Search by name (partial match)
- `email`: Search by email (partial match)
- `phoneNumber`: Search by phone number
- `relationshipType`: Filter by relationship type
- `isActive`: Filter by active status
- `businessName`: Search by business name
- `page`, `size`, `sortBy`, `sortDir`: Pagination & sorting

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customers": [
        {
            "id": 1,
            "name": "John Smith",
            "email": "john.doe@example.com",
            "relationshipType": "CUSTOMER",
            "isActive": true
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "size": 10
}
```

---

## 8. 📊 **Get Customers by Type**

### **GET** `/api/v1/customers/type/SUPPLIER`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Available Types:**
- CUSTOMER
- SUPPLIER
- VENDOR
- PARTNER
- CONTRACTOR
- FREELANCER
- OTHER

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customers": [
        {
            "id": 2,
            "name": "ABC Supplies",
            "relationshipType": "SUPPLIER",
            "isActive": true
        }
    ],
    "count": 1,
    "relationshipType": "SUPPLIER"
}
```

---

## 9. 🔍 **Search by Name**

### **GET** `/api/v1/customers/search/name?name=John`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customers": [
        {
            "id": 1,
            "name": "John Smith",
            "email": "john.doe@example.com",
            "isActive": true
        }
    ],
    "count": 1,
    "searchTerm": "John"
}
```

---

## 10. 💰 **Get Customers with Outstanding Balance** (Admin/Owner Only)

### **GET** `/api/v1/customers/outstanding-balance`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customers": [
        {
            "id": 3,
            "name": "Customer With Balance",
            "currentBalance": 5000.00,
            "creditLimit": 10000.00
        }
    ],
    "count": 1
}
```

---

## 11. 📈 **Get Customer Statistics** (Admin/Owner Only)

### **GET** `/api/v1/customers/stats`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "stats": {
        "totalCustomers": 10,
        "activeCustomers": 8,
        "totalCustomerType": 6,
        "totalSupplierType": 2
    }
}
```

---

## 12. 📋 **Get Relationship Types**

### **GET** `/api/v1/customers/relationship-types`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "relationshipTypes": [
        "CUSTOMER",
        "SUPPLIER",
        "VENDOR",
        "PARTNER",
        "CONTRACTOR",
        "FREELANCER",
        "OTHER"
    ]
}
```

---

## 🎯 **Sample Test Data**

### **Supplier Example:**
```json
{
    "name": "ABC Supplies Co",
    "email": "contact@abcsupplies.com",
    "phoneNumber": "+1-555-123-4567",
    "address": "456 Industrial Blvd, City, State 67890",
    "businessName": "ABC Supplies Co",
    "relationshipType": "SUPPLIER",
    "notes": "Reliable supplier for office materials",
    "creditLimit": 25000.00
}
```

### **Vendor Example:**
```json
{
    "name": "Tech Solutions Inc",
    "email": "sales@techsolutions.com",
    "phoneNumber": "+1-555-987-6543",
    "businessName": "Tech Solutions Inc",
    "relationshipType": "VENDOR",
    "notes": "IT equipment and software vendor"
}
```

### **Freelancer Example:**
```json
{
    "name": "Jane Developer",
    "email": "jane@freelance.com",
    "phoneNumber": "+1-555-246-8100",
    "relationshipType": "FREELANCER",
    "notes": "Freelance web developer, handles special projects"
}
```

---

## ⚠️ **Important Notes**

1. **Authentication**: All endpoints require valid JWT token
2. **Permissions**: 
   - STAFF, OWNER, ADMIN can view and create customers
   - Only OWNER, ADMIN can delete customers
   - Only OWNER, ADMIN can view statistics and outstanding balances
3. **Validation**: Email and phone numbers must be unique
4. **Soft Delete**: Deleting a customer marks it as inactive, doesn't remove from database
5. **Search**: All text searches are case-insensitive and support partial matching

---

## 🚀 **Ready to Test!**

Your Customer Management API is fully functional with:
- ✅ Complete CRUD operations
- ✅ Advanced search and filtering
- ✅ Role-based access control
- ✅ Contact info management
- ✅ Notes and relationship tracking
- ✅ Statistics and reporting

**Start with creating a customer and then explore all the endpoints!** 🎉
