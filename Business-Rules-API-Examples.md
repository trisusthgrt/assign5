# 🛡️ Business Rules & Audit System - API Documentation

## Base URLs
```
Business Rules: http://localhost:8080/api/v1/business-rules
Audit Logs: http://localhost:8080/api/v1/audit
```

## 🔐 Authentication Required
**All endpoints require JWT token in Authorization header:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

---

## 🛡️ **Business Rules Implemented**

### **1. Negative Balance Prevention** ⛔
- **Rule Code**: `INSUFFICIENT_BALANCE`
- **Description**: Prevents debit transactions that would result in negative customer balances
- **Configurable**: Yes (`app.business-rules.allow-negative-balance=false`)
- **Trigger**: Any DEBIT transaction that exceeds current balance

**Example Error Response:**
```json
{
    "success": false,
    "ruleCode": "INSUFFICIENT_BALANCE",
    "message": "Transaction would result in negative balance. Current: 1000.00, Requested: 1500.00, Resulting: -500.00",
    "details": {
        "currentBalance": 1000.00,
        "requestedAmount": 1500.00,
        "resultingBalance": -500.00
    }
}
```

### **2. Transaction Amount Validation** 💰
- **Rule Code**: `INVALID_AMOUNT`
- **Description**: Validates transaction amounts are within acceptable ranges
- **Rules**:
  - Amount must be greater than zero
  - Minimum amount: 0.01 (configurable)
  - Maximum amount: 1,000,000.00 (configurable)
  - Maximum 2 decimal places

**Example Error Response:**
```json
{
    "success": false,
    "ruleCode": "INVALID_AMOUNT",
    "message": "Transaction amount must be at least 0.01",
    "details": {
        "amount": 0.001,
        "validationRule": "BELOW_MIN_AMOUNT"
    }
}
```

### **3. Credit Limit Validation** 🏦
- **Rule Code**: `CREDIT_LIMIT_EXCEEDED`
- **Description**: Ensures customer credit limits are not exceeded
- **Trigger**: CREDIT transactions that would exceed customer's credit limit

**Example Error Response:**
```json
{
    "success": false,
    "ruleCode": "CREDIT_LIMIT_EXCEEDED",
    "message": "Credit limit of 50000.00 would be exceeded. New balance would be 52000.00"
}
```

### **4. Daily Transaction Limit** 📅
- **Rule Code**: `DAILY_LIMIT_EXCEEDED`
- **Description**: Limits total daily transaction amounts per customer
- **Configurable**: Yes (`app.business-rules.max-daily-transaction-limit=100000.00`)

**Example Error Response:**
```json
{
    "success": false,
    "ruleCode": "DAILY_LIMIT_EXCEEDED",
    "message": "Daily transaction limit of 100000.00 would be exceeded. Today's total would be 105000.00"
}
```

### **5. Date Validation** 📆
- **Rule Codes**: `FUTURE_DATE_NOT_ALLOWED`, `DATE_TOO_OLD`
- **Description**: Validates transaction dates are reasonable
- **Rules**:
  - Future dates prohibited (configurable)
  - Dates older than 1 year prohibited

**Example Error Response:**
```json
{
    "success": false,
    "ruleCode": "FUTURE_DATE_NOT_ALLOWED",
    "message": "Transaction date cannot be in the future"
}
```

### **6. Reconciliation Protection** 🔒
- **Rule Code**: `RECONCILED_ENTRY_MODIFICATION`
- **Description**: Prevents modification of reconciled entries
- **Non-configurable**: Hard business rule

### **7. Customer Status Validation** 👤
- **Rule Code**: `INACTIVE_CUSTOMER`
- **Description**: Prevents transactions for inactive customers
- **Non-configurable**: Hard business rule

### **8. Deletion Protection** 🗑️
- **Rule Code**: `OLD_ENTRY_DELETION`
- **Description**: Only administrators can delete entries older than 30 days
- **Non-configurable**: Hard business rule

---

## 📋 **Business Rules API Endpoints**

### **1. Get Business Rule Configuration** (Admin/Owner Only)

#### **GET** `/api/v1/business-rules/configuration`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "configuration": {
        "allowNegativeBalance": false,
        "maxTransactionAmount": 1000000.00,
        "minTransactionAmount": 0.01,
        "maxDailyTransactionLimit": 100000.00,
        "requireFutureDateValidation": true
    }
}
```

### **2. Get Business Rule Descriptions**

#### **GET** `/api/v1/business-rules/descriptions`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "rules": {
        "NEGATIVE_BALANCE_PREVENTION": {
            "code": "INSUFFICIENT_BALANCE",
            "description": "Prevents transactions that would result in negative customer balances",
            "configurable": true
        },
        "TRANSACTION_AMOUNT_VALIDATION": {
            "code": "INVALID_AMOUNT",
            "description": "Validates transaction amounts are within acceptable ranges",
            "rules": {
                "minAmount": "Minimum transaction amount (configurable)",
                "maxAmount": "Maximum transaction amount (configurable)",
                "positiveOnly": "Amount must be greater than zero",
                "decimalPlaces": "Maximum 2 decimal places allowed"
            }
        },
        "CREDIT_LIMIT_VALIDATION": {
            "code": "CREDIT_LIMIT_EXCEEDED",
            "description": "Ensures customer credit limits are not exceeded",
            "configurable": false
        }
    }
}
```

---

## 📊 **Audit System API Endpoints**

### **1. Search Audit Logs** (Admin/Owner Only)

#### **GET** `/api/v1/audit/logs?action=CREATE_LEDGER_ENTRY&success=false&page=0&size=20`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Query Parameters:**
- `userId`: Filter by user ID
- `action`: Filter by action (e.g., CREATE_LEDGER_ENTRY, UPDATE_LEDGER_ENTRY)
- `entityType`: Filter by entity type (LEDGER_ENTRY, CUSTOMER, USER)
- `entityId`: Filter by specific entity ID
- `success`: Filter by success status (true/false)
- `startDate`: Start date (ISO format: 2025-08-19T00:00:00)
- `endDate`: End date (ISO format: 2025-08-19T23:59:59)
- `page`, `size`, `sortBy`, `sortDir`: Pagination & sorting

**Expected Response (200 OK):**
```json
{
    "success": true,
    "logs": [
        {
            "id": 1,
            "action": "CREATE_LEDGER_ENTRY",
            "entityType": "LEDGER_ENTRY",
            "entityId": 123,
            "description": "Created CREDIT entry of 5000.00 for customer John Doe",
            "success": true,
            "createdAt": "2025-08-19T01:30:00",
            "username": "owner",
            "ipAddress": null,
            "userAgent": null,
            "oldValues": null,
            "newValues": "{\"id\":123,\"amount\":5000.00,...}"
        },
        {
            "id": 2,
            "action": "BUSINESS_RULE_VIOLATION",
            "entityType": "LEDGER_ENTRY",
            "entityId": null,
            "description": "Rule: INSUFFICIENT_BALANCE - Transaction would result in negative balance",
            "success": false,
            "errorMessage": "Transaction would result in negative balance. Current: 1000.00, Requested: 1500.00, Resulting: -500.00",
            "createdAt": "2025-08-19T01:25:00",
            "username": "staff_user"
        }
    ],
    "totalElements": 150,
    "totalPages": 8,
    "currentPage": 0,
    "size": 20
}
```

### **2. Get Audit Logs for Specific Entity**

#### **GET** `/api/v1/audit/entity/LEDGER_ENTRY/123`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "logs": [
        {
            "id": 1,
            "action": "CREATE_LEDGER_ENTRY",
            "entityType": "LEDGER_ENTRY",
            "entityId": 123,
            "description": "Created CREDIT entry of 5000.00 for customer John Doe",
            "success": true,
            "createdAt": "2025-08-19T01:30:00",
            "username": "owner"
        },
        {
            "id": 15,
            "action": "UPDATE_LEDGER_ENTRY",
            "entityType": "LEDGER_ENTRY",
            "entityId": 123,
            "description": "Updated ledger entry 123 for customer John Doe",
            "success": true,
            "createdAt": "2025-08-19T02:15:00",
            "username": "owner"
        }
    ],
    "entityType": "LEDGER_ENTRY",
    "entityId": 123,
    "count": 2
}
```

### **3. Get Failed Operations** (Admin Only)

#### **GET** `/api/v1/audit/failures?page=0&size=20`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "failedOperations": [
        {
            "id": 25,
            "action": "BUSINESS_RULE_VIOLATION",
            "entityType": "CUSTOMER",
            "entityId": 5,
            "description": "Rule: INSUFFICIENT_BALANCE - Transaction would result in negative balance",
            "success": false,
            "errorMessage": "Transaction would result in negative balance. Current: 500.00, Requested: 800.00, Resulting: -300.00",
            "createdAt": "2025-08-19T01:45:00",
            "username": "staff_user"
        },
        {
            "id": 23,
            "action": "CREATE_LEDGER_ENTRY",
            "entityType": "LEDGER_ENTRY",
            "entityId": null,
            "description": "Failed to create ledger entry for customer 10",
            "success": false,
            "errorMessage": "Customer not found with id: 10",
            "createdAt": "2025-08-19T01:20:00",
            "username": "staff_user"
        }
    ],
    "totalElements": 12,
    "totalPages": 1,
    "currentPage": 0,
    "size": 20
}
```

### **4. Get Recent Audit Logs**

#### **GET** `/api/v1/audit/recent?hours=24`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "recentLogs": [
        {
            "id": 30,
            "action": "CREATE_LEDGER_ENTRY",
            "entityType": "LEDGER_ENTRY",
            "entityId": 150,
            "description": "Created DEBIT entry of 2500.00 for customer Jane Smith",
            "success": true,
            "createdAt": "2025-08-19T02:00:00",
            "username": "owner"
        }
    ],
    "hours": 24,
    "count": 15
}
```

### **5. Get Audit Statistics** (Admin Only)

#### **GET** `/api/v1/audit/statistics`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "statistics": {
        "totalLogs": 500,
        "successfulLogs": 450,
        "failedLogs": 50,
        "successRate": "90.00%",
        "failureRate": "10.00%",
        "actionStatistics": [
            {
                "action": "CREATE_LEDGER_ENTRY",
                "totalCount": 200,
                "successCount": 185,
                "failureCount": 15
            },
            {
                "action": "UPDATE_LEDGER_ENTRY",
                "totalCount": 100,
                "successCount": 95,
                "failureCount": 5
            },
            {
                "action": "BUSINESS_RULE_VIOLATION",
                "totalCount": 50,
                "successCount": 0,
                "failureCount": 50
            }
        ],
        "userStatistics": [
            {
                "username": "owner",
                "totalCount": 300,
                "successCount": 290,
                "failureCount": 10
            },
            {
                "username": "staff_user",
                "totalCount": 150,
                "successCount": 120,
                "failureCount": 30
            }
        ]
    }
}
```

### **6. Get Audit Logs by User**

#### **GET** `/api/v1/audit/user/2?page=0&size=20`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "logs": [
        {
            "id": 45,
            "action": "CREATE_LEDGER_ENTRY",
            "entityType": "LEDGER_ENTRY",
            "entityId": 200,
            "description": "Created CREDIT entry of 3000.00 for customer ABC Corp",
            "success": true,
            "createdAt": "2025-08-19T01:45:00",
            "username": "staff_user"
        }
    ],
    "userId": 2,
    "totalElements": 85,
    "totalPages": 5,
    "currentPage": 0,
    "size": 20
}
```

---

## 🚨 **Business Rule Error Examples**

### **1. Insufficient Balance Error**
**Request:**
```json
POST /api/v1/ledger/entries
{
    "customerId": 1,
    "transactionDate": "2025-08-19",
    "transactionType": "DEBIT",
    "amount": 1500.00,
    "description": "Large payment"
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "INSUFFICIENT_BALANCE",
    "message": "Transaction would result in negative balance. Current: 1000.00, Requested: 1500.00, Resulting: -500.00",
    "details": {
        "currentBalance": 1000.00,
        "requestedAmount": 1500.00,
        "resultingBalance": -500.00
    }
}
```

### **2. Invalid Amount Error**
**Request:**
```json
POST /api/v1/ledger/entries
{
    "customerId": 1,
    "transactionDate": "2025-08-19",
    "transactionType": "CREDIT",
    "amount": 0.001,
    "description": "Tiny amount"
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "INVALID_AMOUNT",
    "message": "Transaction amount must be at least 0.01",
    "details": {
        "amount": 0.001,
        "validationRule": "BELOW_MIN_AMOUNT"
    }
}
```

### **3. Credit Limit Exceeded Error**
**Request:**
```json
POST /api/v1/ledger/entries
{
    "customerId": 1,
    "transactionDate": "2025-08-19",
    "transactionType": "CREDIT",
    "amount": 60000.00,
    "description": "Large credit"
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "CREDIT_LIMIT_EXCEEDED",
    "message": "Credit limit of 50000.00 would be exceeded. New balance would be 52000.00"
}
```

### **4. Future Date Error**
**Request:**
```json
POST /api/v1/ledger/entries
{
    "customerId": 1,
    "transactionDate": "2025-12-31",
    "transactionType": "CREDIT",
    "amount": 1000.00,
    "description": "Future transaction"
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "FUTURE_DATE_NOT_ALLOWED",
    "message": "Transaction date cannot be in the future"
}
```

### **5. Reconciled Entry Modification Error**
**Request:**
```json
PUT /api/v1/ledger/entries/123
{
    "amount": 2000.00,
    "description": "Updated amount"
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "RECONCILED_ENTRY_MODIFICATION",
    "message": "Cannot modify amount or type of reconciled entries"
}
```

---

## ⚙️ **Configuration Properties**

Add these to your `application.properties` to customize business rules:

```properties
# Business Rules Configuration
app.business-rules.allow-negative-balance=false
app.business-rules.max-transaction-amount=1000000.00
app.business-rules.min-transaction-amount=0.01
app.business-rules.max-daily-transaction-limit=100000.00
app.business-rules.require-future-date-validation=true
```

## 📋 **Audit Action Types**

| Action | Description | Entity Type |
|--------|-------------|-------------|
| `CREATE_LEDGER_ENTRY` | New ledger entry created | LEDGER_ENTRY |
| `UPDATE_LEDGER_ENTRY` | Ledger entry modified | LEDGER_ENTRY |
| `DELETE_LEDGER_ENTRY` | Ledger entry deleted (soft) | LEDGER_ENTRY |
| `CREATE_CUSTOMER` | New customer created | CUSTOMER |
| `UPDATE_CUSTOMER` | Customer information updated | CUSTOMER |
| `DELETE_CUSTOMER` | Customer deactivated | CUSTOMER |
| `USER_LOGIN` | User logged in | USER |
| `USER_LOGOUT` | User logged out | USER |
| `BUSINESS_RULE_VIOLATION` | Rule violation occurred | Various |
| `FILE_UPLOAD` | Document uploaded | DOCUMENT |
| `FILE_DOWNLOAD` | Document downloaded | DOCUMENT |

---

## 🎯 **Key Features**

### **✅ Business Rule Enforcement**
- **Real-time Validation**: All rules checked before transaction processing
- **Configurable Limits**: Many rules can be customized via properties
- **Detailed Error Messages**: Clear explanations of rule violations
- **Role-based Overrides**: Some rules have admin bypass capabilities

### **✅ Comprehensive Audit Logging**
- **Complete Trail**: Every action logged with user, timestamp, and details
- **Before/After Values**: Track changes with old and new values
- **Failure Tracking**: Business rule violations and system errors logged
- **Performance Optimized**: Async logging doesn't impact transaction performance

### **✅ Advanced Monitoring**
- **Statistics Dashboard**: Success/failure rates and trends
- **User Activity**: Track actions by individual users
- **Entity History**: Complete audit trail for any record
- **Failure Analysis**: Detailed failure reports for troubleshooting

---

## 🚀 **Testing Business Rules**

1. **Create a customer** with a low credit limit (e.g., 1000.00)
2. **Try adding a large credit** (e.g., 2000.00) - should fail
3. **Add a smaller credit** (e.g., 800.00) - should succeed  
4. **Try a large debit** (e.g., 1500.00) - should fail with insufficient balance
5. **Try a future date** - should fail if validation enabled
6. **Check audit logs** to see all violations recorded

**Your business rules and audit system is fully operational!** 🎉

All transactions are now protected by:
- ✅ **Negative balance prevention**
- ✅ **Amount validation**
- ✅ **Credit limit enforcement**
- ✅ **Date validation**
- ✅ **Reconciliation protection**
- ✅ **Complete audit trails**
