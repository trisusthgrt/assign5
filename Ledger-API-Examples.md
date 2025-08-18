# 📊 Ledger Management API - Postman Examples

## Base URL
```
http://localhost:8080/api/v1/ledger
```

## 🔐 Authentication Required
**All endpoints require JWT token in Authorization header:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

---

## 1. 📝 **Create Ledger Entry (Credit/Debit)**

### **POST** `/api/v1/ledger/entries`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**JSON Body - Credit Entry:**
```json
{
    "customerId": 1,
    "transactionDate": "2025-08-19",
    "transactionType": "CREDIT",
    "amount": 15000.00,
    "description": "Payment received for invoice INV-001",
    "notes": "Bank transfer received on time",
    "referenceNumber": "REF-20250819-001",
    "invoiceNumber": "INV-001",
    "invoiceDate": "2025-08-15",
    "paymentMethod": "Bank Transfer"
}
```

**JSON Body - Debit Entry:**
```json
{
    "customerId": 1,
    "transactionDate": "2025-08-19",
    "transactionType": "DEBIT",
    "amount": 5000.00,
    "description": "Purchase of office supplies",
    "notes": "Monthly office supplies purchase",
    "referenceNumber": "REF-20250819-002",
    "invoiceNumber": "SUPP-001",
    "invoiceDate": "2025-08-19",
    "paymentMethod": "Credit Card"
}
```

**Expected Response (201 Created):**
```json
{
    "success": true,
    "message": "Ledger entry created successfully",
    "entry": {
        "id": 1,
        "transactionDate": "2025-08-19",
        "transactionType": "CREDIT",
        "amount": 15000.00,
        "description": "Payment received for invoice INV-001",
        "notes": "Bank transfer received on time",
        "referenceNumber": "REF-20250819-001",
        "invoiceNumber": "INV-001",
        "invoiceDate": "2025-08-15",
        "paymentMethod": "Bank Transfer",
        "balanceAfterTransaction": 15000.00,
        "isReconciled": false,
        "isActive": true,
        "customerId": 1,
        "customerName": "John Doe",
        "createdByUsername": "owner",
        "createdAt": "2025-08-19T01:30:00",
        "attachments": []
    }
}
```

---

## 2. 📎 **Create Ledger Entry with File Attachments**

### **POST** `/api/v1/ledger/entries/with-attachments`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: multipart/form-data
```

**Form Data:**
- **entry** (JSON):
```json
{
    "customerId": 1,
    "transactionDate": "2025-08-19",
    "transactionType": "DEBIT",
    "amount": 2500.00,
    "description": "Office rent payment",
    "notes": "Monthly office rent with receipt attached",
    "referenceNumber": "RENT-20250819",
    "paymentMethod": "Bank Transfer"
}
```
- **files** (File Array): Upload receipt images or PDFs
- **attachmentDescription** (Text): "Rent receipt and bank transfer confirmation"

**Expected Response (201 Created):**
```json
{
    "success": true,
    "message": "Ledger entry created successfully with attachments",
    "entry": {
        "id": 2,
        "transactionDate": "2025-08-19",
        "transactionType": "DEBIT",
        "amount": 2500.00,
        "description": "Office rent payment",
        "balanceAfterTransaction": 12500.00,
        "attachments": [
            {
                "id": 1,
                "fileName": "20250819_143000_a1b2c3d4.jpg",
                "originalFileName": "rent_receipt.jpg",
                "contentType": "image/jpeg",
                "fileSize": 245760,
                "description": "Rent receipt and bank transfer confirmation",
                "isActive": true,
                "uploadedAt": "2025-08-19T01:30:00",
                "uploadedByUsername": "owner",
                "downloadUrl": "/api/v1/files/download/1"
            }
        ]
    }
}
```

---

## 3. 📋 **Get Ledger Entry by ID**

### **GET** `/api/v1/ledger/entries/{id}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example:** `GET /api/v1/ledger/entries/1`

**Expected Response (200 OK):**
```json
{
    "success": true,
    "entry": {
        "id": 1,
        "transactionDate": "2025-08-19",
        "transactionType": "CREDIT",
        "amount": 15000.00,
        "description": "Payment received for invoice INV-001",
        "balanceAfterTransaction": 15000.00,
        "isReconciled": false,
        "isActive": true,
        "customerId": 1,
        "customerName": "John Doe",
        "attachments": []
    }
}
```

---

## 4. ✏️ **Update Ledger Entry**

### **PUT** `/api/v1/ledger/entries/{id}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: application/json
```

**JSON Body (Partial Update):**
```json
{
    "amount": 16000.00,
    "description": "Payment received for invoice INV-001 (Updated amount)",
    "notes": "Amount corrected after bank confirmation",
    "isReconciled": true,
    "reconciledDate": "2025-08-19"
}
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Ledger entry updated successfully",
    "entry": {
        "id": 1,
        "amount": 16000.00,
        "description": "Payment received for invoice INV-001 (Updated amount)",
        "notes": "Amount corrected after bank confirmation",
        "isReconciled": true,
        "reconciledDate": "2025-08-19",
        "balanceAfterTransaction": 16000.00,
        "updatedByUsername": "owner",
        "updatedAt": "2025-08-19T01:35:00"
    }
}
```

---

## 5. 🗑️ **Delete Ledger Entry** (Admin/Owner Only)

### **DELETE** `/api/v1/ledger/entries/{id}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Ledger entry deleted successfully"
}
```

---

## 6. 📊 **Get Customer Ledger Entries (Paginated)**

### **GET** `/api/v1/ledger/customer/{customerId}/entries?page=0&size=10&sortBy=transactionDate&sortDir=desc`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Query Parameters:**
- `page`: Page number (default: 0)
- `size`: Items per page (default: 10)
- `sortBy`: Sort field (default: transactionDate)
- `sortDir`: Sort direction (asc/desc, default: desc)

**Expected Response (200 OK):**
```json
{
    "success": true,
    "entries": [
        {
            "id": 2,
            "transactionDate": "2025-08-19",
            "transactionType": "DEBIT",
            "amount": 2500.00,
            "description": "Office rent payment",
            "balanceAfterTransaction": 13500.00,
            "customerId": 1,
            "customerName": "John Doe"
        },
        {
            "id": 1,
            "transactionDate": "2025-08-19",
            "transactionType": "CREDIT",
            "amount": 16000.00,
            "description": "Payment received for invoice INV-001",
            "balanceAfterTransaction": 16000.00,
            "customerId": 1,
            "customerName": "John Doe"
        }
    ],
    "totalElements": 2,
    "totalPages": 1,
    "currentPage": 0,
    "size": 10,
    "customerId": 1
}
```

---

## 7. 🔍 **Advanced Search Ledger Entries**

### **GET** `/api/v1/ledger/entries/search`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Query Parameters:**
- `customerId`: Filter by customer ID
- `transactionType`: CREDIT, DEBIT, OPENING_BALANCE, ADJUSTMENT, TRANSFER
- `startDate`: Start date (YYYY-MM-DD)
- `endDate`: End date (YYYY-MM-DD)
- `minAmount`: Minimum amount
- `maxAmount`: Maximum amount
- `description`: Search in description (partial match)
- `referenceNumber`: Search by reference number
- `invoiceNumber`: Search by invoice number
- `isReconciled`: true/false
- `page`, `size`, `sortBy`, `sortDir`: Pagination & sorting

**Example Query:**
```
/api/v1/ledger/entries/search?customerId=1&transactionType=CREDIT&startDate=2025-08-01&endDate=2025-08-31&minAmount=1000&isReconciled=false
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "entries": [
        {
            "id": 1,
            "transactionDate": "2025-08-19",
            "transactionType": "CREDIT",
            "amount": 16000.00,
            "description": "Payment received for invoice INV-001",
            "isReconciled": false,
            "customerId": 1,
            "customerName": "John Doe"
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "size": 10
}
```

---

## 8. 💰 **Get Customer Balance Summary**

### **GET** `/api/v1/ledger/customer/{customerId}/balance-summary`

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
        "totalCredit": 16000.00,
        "totalDebit": 2500.00,
        "currentBalance": 13500.00,
        "totalTransactions": 2,
        "creditLimit": 50000.00,
        "isOverCreditLimit": false
    }
}
```

---

## 9. ⚠️ **Get Unreconciled Entries** (Admin/Owner Only)

### **GET** `/api/v1/ledger/customer/{customerId}/unreconciled`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "entries": [
        {
            "id": 2,
            "transactionDate": "2025-08-19",
            "transactionType": "DEBIT",
            "amount": 2500.00,
            "description": "Office rent payment",
            "isReconciled": false,
            "customerId": 1,
            "customerName": "John Doe"
        }
    ],
    "count": 1,
    "customerId": 1
}
```

---

## 10. 📎 **Add Attachment to Existing Entry**

### **POST** `/api/v1/ledger/entries/{entryId}/attachments`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
Content-Type: multipart/form-data
```

**Form Data:**
- **file** (File): Upload image or document
- **description** (Text): "Additional receipt for payment verification"

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Attachment added successfully",
    "entry": {
        "id": 1,
        "attachments": [
            {
                "id": 2,
                "fileName": "20250819_144500_b2c3d4e5.pdf",
                "originalFileName": "additional_receipt.pdf",
                "contentType": "application/pdf",
                "fileSize": 156789,
                "description": "Additional receipt for payment verification",
                "downloadUrl": "/api/v1/files/download/2"
            }
        ]
    }
}
```

---

## 11. 🗑️ **Remove Attachment from Entry**

### **DELETE** `/api/v1/ledger/entries/{entryId}/attachments/{attachmentId}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "message": "Attachment removed successfully"
}
```

---

## 12. 📥 **Download File Attachment**

### **GET** `/api/v1/files/download/{attachmentId}`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Response:** File download with appropriate headers

---

## 13. 📋 **Get Transaction Types**

### **GET** `/api/v1/ledger/transaction-types`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "transactionTypes": [
        "CREDIT",
        "DEBIT",
        "OPENING_BALANCE",
        "ADJUSTMENT",
        "TRANSFER"
    ]
}
```

---

## 14. 📅 **Get Entries by Date Range**

### **GET** `/api/v1/ledger/entries/date-range?startDate=2025-08-01&endDate=2025-08-31&customerId=1`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Query Parameters:**
- `startDate`: Start date (required, YYYY-MM-DD)
- `endDate`: End date (required, YYYY-MM-DD)
- `customerId`: Customer ID (optional)
- `page`, `size`, `sortBy`, `sortDir`: Pagination & sorting

**Expected Response (200 OK):**
```json
{
    "success": true,
    "entries": [
        {
            "id": 1,
            "transactionDate": "2025-08-19",
            "transactionType": "CREDIT",
            "amount": 16000.00,
            "description": "Payment received for invoice INV-001"
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "size": 10,
    "startDate": "2025-08-01",
    "endDate": "2025-08-31"
}
```

---

## 15. 📊 **Get File Storage Statistics** (Admin/Owner Only)

### **GET** `/api/v1/files/stats`

**Headers:**
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Expected Response (200 OK):**
```json
{
    "success": true,
    "stats": {
        "totalFiles": 5,
        "totalSizeBytes": 1248576,
        "formattedTotalSize": "1.2 MB",
        "imageFiles": 3,
        "documentFiles": 2
    }
}
```

---

## 🎯 **Sample Test Data**

### **Opening Balance Entry:**
```json
{
    "customerId": 1,
    "transactionDate": "2025-08-01",
    "transactionType": "OPENING_BALANCE",
    "amount": 10000.00,
    "description": "Opening balance for August 2025",
    "notes": "Starting balance for new accounting period"
}
```

### **Adjustment Entry:**
```json
{
    "customerId": 1,
    "transactionDate": "2025-08-20",
    "transactionType": "ADJUSTMENT",
    "amount": 100.00,
    "description": "Manual adjustment for calculation error",
    "notes": "Correcting previous entry calculation mistake",
    "referenceNumber": "ADJ-20250820-001"
}
```

### **Transfer Entry:**
```json
{
    "customerId": 1,
    "transactionDate": "2025-08-21",
    "transactionType": "TRANSFER",
    "amount": 5000.00,
    "description": "Transfer to savings account",
    "notes": "Moving funds to high-yield savings",
    "paymentMethod": "Online Transfer"
}
```

---

## 📋 **File Upload Guidelines**

### **Supported File Types:**
- **Images**: JPEG, JPG, PNG, GIF, BMP, WEBP
- **Documents**: PDF, TXT, DOC, DOCX, XLS, XLSX

### **File Size Limits:**
- Maximum file size: **10MB**
- Maximum request size: **10MB**

### **Best Practices:**
1. **Image Receipts**: Use JPEG/PNG for bill photos and receipts
2. **Invoices**: Upload PDF copies of invoices and contracts
3. **Descriptions**: Always provide meaningful descriptions for attachments
4. **File Names**: Use descriptive original file names

---

## ⚠️ **Important Notes**

1. **Authentication**: All endpoints require valid JWT token
2. **Permissions**: 
   - STAFF, OWNER, ADMIN can create/read/update entries
   - Only OWNER, ADMIN can delete entries and view unreconciled entries
   - Only ADMIN, OWNER can view file storage statistics
3. **Balance Calculation**: Automatic balance calculation after each transaction
4. **Soft Delete**: Deleting entries marks them as inactive, doesn't remove from database
5. **File Security**: Uploaded files are stored securely and access-controlled
6. **Search Performance**: Use date ranges and customer filters for better performance

---

## 🚀 **Ready to Test!**

Your Ledger Management system is fully functional with:
- ✅ Complete CRUD operations for credit/debit entries
- ✅ File attachment support with image uploads
- ✅ Advanced search and filtering capabilities
- ✅ Automatic balance calculations
- ✅ Role-based access control
- ✅ Comprehensive audit trails
- ✅ Reconciliation management

**Start by creating customers, then add ledger entries with attachments!** 🎉
