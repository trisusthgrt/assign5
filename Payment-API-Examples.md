# 💰 Payment Management & Settlement System - API Documentation

## Base URL
```
http://localhost:8080/api/v1/payments
```

## 🔐 Authentication Required
**All endpoints require JWT token in Authorization header:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

---

## 📋 **Payment System Features**

### **✅ Payment Recording**
- Record cash, check, bank transfer, and advance payments
- Support for partial and full payment applications
- Reference numbers and bank details tracking
- Multiple payment methods and status tracking

### **✅ Partial Settlements**
- Apply payments to specific ledger entries
- Auto-application to oldest outstanding entries
- Reverse payment applications with audit trail
- Settlement suggestions based on outstanding balances

### **✅ Outstanding Balance Tracking**
- Real-time outstanding balance calculations
- Aging analysis with days outstanding
- Unapplied payments tracking
- Net outstanding balance with payment offsets

### **✅ Advanced Features**
- Payment status management (PENDING, PROCESSED, PARTIAL, etc.)
- Business rule validation for all payment operations
- Complete audit logging for financial compliance
- Settlement suggestions for efficient payment processing

---

## 💰 **Payment Recording Endpoints**

### **1. Record a New Payment**

#### **POST** `/api/v1/payments`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**Request Body:**
```json
{
    "customerId": 1,
    "paymentDate": "2025-08-19",
    "amount": 5000.00,
    "description": "Payment for Invoice #INV-2025-001",
    "notes": "Cash payment received at office",
    "referenceNumber": "PAY-2025-001",
    "paymentMethod": "CASH",
    "status": "PENDING",
    "bankDetails": null,
    "checkNumber": null,
    "isAdvancePayment": false
}
```

**Expected Response (201 Created):**
```json
{
    "success": true,
    "message": "Payment recorded successfully",
    "payment": {
        "id": 25,
        "paymentDate": "2025-08-19",
        "amount": 5000.00,
        "appliedAmount": 0.00,
        "remainingAmount": 5000.00,
        "description": "Payment for Invoice #INV-2025-001",
        "notes": "Cash payment received at office",
        "referenceNumber": "PAY-2025-001",
        "paymentMethod": "CASH",
        "status": "PENDING",
        "bankDetails": null,
        "checkNumber": null,
        "processedDate": null,
        "isAdvancePayment": false,
        "isActive": true,
        "customerId": 1,
        "customerName": "John Doe",
        "createdByUsername": "owner",
        "updatedByUsername": null,
        "createdAt": "2025-08-19T01:55:00",
        "updatedAt": null,
        "applications": [],
        "unappliedAmount": 5000.00,
        "fullyApplied": false,
        "applicationPercentage": 0.0
    }
}
```

### **2. Check Payment Example**

#### **POST** `/api/v1/payments`

**Request Body:**
```json
{
    "customerId": 2,
    "paymentDate": "2025-08-19",
    "amount": 7500.50,
    "description": "Check payment for outstanding balance",
    "notes": "Check received via mail",
    "referenceNumber": "CHK-2025-002",
    "paymentMethod": "CHECK",
    "status": "PENDING",
    "bankDetails": "ABC Bank, Account: 1234567890",
    "checkNumber": "CHK-001234",
    "isAdvancePayment": false
}
```

### **3. Bank Transfer Example**

#### **POST** `/api/v1/payments`

**Request Body:**
```json
{
    "customerId": 3,
    "paymentDate": "2025-08-19",
    "amount": 12000.00,
    "description": "Wire transfer payment",
    "notes": "Bank transfer received",
    "referenceNumber": "TXN-2025-003",
    "paymentMethod": "BANK_TRANSFER",
    "status": "PROCESSED",
    "bankDetails": "Wire from XYZ Bank, Ref: TXN123456",
    "checkNumber": null,
    "isAdvancePayment": false
}
```

### **4. Advance Payment Example**

#### **POST** `/api/v1/payments`

**Request Body:**
```json
{
    "customerId": 1,
    "paymentDate": "2025-08-19",
    "amount": 3000.00,
    "description": "Advance payment for future orders",
    "notes": "Customer paid in advance",
    "referenceNumber": "ADV-2025-001",
    "paymentMethod": "CASH",
    "status": "PENDING",
    "bankDetails": null,
    "checkNumber": null,
    "isAdvancePayment": true
}
```

---

## 🎯 **Payment Application & Settlement Endpoints**

### **1. Apply Payment to Specific Ledger Entries**

#### **POST** `/api/v1/payments/apply`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**Request Body:**
```json
{
    "paymentId": 25,
    "notes": "Applying payment to outstanding invoices",
    "applications": [
        {
            "ledgerEntryId": 15,
            "appliedAmount": 2500.00,
            "applicationNotes": "Partial payment for Invoice #001"
        },
        {
            "ledgerEntryId": 18,
            "appliedAmount": 2500.00,
            "applicationNotes": "Full payment for Invoice #002"
        }
    ]
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Payment applied successfully",
    "applicationsCount": 2,
    "payment": {
        "id": 25,
        "paymentDate": "2025-08-19",
        "amount": 5000.00,
        "appliedAmount": 5000.00,
        "remainingAmount": 0.00,
        "status": "PROCESSED",
        "applications": [
            {
                "id": 101,
                "appliedAmount": 2500.00,
                "applicationNotes": "Partial payment for Invoice #001",
                "paymentId": 25,
                "ledgerEntryId": 15,
                "ledgerEntryDescription": "Invoice #001 - Office supplies",
                "ledgerEntryAmount": 5000.00,
                "appliedByUsername": "owner",
                "appliedAt": "2025-08-19T01:58:00",
                "isReversed": false
            },
            {
                "id": 102,
                "appliedAmount": 2500.00,
                "applicationNotes": "Full payment for Invoice #002",
                "paymentId": 25,
                "ledgerEntryId": 18,
                "ledgerEntryDescription": "Invoice #002 - Equipment rental",
                "ledgerEntryAmount": 2500.00,
                "appliedByUsername": "owner",
                "appliedAt": "2025-08-19T01:58:00",
                "isReversed": false
            }
        ],
        "unappliedAmount": 0.00,
        "fullyApplied": true,
        "applicationPercentage": 100.0
    }
}
```

### **2. Auto-Apply Payment to Oldest Outstanding Entries**

#### **POST** `/api/v1/payments/{paymentId}/auto-apply`

**Example:** `/api/v1/payments/25/auto-apply`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Payment auto-applied successfully",
    "payment": {
        "id": 25,
        "amount": 5000.00,
        "appliedAmount": 5000.00,
        "remainingAmount": 0.00,
        "status": "PROCESSED",
        "applications": [
            {
                "id": 103,
                "appliedAmount": 3000.00,
                "applicationNotes": "Auto-applied to oldest outstanding entries",
                "ledgerEntryId": 12,
                "ledgerEntryDescription": "Invoice #INV-001 (Oldest)",
                "appliedAt": "2025-08-19T02:00:00"
            },
            {
                "id": 104,
                "appliedAmount": 2000.00,
                "applicationNotes": "Auto-applied to oldest outstanding entries",
                "ledgerEntryId": 14,
                "ledgerEntryDescription": "Invoice #INV-003",
                "appliedAt": "2025-08-19T02:00:00"
            }
        ]
    }
}
```

### **3. Reverse Payment Application** (Admin/Owner Only)

#### **DELETE** `/api/v1/payments/applications/{applicationId}`

**Example:** `/api/v1/payments/applications/103`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Payment application reversed successfully"
}
```

---

## 📊 **Outstanding Balance & Tracking Endpoints**

### **1. Get Outstanding Balance for Customer**

#### **GET** `/api/v1/payments/customer/{customerId}/outstanding-balance`

**Example:** `/api/v1/payments/customer/1/outstanding-balance`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "outstandingBalance": {
        "customerId": 1,
        "customerName": "John Doe",
        "totalOutstandingBalance": 8500.00,
        "totalCurrentBalance": 8500.00,
        "totalUnappliedPayments": 2000.00,
        "netOutstandingBalance": 6500.00,
        "totalOutstandingEntries": 3,
        "totalUnappliedPaymentCount": 1,
        "oldestOutstandingDate": "2025-07-15",
        "averageDaysOutstanding": 25,
        "hasOutstandingBalance": true,
        "hasUnappliedPayments": true,
        "outstandingEntries": [
            {
                "ledgerEntryId": 15,
                "transactionDate": "2025-07-15",
                "description": "Invoice #001 - Office supplies",
                "originalAmount": 5000.00,
                "outstandingAmount": 2500.00,
                "daysOutstanding": 35
            },
            {
                "ledgerEntryId": 18,
                "transactionDate": "2025-08-01",
                "description": "Invoice #002 - Equipment rental",
                "originalAmount": 3000.00,
                "outstandingAmount": 3000.00,
                "daysOutstanding": 18
            },
            {
                "ledgerEntryId": 22,
                "transactionDate": "2025-08-10",
                "description": "Invoice #003 - Consulting services",
                "originalAmount": 3000.00,
                "outstandingAmount": 3000.00,
                "daysOutstanding": 9
            }
        ],
        "unappliedPayments": [
            {
                "paymentId": 26,
                "paymentDate": "2025-08-18",
                "description": "Partial advance payment",
                "totalAmount": 3000.00,
                "appliedAmount": 1000.00,
                "unappliedAmount": 2000.00,
                "paymentMethod": "CASH"
            }
        ]
    }
}
```

### **2. Get Unapplied Payments for Customer**

#### **GET** `/api/v1/payments/customer/{customerId}/unapplied`

**Example:** `/api/v1/payments/customer/1/unapplied`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customerId": 1,
    "count": 2,
    "unappliedPayments": [
        {
            "id": 26,
            "paymentDate": "2025-08-18",
            "amount": 3000.00,
            "appliedAmount": 1000.00,
            "remainingAmount": 2000.00,
            "description": "Partial advance payment",
            "status": "PARTIAL",
            "paymentMethod": "CASH",
            "unappliedAmount": 2000.00,
            "fullyApplied": false
        },
        {
            "id": 27,
            "paymentDate": "2025-08-19",
            "amount": 1500.00,
            "appliedAmount": 0.00,
            "remainingAmount": 1500.00,
            "description": "Check payment",
            "status": "PENDING",
            "paymentMethod": "CHECK",
            "checkNumber": "CHK-001235",
            "unappliedAmount": 1500.00,
            "fullyApplied": false
        }
    ]
}
```

### **3. Get Payment Summary for Customer**

#### **GET** `/api/v1/payments/customer/{customerId}/summary`

**Example:** `/api/v1/payments/customer/1/summary`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "summary": {
        "customerId": 1,
        "customerName": "John Doe",
        "totalOutstandingBalance": 8500.00,
        "totalUnappliedPayments": 3500.00,
        "netOutstandingBalance": 5000.00,
        "outstandingEntriesCount": 3,
        "unappliedPaymentsCount": 2,
        "oldestOutstandingDate": "2025-07-15",
        "averageDaysOutstanding": 25,
        "hasOutstandingBalance": true,
        "hasUnappliedPayments": true
    }
}
```

### **4. Get Settlement Suggestions**

#### **GET** `/api/v1/payments/customer/{customerId}/settlement-suggestions`

**Example:** `/api/v1/payments/customer/1/settlement-suggestions`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customerId": 1,
    "suggestionsCount": 2,
    "suggestions": [
        {
            "paymentId": 26,
            "paymentDescription": "Partial advance payment",
            "paymentAmount": 3000.00,
            "unappliedAmount": 2000.00,
            "totalSuggestedAmount": 2000.00,
            "suggestedApplications": [
                {
                    "ledgerEntryId": 15,
                    "description": "Invoice #001 - Office supplies",
                    "outstandingAmount": 2500.00,
                    "suggestedAmount": 2000.00,
                    "daysOutstanding": 35
                }
            ]
        },
        {
            "paymentId": 27,
            "paymentDescription": "Check payment",
            "paymentAmount": 1500.00,
            "unappliedAmount": 1500.00,
            "totalSuggestedAmount": 1500.00,
            "suggestedApplications": [
                {
                    "ledgerEntryId": 15,
                    "description": "Invoice #001 - Office supplies",
                    "outstandingAmount": 500.00,
                    "suggestedAmount": 500.00,
                    "daysOutstanding": 35
                },
                {
                    "ledgerEntryId": 18,
                    "description": "Invoice #002 - Equipment rental",
                    "outstandingAmount": 3000.00,
                    "suggestedAmount": 1000.00,
                    "daysOutstanding": 18
                }
            ]
        }
    ]
}
```

---

## 📋 **Payment Information Endpoints**

### **1. Get Payment by ID**

#### **GET** `/api/v1/payments/{id}`

**Example:** `/api/v1/payments/25`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "payment": {
        "id": 25,
        "paymentDate": "2025-08-19",
        "amount": 5000.00,
        "appliedAmount": 5000.00,
        "remainingAmount": 0.00,
        "description": "Payment for Invoice #INV-2025-001",
        "notes": "Cash payment received at office",
        "referenceNumber": "PAY-2025-001",
        "paymentMethod": "CASH",
        "status": "PROCESSED",
        "isAdvancePayment": false,
        "isActive": true,
        "customerId": 1,
        "customerName": "John Doe",
        "createdByUsername": "owner",
        "createdAt": "2025-08-19T01:55:00",
        "applications": [
            {
                "id": 101,
                "appliedAmount": 2500.00,
                "ledgerEntryId": 15,
                "ledgerEntryDescription": "Invoice #001 - Office supplies",
                "appliedAt": "2025-08-19T01:58:00"
            },
            {
                "id": 102,
                "appliedAmount": 2500.00,
                "ledgerEntryId": 18,
                "ledgerEntryDescription": "Invoice #002 - Equipment rental",
                "appliedAt": "2025-08-19T01:58:00"
            }
        ]
    }
}
```

### **2. Get Payments for Customer with Pagination**

#### **GET** `/api/v1/payments/customer/{customerId}?page=0&size=10&sortBy=paymentDate&sortDir=desc`

**Example:** `/api/v1/payments/customer/1?page=0&size=5&sortBy=paymentDate&sortDir=desc`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "customerId": 1,
    "payments": [
        {
            "id": 27,
            "paymentDate": "2025-08-19",
            "amount": 1500.00,
            "appliedAmount": 0.00,
            "status": "PENDING",
            "paymentMethod": "CHECK",
            "checkNumber": "CHK-001235"
        },
        {
            "id": 26,
            "paymentDate": "2025-08-18",
            "amount": 3000.00,
            "appliedAmount": 1000.00,
            "status": "PARTIAL",
            "paymentMethod": "CASH"
        },
        {
            "id": 25,
            "paymentDate": "2025-08-19",
            "amount": 5000.00,
            "appliedAmount": 5000.00,
            "status": "PROCESSED",
            "paymentMethod": "CASH"
        }
    ],
    "totalElements": 15,
    "totalPages": 3,
    "currentPage": 0,
    "size": 5
}
```

### **3. Get All Payment Statuses**

#### **GET** `/api/v1/payments/statuses`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "paymentStatuses": [
        {
            "name": "PENDING",
            "displayName": "Pending",
            "description": "Payment recorded but not yet processed"
        },
        {
            "name": "PROCESSED",
            "displayName": "Processed",
            "description": "Payment has been successfully processed"
        },
        {
            "name": "CANCELLED",
            "displayName": "Cancelled",
            "description": "Payment has been cancelled"
        },
        {
            "name": "FAILED",
            "displayName": "Failed",
            "description": "Payment processing failed"
        },
        {
            "name": "PARTIAL",
            "displayName": "Partial",
            "description": "Partial payment received"
        },
        {
            "name": "REFUNDED",
            "displayName": "Refunded",
            "description": "Payment has been refunded"
        }
    ]
}
```

---

## 🚨 **Error Handling Examples**

### **1. Business Rule Violation - Insufficient Payment Amount**

**Request:**
```json
POST /api/v1/payments/apply
{
    "paymentId": 25,
    "applications": [
        {
            "ledgerEntryId": 15,
            "appliedAmount": 6000.00
        }
    ]
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "INSUFFICIENT_PAYMENT_AMOUNT",
    "message": "Payment has insufficient unapplied amount. Available: 2000.00, Requested: 6000.00"
}
```

### **2. Customer Mismatch Error**

**Request:**
```json
POST /api/v1/payments/apply
{
    "paymentId": 25,
    "applications": [
        {
            "ledgerEntryId": 99,
            "appliedAmount": 1000.00
        }
    ]
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "CUSTOMER_MISMATCH",
    "message": "Ledger entry must belong to the same customer as the payment"
}
```

### **3. Invalid Payment Status**

**Request:**
```json
POST /api/v1/payments/apply
{
    "paymentId": 30,
    "applications": [...]
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "INVALID_PAYMENT_STATUS",
    "message": "Cannot apply cancelled, failed, or inactive payments"
}
```

### **4. Payment Not Found**

**Request:**
```json
GET /api/v1/payments/999
```

**Response (404 Not Found):**
```json
{
    "success": false,
    "message": "Payment not found with id: 999"
}
```

---

## 💡 **Payment Workflow Examples**

### **Scenario 1: Complete Payment Processing**

1. **Record Payment:**
```bash
POST /api/v1/payments
# Customer pays $5000
```

2. **Check Outstanding Balance:**
```bash
GET /api/v1/payments/customer/1/outstanding-balance
# See what customer owes
```

3. **Get Settlement Suggestions:**
```bash
GET /api/v1/payments/customer/1/settlement-suggestions
# Get automatic suggestions
```

4. **Apply Payment:**
```bash
POST /api/v1/payments/apply
# Apply to specific invoices
```

5. **Verify Results:**
```bash
GET /api/v1/payments/25
# Check payment status
```

### **Scenario 2: Auto-Settlement**

1. **Record Payment:**
```bash
POST /api/v1/payments
```

2. **Auto-Apply to Oldest Entries:**
```bash
POST /api/v1/payments/25/auto-apply
```

3. **Check Results:**
```bash
GET /api/v1/payments/customer/1/summary
```

### **Scenario 3: Partial Settlement Management**

1. **Check Unapplied Payments:**
```bash
GET /api/v1/payments/customer/1/unapplied
```

2. **Apply Partial Amount:**
```bash
POST /api/v1/payments/apply
# Apply part of payment
```

3. **Apply Remaining Later:**
```bash
POST /api/v1/payments/apply
# Apply rest when ready
```

---

## 🎯 **Key Features Summary**

### **✅ Payment Recording**
- **Multiple Payment Methods**: Cash, Check, Bank Transfer, Credit Card
- **Advanced Tracking**: Reference numbers, bank details, check numbers
- **Status Management**: PENDING → PARTIAL → PROCESSED workflow
- **Advance Payments**: Support for prepaid amounts

### **✅ Intelligent Settlement**
- **Manual Application**: Apply payments to specific invoices
- **Auto-Application**: Automatically apply to oldest outstanding entries
- **Settlement Suggestions**: AI-powered recommendations for optimal settlement
- **Partial Applications**: Support for partial payment applications

### **✅ Outstanding Balance Tracking**
- **Real-time Calculations**: Current outstanding balances
- **Aging Analysis**: Days outstanding for each entry
- **Net Outstanding**: Outstanding balance minus unapplied payments
- **Comprehensive Reporting**: Detailed breakdowns and summaries

### **✅ Advanced Features**
- **Reversal Support**: Reverse payment applications with audit trail
- **Business Rule Integration**: All payment operations validated
- **Complete Audit Logging**: Every action tracked for compliance
- **Role-based Security**: Different access levels for different users

**Your Payment Management System is complete and operational!** 🎉

The system now provides:
- ✅ **Payment recording** with full details
- ✅ **Partial settlement** capabilities
- ✅ **Outstanding balance tracking** with aging
- ✅ **Settlement suggestions** for efficient processing
- ✅ **Auto-application** to oldest entries
- ✅ **Complete audit trails** for all financial operations
