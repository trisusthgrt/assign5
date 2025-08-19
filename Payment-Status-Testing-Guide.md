# Payment Status Tracking Testing Guide

This guide provides comprehensive methods to test whether your Ledgerly application's payment status tracking functionality is working correctly for all statuses: **Pending**, **Paid**, **Overdue**, and **Disputed**.

## 🎯 What We're Testing

### Payment Statuses to Verify:
- ✅ **Pending** - Payment recorded but not yet processed
- ✅ **Paid** - Payment successfully completed
- ⚠️ **Overdue** - Payment past due date
- ❌ **Disputed** - Payment under dispute

### Core Functionality to Test:
- Payment creation with different statuses
- Status transitions (e.g., Pending → Paid)
- Automatic overdue detection
- Dispute management
- Payment status updates
- Status-based filtering and reporting

---

## 🚀 Quick Test Commands

### 1. Test Health Check First
```bash
# Ensure application is running
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/health" -UseBasicParsing
```

### 2. Test Payment Status Endpoints
```bash
# Get all payments with their statuses
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments" -Headers @{"Authorization"="Bearer $token"} -UseBasicParsing

# Get payments by specific status
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status/PENDING" -Headers @{"Authorization"="Bearer $token"} -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status/PAID" -Headers @{"Authorization"="Bearer $token"} -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status/OVERDUE" -Headers @{"Authorization"="Bearer $token"} -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status/DISPUTED" -Headers @{"Authorization"="Bearer $token"} -UseBasicParsing
```

---

## 📋 Step-by-Step Testing Process

### Step 1: Authentication Setup
```bash
# 1. Register a test user (if not exists)
$registerBody = @{
    username = "testuser"
    email = "testuser@example.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
    role = "STAFF"
} | ConvertTo-Json

$registerResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/register" -Method POST -Headers @{"Content-Type"="application/json"} -Body $registerBody -UseBasicParsing

# 2. Login to get JWT token
$loginBody = @{
    usernameOrEmail = "testuser"
    password = "password123"
} | ConvertTo-Json

$loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/login" -Method POST -Headers @{"Content-Type"="application/json"} -Body $loginBody -UseBasicParsing

# 3. Extract JWT token
$token = ($loginResponse.Content | ConvertFrom-Json).token
$headers = @{"Authorization"="Bearer $token"; "Content-Type"="application/json"}
```

### Step 2: Create Test Customer
```bash
# Create a customer for testing payments
$customerBody = @{
    name = "Test Customer"
    email = "customer@test.com"
    phone = "+1234567890"
    address = "123 Test Street"
    relationshipType = "REGULAR"
    notes = "Test customer for payment status testing"
} | ConvertTo-Json

$customerResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/customers" -Method POST -Headers $headers -Body $customerBody -UseBasicParsing
$customerId = ($customerResponse.Content | ConvertFrom-Json).id
```

### Step 3: Test Payment Status Creation

#### 3.1 Create Pending Payment
```bash
$pendingPaymentBody = @{
    customerId = $customerId
    amount = 1000.00
    paymentDate = (Get-Date).ToString("yyyy-MM-dd")
    paymentMethod = "BANK_TRANSFER"
    description = "Test pending payment"
    dueDate = (Get-Date).AddDays(30).ToString("yyyy-MM-dd")
    status = "PENDING"
} | ConvertTo-Json

$pendingResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments" -Method POST -Headers $headers -Body $pendingPaymentBody -UseBasicParsing
$pendingPaymentId = ($pendingResponse.Content | ConvertFrom-Json).id

Write-Host "Created Pending Payment with ID: $pendingPaymentId"
```

#### 3.2 Create Overdue Payment
```bash
$overduePaymentBody = @{
    customerId = $customerId
    amount = 500.00
    paymentDate = (Get-Date).AddDays(-10).ToString("yyyy-MM-dd")
    paymentMethod = "CHECK"
    description = "Test overdue payment"
    dueDate = (Get-Date).AddDays(-5).ToString("yyyy-MM-dd")
    status = "PENDING"
} | ConvertTo-Json

$overdueResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments" -Method POST -Headers $headers -Body $overduePaymentBody -UseBasicParsing
$overduePaymentId = ($overdueResponse.Content | ConvertFrom-Json).id

Write-Host "Created Overdue Payment with ID: $overduePaymentId"
```

### Step 4: Test Status Transitions

#### 4.1 Mark Payment as Paid
```bash
$paidStatusBody = @{
    status = "PAID"
    statusNotes = "Payment received via bank transfer"
} | ConvertTo-Json

$paidResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$pendingPaymentId/status" -Method PUT -Headers $headers -Body $paidStatusBody -UseBasicParsing
Write-Host "Updated payment status to PAID: $($paidResponse.StatusCode)"
```

#### 4.2 Mark Payment as Disputed
```bash
$disputeBody = @{
    disputeReason = "Customer claims payment was already made"
} | ConvertTo-Json

$disputeResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$overduePaymentId/dispute" -Method POST -Headers $headers -Body $disputeBody -UseBasicParsing
Write-Host "Marked payment as DISPUTED: $($disputeResponse.StatusCode)"
```

### Step 5: Verify Status Changes

#### 5.1 Check Individual Payment Status
```bash
# Check pending payment (should now be PAID)
$paidPayment = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$pendingPaymentId" -Headers $headers -UseBasicParsing
Write-Host "Payment Status: $($paidPayment.Content | ConvertFrom-Json | Select-Object -ExpandProperty status)"

# Check overdue payment (should now be DISPUTED)
$disputedPayment = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$overduePaymentId" -Headers $headers -UseBasicParsing
Write-Host "Payment Status: $($disputedPayment.Content | ConvertFrom-Json | Select-Object -ExpandProperty status)"
```

#### 5.2 Check Status-Based Filtering
```bash
# Get all PAID payments
$paidPayments = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status/PAID" -Headers $headers -UseBasicParsing
Write-Host "PAID Payments Count: $($paidPayments.Content | ConvertFrom-Json | Measure-Object).Count"

# Get all DISPUTED payments
$disputedPayments = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status/DISPUTED" -Headers $headers -UseBasicParsing
Write-Host "DISPUTED Payments Count: $($disputedPayments.Content | ConvertFrom-Json | Measure-Object).Count"

# Get all OVERDUE payments
$overduePayments = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status/OVERDUE" -Headers $headers -UseBasicParsing
Write-Host "OVERDUE Payments Count: $($overduePayments.Content | ConvertFrom-Json | Measure-Object).Count"
```

---

## 🔍 Detailed Testing Scenarios

### Scenario 1: Pending Payment Flow
```bash
# Test creating a payment with PENDING status
Write-Host "=== Testing PENDING Payment Flow ==="

$pendingTestBody = @{
    customerId = $customerId
    amount = 750.00
    paymentDate = (Get-Date).ToString("yyyy-MM-dd")
    paymentMethod = "CASH"
    description = "Test pending payment flow"
    dueDate = (Get-Date).AddDays(15).ToString("yyyy-MM-dd")
    status = "PENDING"
} | ConvertTo-Json

$pendingTestResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments" -Method POST -Headers $headers -Body $pendingTestBody -UseBasicParsing

if ($pendingTestResponse.StatusCode -eq 200) {
    Write-Host "✅ PENDING payment created successfully"
    $pendingTestId = ($pendingTestResponse.Content | ConvertFrom-Json).id
    
    # Verify status is PENDING
    $verifyPending = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$pendingTestId" -Headers $headers -UseBasicParsing
    $status = ($verifyPending.Content | ConvertFrom-Json).status
    Write-Host "Status verification: $status"
} else {
    Write-Host "❌ Failed to create PENDING payment"
}
```

### Scenario 2: Overdue Payment Detection
```bash
# Test automatic overdue detection
Write-Host "=== Testing Overdue Payment Detection ==="

# Create a payment that's already overdue
$overdueTestBody = @{
    customerId = $customerId
    amount = 300.00
    paymentDate = (Get-Date).AddDays(-20).ToString("yyyy-MM-dd")
    paymentMethod = "CREDIT_CARD"
    description = "Test overdue detection"
    dueDate = (Get-Date).AddDays(-15).ToString("yyyy-MM-dd")
    status = "PENDING"
} | ConvertTo-Json

$overdueTestResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments" -Method POST -Headers $headers -Body $overdueTestBody -UseBasicParsing

if ($overdueTestResponse.StatusCode -eq 200) {
    Write-Host "✅ Overdue payment created successfully"
    $overdueTestId = ($overdueTestResponse.Content | ConvertFrom-Json).id
    
    # Wait for scheduled task to run or manually trigger overdue check
    Start-Sleep -Seconds 5
    
    # Check if status automatically changed to OVERDUE
    $verifyOverdue = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$overdueTestId" -Headers $headers -UseBasicParsing
    $status = ($verifyOverdue.Content | ConvertFrom-Json).status
    Write-Host "Status after overdue check: $status"
} else {
    Write-Host "❌ Failed to create overdue payment"
}
```

### Scenario 3: Dispute Management
```bash
# Test dispute creation and resolution
Write-Host "=== Testing Dispute Management ==="

# Create a payment to dispute
$disputeTestBody = @{
    customerId = $customerId
    amount = 450.00
    paymentDate = (Get-Date).ToString("yyyy-MM-dd")
    paymentMethod = "BANK_TRANSFER"
    description = "Test dispute management"
    dueDate = (Get-Date).AddDays(10).ToString("yyyy-MM-dd")
    status = "PENDING"
} | ConvertTo-Json

$disputeTestResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments" -Method POST -Headers $headers -Body $disputeTestBody -UseBasicParsing

if ($disputeTestResponse.StatusCode -eq 200) {
    Write-Host "✅ Payment created for dispute testing"
    $disputeTestId = ($disputeTestResponse.Content | ConvertFrom-Json).id
    
    # Mark as disputed
    $disputeReason = @{
        disputeReason = "Customer disputes the amount charged"
    } | ConvertTo-Json
    
    $disputeResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$disputeTestId/dispute" -Method POST -Headers $headers -Body $disputeReason -UseBasicParsing
    
    if ($disputeResponse.StatusCode -eq 200) {
        Write-Host "✅ Payment marked as disputed"
        
        # Verify dispute status
        $verifyDispute = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$disputeTestId" -Headers $headers -UseBasicParsing
        $disputeData = $verifyDispute.Content | ConvertFrom-Json
        Write-Host "Dispute Status: $($disputeData.status)"
        Write-Host "Dispute Reason: $($disputeData.disputeReason)"
        Write-Host "Dispute Date: $($disputeData.disputeDate)"
    } else {
        Write-Host "❌ Failed to mark payment as disputed"
    }
} else {
    Write-Host "❌ Failed to create payment for dispute testing"
}
```

### Scenario 4: Status Summary and Reporting
```bash
# Test payment status summary
Write-Host "=== Testing Payment Status Summary ==="

$summaryResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/status-summary" -Headers $headers -UseBasicParsing

if ($summaryResponse.StatusCode -eq 200) {
    $summary = $summaryResponse.Content | ConvertFrom-Json
    Write-Host "Payment Status Summary:"
    Write-Host "  PENDING: $($summary.pendingCount)"
    Write-Host "  PAID: $($summary.paidCount)"
    Write-Host "  OVERDUE: $($summary.overdueCount)"
    Write-Host "  DISPUTED: $($summary.disputedCount)"
    Write-Host "  Total: $($summary.totalCount)"
} else {
    Write-Host "❌ Failed to get payment status summary"
}
```

---

## 🧪 Automated Testing Script

### Complete Test Script
```bash
# Complete payment status testing script
Write-Host "Starting Payment Status Testing..." -ForegroundColor Green

# 1. Setup authentication
Write-Host "Setting up authentication..." -ForegroundColor Yellow
# [Include authentication code from Step 1]

# 2. Create test customer
Write-Host "Creating test customer..." -ForegroundColor Yellow
# [Include customer creation code from Step 2]

# 3. Test all payment statuses
Write-Host "Testing payment statuses..." -ForegroundColor Yellow

# Test PENDING
Write-Host "Testing PENDING status..." -ForegroundColor Cyan
# [Include pending test code]

# Test PAID
Write-Host "Testing PAID status..." -ForegroundColor Cyan
# [Include paid test code]

# Test OVERDUE
Write-Host "Testing OVERDUE status..." -ForegroundColor Cyan
# [Include overdue test code]

# Test DISPUTED
Write-Host "Testing DISPUTED status..." -ForegroundColor Cyan
# [Include dispute test code]

# 4. Verify all statuses
Write-Host "Verifying all statuses..." -ForegroundColor Yellow
# [Include verification code]

Write-Host "Payment Status Testing Complete!" -ForegroundColor Green
```

---

## 📊 Expected Results

### Successful Test Outcomes:
- ✅ **PENDING**: Payment created with PENDING status, can be updated to other statuses
- ✅ **PAID**: Payment status successfully changed to PAID, includes payment details
- ✅ **OVERDUE**: Payment automatically detected as overdue, status updated accordingly
- ✅ **DISPUTED**: Payment marked as disputed with reason and timestamp
- ✅ **Status Transitions**: All status changes work correctly
- ✅ **Filtering**: Status-based queries return correct results
- ✅ **Summary**: Status summary shows accurate counts

### Common Issues to Watch For:
- ❌ **Status not updating**: Check if status update endpoint is working
- ❌ **Overdue not detected**: Verify scheduled task is running
- ❌ **Dispute not working**: Check dispute endpoint implementation
- ❌ **Filtering issues**: Verify status enum values match database
- ❌ **Authentication errors**: Ensure JWT token is valid

---

## 🔧 Troubleshooting

### If Status Updates Fail:
```bash
# Check if payment exists
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/payments/$paymentId" -Headers $headers -UseBasicParsing

# Check application logs for errors
# Look for validation errors or business rule violations
```

### If Overdue Detection Fails:
```bash
# Check if scheduled task is enabled
# Verify @EnableScheduling annotation in main class
# Check cron expression in @Scheduled annotation
# Look for errors in scheduled task execution
```

### If Dispute Creation Fails:
```bash
# Verify dispute endpoint is accessible
# Check if dispute reason is provided
# Verify business rules allow dispute creation
# Check database constraints
```

---

## 📝 Testing Checklist

### Before Testing:
- [ ] Application is running and healthy
- [ ] Database is accessible
- [ ] Authentication is working
- [ ] Test customer exists
- [ ] JWT token is valid

### During Testing:
- [ ] Create payments with different statuses
- [ ] Test status transitions
- [ ] Verify overdue detection
- [ ] Test dispute management
- [ ] Check status filtering
- [ ] Verify status summary

### After Testing:
- [ ] All statuses work correctly
- [ ] Status transitions are successful
- [ ] Overdue detection is automatic
- [ ] Dispute management is functional
- [ ] Status reporting is accurate
- [ ] No errors in application logs

---

## 🎯 Success Criteria

Your payment status tracking is working correctly if:

1. **All Statuses Supported**: PENDING, PAID, OVERDUE, DISPUTED are all functional
2. **Status Transitions Work**: Can change from PENDING to any other status
3. **Overdue Detection**: Automatically marks payments as overdue when past due date
4. **Dispute Management**: Can mark payments as disputed with reasons
5. **Status Filtering**: Can query payments by specific status
6. **Status Summary**: Provides accurate counts for each status
7. **Data Integrity**: Status changes are properly persisted and retrieved
8. **Business Rules**: Enforces proper status transition rules
9. **Audit Trail**: Tracks status changes with timestamps and user information
10. **API Consistency**: All endpoints return consistent status information

---

## 🚀 Next Steps

After successful testing:

1. **Integration Testing**: Test with frontend application
2. **Performance Testing**: Test with large numbers of payments
3. **Edge Cases**: Test boundary conditions and error scenarios
4. **User Acceptance**: Have business users verify functionality
5. **Documentation**: Update API documentation with examples
6. **Monitoring**: Set up alerts for failed status updates

---

## 📞 Support

If you encounter issues during testing:

1. **Check Application Logs**: Look for error messages and stack traces
2. **Verify Database**: Check if data is being saved correctly
3. **Test Endpoints Individually**: Isolate which specific functionality is failing
4. **Check Business Rules**: Verify if any validation is blocking operations
5. **Review Code**: Ensure all required methods are implemented

Remember: Payment status tracking is critical for business operations, so thorough testing is essential!
