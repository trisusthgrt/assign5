# 📊 Export API Documentation

## Overview
The Export API provides functionality for **STAFF users** to generate and download customer statements and transaction history in both **PDF** and **CSV** formats. This feature ensures proper role-based access control where staff can only export data for customers in their assigned shop.

---

## 🔐 **Access Control**
- **STAFF**: Can export statements for customers in their assigned shop only
- **OWNER**: Can export statements for all customers in their shops
- **ADMIN**: Can export statements for all customers across all shops

---

## 📋 **Available Export Types**

### 1. **Customer Statement** (Date Range Specific)
- **PDF Format**: Professional formatted statement with tables and summaries
- **CSV Format**: Spreadsheet-friendly data for analysis and reporting

### 2. **Complete Transaction History**
- **PDF Format**: Full transaction history with detailed formatting
- **CSV Format**: Complete transaction data for external analysis

### 3. **Current Month Statement**
- **PDF Format**: Statement for the current month (1st to current date)
- **CSV Format**: Current month data in spreadsheet format

---

## 🚀 **API Endpoints**

### **Base URL**: `/api/v1/export`

---

## 📄 **1. Export Customer Statement as PDF**

### **GET** `/customers/{customerId}/statement/pdf`

**Description**: Generate a PDF statement for a customer within a specified date range.

**Parameters**:
- `customerId` (path): Customer ID (required)
- `startDate` (query): Start date in YYYY-MM-DD format (required)
- `endDate` (query): End date in YYYY-MM-DD format (required)

**Headers**:
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example Request**:
```
GET /api/v1/export/customers/1/statement/pdf?startDate=2025-08-01&endDate=2025-08-31
```

**Response**:
- **Content-Type**: `application/pdf`
- **Content-Disposition**: `attachment; filename="customer_statement_1_2025-08-01_to_2025-08-31.pdf"`
- **Body**: PDF file content

**PDF Content Includes**:
- Customer information (name, email, phone, business details)
- Current balance and credit limit
- Transaction summary for the period
- Detailed transaction table with:
  - Date, Type, Description, Reference, Invoice, Amount, Balance After, Reconciled status

---

## 📊 **2. Export Customer Statement as CSV**

### **GET** `/customers/{customerId}/statement/csv`

**Description**: Generate a CSV statement for a customer within a specified date range.

**Parameters**:
- `customerId` (path): Customer ID (required)
- `startDate` (query): Start date in YYYY-MM-DD format (required)
- `endDate` (query): End date in YYYY-MM-DD format (required)

**Headers**:
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example Request**:
```
GET /api/v1/export/customers/1/statement/csv?startDate=2025-08-01&endDate=2025-08-31
```

**Response**:
- **Content-Type**: `text/csv`
- **Content-Disposition**: `attachment; filename="customer_statement_1_2025-08-01_to_2025-08-31.csv"`
- **Body**: CSV file content

**CSV Content Includes**:
- Header with customer name and period
- Customer information section
- Transaction summary section
- Detailed transaction data with columns:
  - Date, Type, Description, Reference, Invoice, Amount, Balance After

---

## 📄 **3. Export Complete Transaction History as PDF**

### **GET** `/customers/{customerId}/history/pdf`

**Description**: Generate a PDF containing the complete transaction history for a customer.

**Parameters**:
- `customerId` (path): Customer ID (required)

**Headers**:
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example Request**:
```
GET /api/v1/export/customers/1/history/pdf
```

**Response**:
- **Content-Type**: `application/pdf`
- **Content-Disposition**: `attachment; filename="customer_transaction_history_1.pdf"`
- **Body**: PDF file content

**PDF Content Includes**:
- Complete transaction history header
- Customer information and current balance
- All transactions in a detailed table format
- Reconciliation status for each transaction

---

## 📊 **4. Export Complete Transaction History as CSV**

### **GET** `/customers/{customerId}/history/csv`

**Description**: Generate a CSV containing the complete transaction history for a customer.

**Parameters**:
- `customerId` (path): Customer ID (required)

**Headers**:
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example Request**:
```
GET /api/v1/export/customers/1/history/csv
```

**Response**:
- **Content-Type**: `text/csv`
- **Content-Disposition**: `attachment; filename="customer_transaction_history_1.csv"`
- **Body**: CSV file content

**CSV Content Includes**:
- Complete transaction history header
- Customer information section
- All transactions with columns:
  - Date, Type, Description, Reference, Invoice, Amount, Balance After, Reconciled

---

## 📅 **5. Export Current Month Statement as PDF**

### **GET** `/customers/{customerId}/statement/current-month/pdf`

**Description**: Generate a PDF statement for a customer for the current month (1st to current date).

**Parameters**:
- `customerId` (path): Customer ID (required)

**Headers**:
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example Request**:
```
GET /api/v1/export/customers/1/statement/current-month/pdf
```

**Response**:
- **Content-Type**: `application/pdf`
- **Content-Disposition**: `attachment; filename="customer_statement_1_current_month.pdf"`
- **Body**: PDF file content

---

## 📊 **6. Export Current Month Statement as CSV**

### **GET** `/customers/{customerId}/statement/current-month/csv`

**Description**: Generate a CSV statement for a customer for the current month (1st to current date).

**Parameters**:
- `customerId` (path): Customer ID (required)

**Headers**:
```
Authorization: Bearer [YOUR_JWT_TOKEN]
```

**Example Request**:
```
GET /api/v1/export/customers/1/statement/current-month/csv
```

**Response**:
- **Content-Type**: `text/csv`
- **Content-Disposition**: `attachment; filename="customer_statement_1_current_month.csv"`
- **Body**: CSV file content

---

## 🔒 **Security & Access Control**

### **Role-Based Access**:
- **STAFF**: Limited to customers in their assigned shop
- **OWNER**: Access to all customers in their shops
- **ADMIN**: Access to all customers across all shops

### **Validation**:
- Customer existence verification
- Shop access validation for staff users
- Date range validation
- Authentication token validation

---

## 📝 **Error Handling**

### **Common Error Responses**:

#### **400 Bad Request**:
```json
{
    "error": "Invalid date format",
    "message": "Start date must be in YYYY-MM-DD format"
}
```

#### **403 Forbidden**:
```json
{
    "error": "Access Denied",
    "message": "Customer not in your assigned shop"
}
```

#### **404 Not Found**:
```json
{
    "error": "Customer Not Found",
    "message": "Customer with ID 999 does not exist"
}
```

#### **500 Internal Server Error**:
```json
{
    "error": "Export Generation Failed",
    "message": "Failed to generate PDF: Database connection error"
}
```

---

## 🎯 **Use Cases**

### **For STAFF Users**:
1. **Monthly Statements**: Generate monthly statements for customers
2. **Periodic Reports**: Create statements for specific date ranges
3. **Transaction History**: Export complete transaction data for analysis
4. **Customer Communication**: Send statements to customers via email

### **For OWNER Users**:
1. **Business Analysis**: Analyze customer transaction patterns
2. **Financial Reporting**: Generate reports for accounting purposes
3. **Customer Management**: Review customer financial status

### **For ADMIN Users**:
1. **System Monitoring**: Monitor export usage across all shops
2. **Audit Purposes**: Generate reports for compliance
3. **Business Intelligence**: Analyze data across all locations

---

## 📱 **Frontend Integration**

### **Download Links**:
```html
<!-- PDF Statement -->
<a href="/api/v1/export/customers/1/statement/pdf?startDate=2025-08-01&endDate=2025-08-31" 
   class="btn btn-primary">
   Download PDF Statement
</a>

<!-- CSV Statement -->
<a href="/api/v1/export/customers/1/statement/csv?startDate=2025-08-01&endDate=2025-08-31" 
   class="btn btn-secondary">
   Download CSV Statement
</a>

<!-- Current Month PDF -->
<a href="/api/v1/export/customers/1/statement/current-month/pdf" 
   class="btn btn-success">
   Current Month PDF
</a>
```

### **JavaScript Download**:
```javascript
function downloadStatement(customerId, startDate, endDate, format) {
    const url = `/api/v1/export/customers/${customerId}/statement/${format}?startDate=${startDate}&endDate=${endDate}`;
    
    fetch(url, {
        headers: {
            'Authorization': `Bearer ${getAuthToken()}`
        }
    })
    .then(response => {
        if (response.ok) {
            return response.blob();
        }
        throw new Error('Export failed');
    })
    .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `customer_statement_${customerId}_${startDate}_to_${endDate}.${format}`;
        a.click();
        window.URL.revokeObjectURL(url);
    })
    .catch(error => {
        console.error('Export error:', error);
        alert('Failed to download statement');
    });
}
```

---

## 🚀 **Performance Considerations**

### **Large Data Sets**:
- PDF generation optimized for up to 1000 transactions
- CSV generation handles unlimited transaction counts
- Memory-efficient processing for large exports

### **Caching**:
- Generated files are not cached (fresh data each time)
- Consider implementing caching for frequently requested exports

### **File Sizes**:
- PDF files: Typically 50KB - 2MB depending on transaction count
- CSV files: Typically 10KB - 500KB depending on transaction count

---

## 🔧 **Technical Implementation**

### **Backend Services**:
- `ExportService`: Core export logic and PDF/CSV generation
- `LedgerService`: Data retrieval for transactions
- `CustomerService`: Customer information and validation

### **Dependencies**:
- **iText7**: PDF generation (kernel, layout, io modules)
- **OpenCSV**: CSV generation and formatting
- **Spring Security**: Role-based access control

### **File Generation**:
- **PDF**: Professional formatting with tables, headers, and summaries
- **CSV**: Standard CSV format compatible with Excel, Google Sheets, etc.

---

## 📋 **Testing Examples**

### **Test Customer Statement Export**:
```bash
# PDF Statement
curl -X GET "http://localhost:8080/api/v1/export/customers/1/statement/pdf?startDate=2025-08-01&endDate=2025-08-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Accept: application/pdf" \
  --output statement.pdf

# CSV Statement
curl -X GET "http://localhost:8080/api/v1/export/customers/1/statement/csv?startDate=2025-08-01&endDate=2025-08-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Accept: text/csv" \
  --output statement.csv
```

### **Test Transaction History Export**:
```bash
# PDF History
curl -X GET "http://localhost:8080/api/v1/export/customers/1/history/pdf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Accept: application/pdf" \
  --output history.pdf

# CSV History
curl -X GET "http://localhost:8080/api/v1/export/customers/1/history/csv" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Accept: text/csv" \
  --output history.csv
```

---

## 🎉 **Summary**

The Export API provides **comprehensive export functionality** for customer statements and transaction history, ensuring:

✅ **Role-based access control** for security  
✅ **Multiple formats** (PDF/CSV) for flexibility  
✅ **Date range filtering** for specific periods  
✅ **Professional formatting** for business use  
✅ **Shop-based isolation** for multi-tenant security  
✅ **Comprehensive error handling** for reliability  

This implementation allows **STAFF users** to efficiently generate and share customer financial information while maintaining proper data security and access controls.
