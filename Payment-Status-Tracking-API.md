# 📊 Payment Status Tracking System - API Documentation

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

## 📋 **Payment Status Tracking Features**

### **✅ Comprehensive Status Management**
- **PENDING**: Payment recorded but not yet processed
- **PAID**: Payment has been successfully completed
- **OVERDUE**: Payment is past due date with automatic detection
- **DISPUTED**: Payment is under dispute with full audit trail
- **PROCESSED**: Payment has been successfully processed
- **PARTIAL**: Partial payment received
- **CANCELLED**: Payment has been cancelled
- **FAILED**: Payment processing failed
- **REFUNDED**: Payment has been refunded
- **IN_COLLECTION**: Payment sent to collection agency
- **WRITTEN_OFF**: Payment written off as bad debt

### **✅ Advanced Tracking Capabilities**
- **Automatic Overdue Detection**: Daily scheduled task to check for overdue payments
- **Dispute Management**: Full dispute lifecycle with reasons and resolution tracking
- **Status History**: Complete audit trail of all status changes
- **Due Date Management**: Track payment due dates and calculate overdue days
- **Reminder System**: Track payment reminders sent to customers

### **✅ Real-time Reporting**
- **Status Summary Dashboard**: Get counts and percentages by status
- **Overdue Payments Report**: List all overdue payments with aging
- **Disputed Payments Report**: Track all payments under dispute
- **Payment Status Analytics**: Comprehensive status breakdown

---

## 🔄 **Payment Status Management Endpoints**

### **1. Update Payment Status**

#### **PUT** `/api/v1/payments/{paymentId}/status`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**Request Body:**
```json
{
    "status": "PAID",
    "statusNotes": "Payment confirmed via bank transfer",
    "dueDate": "2025-09-01"
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Payment status updated successfully",
    "newStatus": "PAID",
    "payment": {
        "id": 25,
        "paymentDate": "2025-08-19",
        "amount": 5000.00,
        "status": "PAID",
        "statusUpdatedAt": "2025-08-19T10:30:00",
        "statusUpdatedByUsername": "owner",
        "statusNotes": "Payment confirmed via bank transfer",
        "processedDate": "2025-08-19",
        "dueDate": "2025-09-01",
        "overdueDays": 0,
        "customerId": 1,
        "customerName": "John Doe",
        "canBeUpdated": false,
        "isProblematic": false
    }
}
```

### **2. Mark Payment as Overdue**

#### **PUT** `/api/v1/payments/{paymentId}/status`

**Request Body:**
```json
{
    "status": "OVERDUE",
    "statusNotes": "Payment is 15 days past due date",
    "dueDate": "2025-08-01"
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Payment status updated successfully",
    "newStatus": "OVERDUE",
    "payment": {
        "id": 26,
        "status": "OVERDUE",
        "dueDate": "2025-08-01",
        "overdueDays": 15,
        "statusUpdatedAt": "2025-08-19T10:35:00",
        "statusUpdatedByUsername": "admin",
        "statusNotes": "Payment is 15 days past due date",
        "isProblematic": true,
        "canBeUpdated": true
    }
}
```

---

## 🚨 **Dispute Management Endpoints**

### **1. Dispute a Payment**

#### **POST** `/api/v1/payments/{paymentId}/dispute`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**Request Body:**
```json
{
    "disputeReason": "Customer claims payment was already made via different method",
    "additionalNotes": "Customer provided receipt for cash payment on 2025-08-15"
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Payment disputed successfully",
    "disputeReason": "Customer claims payment was already made via different method",
    "payment": {
        "id": 27,
        "status": "DISPUTED",
        "disputeDate": "2025-08-19",
        "disputeReason": "Customer claims payment was already made via different method | Additional notes: Customer provided receipt for cash payment on 2025-08-15",
        "disputedByUsername": "staff",
        "statusUpdatedAt": "2025-08-19T11:00:00",
        "statusUpdatedByUsername": "staff",
        "statusNotes": "Payment disputed",
        "isDisputed": true,
        "isProblematic": true,
        "canBeUpdated": true
    }
}
```

### **2. Resolve Payment Dispute** (Admin/Owner Only)

#### **POST** `/api/v1/payments/{paymentId}/resolve-dispute?resolutionNotes=Verified cash payment with receipt, updating records`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Payment dispute resolved successfully",
    "resolutionNotes": "Verified cash payment with receipt, updating records",
    "payment": {
        "id": 27,
        "status": "PENDING",
        "disputeDate": null,
        "disputeReason": null,
        "disputedByUsername": null,
        "statusUpdatedAt": "2025-08-19T11:15:00",
        "statusUpdatedByUsername": "admin",
        "statusNotes": "Dispute resolved: Verified cash payment with receipt, updating records",
        "isDisputed": false,
        "isProblematic": false
    }
}
```

---

## 📊 **Payment Status Reporting Endpoints**

### **1. Get Payments by Status**

#### **GET** `/api/v1/payments/status/{status}?page=0&size=10&sortBy=paymentDate&sortDir=desc`

**Example:** `/api/v1/payments/status/OVERDUE?page=0&size=5`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "status": "OVERDUE",
    "payments": [
        {
            "id": 28,
            "paymentDate": "2025-07-15",
            "amount": 3500.00,
            "status": "OVERDUE",
            "dueDate": "2025-08-01",
            "overdueDays": 18,
            "statusUpdatedAt": "2025-08-19T09:00:00",
            "statusNotes": "Payment is 18 days overdue",
            "customerName": "ABC Company",
            "reminderCount": 2,
            "lastReminderSent": "2025-08-17"
        },
        {
            "id": 29,
            "paymentDate": "2025-07-20",
            "amount": 2200.00,
            "status": "OVERDUE",
            "dueDate": "2025-08-05",
            "overdueDays": 14,
            "statusUpdatedAt": "2025-08-19T09:00:00",
            "statusNotes": "Payment is 14 days overdue",
            "customerName": "XYZ Corp",
            "reminderCount": 1,
            "lastReminderSent": "2025-08-15"
        }
    ],
    "totalElements": 15,
    "totalPages": 3,
    "currentPage": 0,
    "size": 5
}
```

### **2. Get All Overdue Payments**

#### **GET** `/api/v1/payments/overdue`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "count": 8,
    "overduePayments": [
        {
            "id": 28,
            "paymentDate": "2025-07-15",
            "amount": 3500.00,
            "status": "OVERDUE",
            "dueDate": "2025-08-01",
            "overdueDays": 18,
            "customerName": "ABC Company",
            "description": "Invoice #INV-001 payment",
            "reminderCount": 2,
            "lastReminderSent": "2025-08-17"
        },
        {
            "id": 29,
            "paymentDate": "2025-07-20",
            "amount": 2200.00,
            "status": "OVERDUE", 
            "dueDate": "2025-08-05",
            "overdueDays": 14,
            "customerName": "XYZ Corp",
            "description": "Service payment - July",
            "reminderCount": 1,
            "lastReminderSent": "2025-08-15"
        }
    ]
}
```

### **3. Get All Disputed Payments**

#### **GET** `/api/v1/payments/disputed`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "count": 3,
    "disputedPayments": [
        {
            "id": 30,
            "paymentDate": "2025-08-10",
            "amount": 1800.00,
            "status": "DISPUTED",
            "disputeDate": "2025-08-18",
            "disputeReason": "Customer claims service was not delivered as agreed",
            "disputedByUsername": "support",
            "customerName": "Tech Solutions Ltd",
            "statusUpdatedAt": "2025-08-18T14:30:00",
            "statusNotes": "Payment disputed"
        },
        {
            "id": 31,
            "paymentDate": "2025-08-12",
            "amount": 950.00,
            "status": "DISPUTED",
            "disputeDate": "2025-08-19",
            "disputeReason": "Billing amount discrepancy",
            "disputedByUsername": "admin",
            "customerName": "StartupCo",
            "statusUpdatedAt": "2025-08-19T09:15:00",
            "statusNotes": "Payment disputed"
        }
    ]
}
```

### **4. Get Payment Status Summary**

#### **GET** `/api/v1/payments/status-summary`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "summary": {
        "totalPayments": 125,
        "problematicPayments": 28,
        "problematicPercentage": 22.4,
        "statusCounts": {
            "PENDING": 15,
            "PAID": 85,
            "OVERDUE": 18,
            "DISPUTED": 5,
            "PROCESSED": 2,
            "PARTIAL": 8,
            "CANCELLED": 3,
            "FAILED": 2,
            "REFUNDED": 1,
            "IN_COLLECTION": 3,
            "WRITTEN_OFF": 1
        }
    }
}
```

---

## 🔄 **All Available Payment Statuses**

### **GET** `/api/v1/payments/statuses`

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
            "description": "Payment recorded but not yet processed",
            "category": "WAITING",
            "canBeUpdated": true,
            "canBeDisputed": true,
            "canBePaid": true
        },
        {
            "name": "PAID",
            "displayName": "Paid",
            "description": "Payment has been successfully completed",
            "category": "COMPLETED",
            "canBeUpdated": false,
            "canBeDisputed": false,
            "canBePaid": false
        },
        {
            "name": "OVERDUE",
            "displayName": "Overdue",
            "description": "Payment is past due date",
            "category": "PROBLEMATIC",
            "canBeUpdated": true,
            "canBeDisputed": true,
            "canBePaid": true
        },
        {
            "name": "DISPUTED",
            "displayName": "Disputed",
            "description": "Payment is under dispute",
            "category": "PROBLEMATIC",
            "canBeUpdated": true,
            "canBeDisputed": false,
            "canBePaid": true
        },
        {
            "name": "PROCESSED",
            "displayName": "Processed",
            "description": "Payment has been successfully processed",
            "category": "COMPLETED",
            "canBeUpdated": false,
            "canBeDisputed": false,
            "canBePaid": false
        },
        {
            "name": "PARTIAL",
            "displayName": "Partial",
            "description": "Partial payment received",
            "category": "WAITING",
            "canBeUpdated": true,
            "canBeDisputed": true,
            "canBePaid": true
        },
        {
            "name": "CANCELLED",
            "displayName": "Cancelled",
            "description": "Payment has been cancelled",
            "category": "COMPLETED",
            "canBeUpdated": false,
            "canBeDisputed": false,
            "canBePaid": false
        },
        {
            "name": "FAILED",
            "displayName": "Failed",
            "description": "Payment processing failed",
            "category": "PROBLEMATIC",
            "canBeUpdated": false,
            "canBeDisputed": false,
            "canBePaid": false
        },
        {
            "name": "REFUNDED",
            "displayName": "Refunded",
            "description": "Payment has been refunded",
            "category": "COMPLETED",
            "canBeUpdated": false,
            "canBeDisputed": false,
            "canBePaid": false
        },
        {
            "name": "IN_COLLECTION",
            "displayName": "In Collection",
            "description": "Payment sent to collection agency",
            "category": "PROBLEMATIC",
            "canBeUpdated": false,
            "canBeDisputed": false,
            "canBePaid": false
        },
        {
            "name": "WRITTEN_OFF",
            "displayName": "Written Off",
            "description": "Payment written off as bad debt",
            "category": "COMPLETED",
            "canBeUpdated": false,
            "canBeDisputed": false,
            "canBePaid": false
        }
    ]
}
```

---

## 🚨 **Error Handling Examples**

### **1. Invalid Status Transition**

**Request:**
```json
PUT /api/v1/payments/25/status
{
    "status": "PAID",
    "statusNotes": "Marking as paid"
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "INVALID_STATUS_TRANSITION",
    "message": "Cannot update payment status from CANCELLED to PAID"
}
```

### **2. Cannot Dispute Completed Payment**

**Request:**
```json
POST /api/v1/payments/25/dispute
{
    "disputeReason": "Customer wants to dispute this payment"
}
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "ruleCode": "CANNOT_DISPUTE",
    "message": "Cannot dispute payment from status PAID"
}
```

### **3. Payment Not Currently Disputed**

**Request:**
```json
POST /api/v1/payments/25/resolve-dispute?resolutionNotes=Issue resolved
```

**Response (400 Bad Request):**
```json
{
    "success": false,
    "message": "Payment is not currently disputed"
}
```

---

## ⏰ **Automatic Overdue Detection**

The system includes an **automatic scheduled task** that runs daily at 9:00 AM to check for overdue payments:

### **How It Works:**
1. **Daily Schedule**: Runs every day at 9:00 AM automatically
2. **Detection Logic**: Checks all PENDING and PARTIAL payments for overdue status
3. **Automatic Update**: Marks payments as OVERDUE if past due date
4. **Audit Logging**: All automatic status changes are logged
5. **Days Calculation**: Automatically calculates and sets overdue days

### **What Gets Checked:**
- All payments with status `PENDING` or `PARTIAL`
- Payments with a `dueDate` set
- Payments where `dueDate` is before today's date

### **Automatic Actions:**
- Status changed from `PENDING`/`PARTIAL` to `OVERDUE`
- `overdueDays` field updated with calculated days
- `statusUpdatedAt` and `statusUpdatedBy` fields updated
- Complete audit log entry created

---

## 💡 **Payment Status Workflow Examples**

### **Scenario 1: Normal Payment Lifecycle**

1. **Create Payment:**
```bash
POST /api/v1/payments
# Status: PENDING
```

2. **Mark as Paid:**
```bash
PUT /api/v1/payments/25/status
{
    "status": "PAID",
    "statusNotes": "Bank transfer confirmed"
}
```

3. **Final Status:** `PAID` (completed)

### **Scenario 2: Overdue Payment Management**

1. **Payment Created with Due Date:**
```bash
POST /api/v1/payments
# Status: PENDING, dueDate: 2025-08-01
```

2. **Automatic Detection:** (Daily at 9 AM)
```bash
# System automatically marks as OVERDUE after due date
# Status: OVERDUE, overdueDays: 15
```

3. **Manual Update:**
```bash
PUT /api/v1/payments/26/status
{
    "status": "PAID",
    "statusNotes": "Late payment received"
}
```

### **Scenario 3: Dispute Resolution Workflow**

1. **Dispute Payment:**
```bash
POST /api/v1/payments/27/dispute
{
    "disputeReason": "Service quality issues"
}
# Status: DISPUTED
```

2. **Resolve Dispute:**
```bash
POST /api/v1/payments/27/resolve-dispute?resolutionNotes=Service corrected, customer satisfied
# Status: PENDING
```

3. **Final Payment:**
```bash
PUT /api/v1/payments/27/status
{
    "status": "PAID",
    "statusNotes": "Dispute resolved, payment processed"
}
```

---

## 🎯 **Key Features Summary**

### **✅ Status Management**
- **11 Payment Statuses**: Comprehensive coverage of all payment scenarios
- **Smart Transitions**: Business rule validation for status changes
- **Category Classification**: WAITING, COMPLETED, PROBLEMATIC categories
- **Permission Controls**: Different access levels for different operations

### **✅ Automated Operations**
- **Daily Overdue Detection**: Automatic checking and status updates
- **Business Rule Integration**: All operations validated against business rules
- **Complete Audit Trail**: Every status change logged and tracked
- **Scheduled Tasks**: Background processing for maintenance operations

### **✅ Dispute Management**
- **Full Dispute Lifecycle**: From initiation to resolution
- **Detailed Tracking**: Dispute reasons, dates, and responsible users
- **Resolution Workflow**: Structured process for dispute resolution
- **Audit Compliance**: Complete documentation of dispute history

### **✅ Advanced Reporting**
- **Status Summary Dashboard**: Real-time counts and percentages
- **Overdue Analysis**: Aging reports with days outstanding
- **Dispute Tracking**: Monitor all payments under dispute
- **Problem Identification**: Quick identification of problematic payments

**Your Payment Status Tracking System is production-ready!** 🎉

The system now provides:
- ✅ **Comprehensive status tracking** with 11 different statuses
- ✅ **Automatic overdue detection** with daily scheduled tasks
- ✅ **Full dispute management** with resolution workflow
- ✅ **Real-time reporting** and analytics
- ✅ **Complete audit trails** for compliance
- ✅ **Business rule integration** for data integrity

You can now track every aspect of payment lifecycle from creation to completion!
