# 🚀 Ledgerly API - Postman Test Examples

## Base URL
```
http://localhost:8080
```

---

## 1. 📝 **User Registration**

### **POST** `/api/v1/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "john_owner",
    "email": "john@business.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "OWNER",
    "businessName": "John's Hardware Store",
    "businessAddress": "123 Main Street, City, State 12345",
    "phoneNumber": "+1-234-567-8900"
}
```

**Expected Response (201 Created):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "john_owner",
    "email": "john@business.com",
    "role": "OWNER",
    "message": "User registered successfully"
}
```

---

## 2. 🔐 **User Login**

### **POST** `/api/v1/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
    "username": "john_owner",
    "password": "password123"
}
```

**Alternative Login (with email):**
```json
{
    "username": "john@business.com",
    "password": "password123"
}
```

**Expected Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX293bmVyIiwiaWF0IjoxNjk5...",
    "username": "john_owner",
    "email": "john@business.com",
    "role": "OWNER",
    "message": "Login successful"
}
```

---

## 3. 👤 **Get Current User Info**

### **GET** `/api/v1/auth/me`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX293bmVyIiwiaWF0IjoxNjk5...
Content-Type: application/json
```

**Expected Response (200 OK):**
```json
{
    "id": 1,
    "username": "john_owner",
    "email": "john@business.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "OWNER",
    "phoneNumber": "+1-234-567-8900",
    "businessName": "John's Hardware Store",
    "businessAddress": "123 Main Street, City, State 12345",
    "isActive": true,
    "isEmailVerified": false,
    "isPhoneVerified": false,
    "createdAt": "2025-08-19T00:08:14.833",
    "lastLogin": null
}
```

---

## 4. 🏥 **Health Check** (No Authentication Required)

### **GET** `/api/v1/health`

**Headers:**
```
Content-Type: application/json
```

**Expected Response (200 OK):**
```json
{
    "application": "Ledgerly",
    "message": "Ledgerly application is running successfully",
    "version": "0.0.1-SNAPSHOT",
    "status": "UP",
    "timestamp": "2025-08-19T00:07:38.8501127"
}
```

---

## 📋 **Step-by-Step Testing Instructions**

### **Step 1: Test Health Check**
1. Create new request in Postman
2. Set method to `GET`
3. URL: `http://localhost:8080/api/v1/health`
4. Click **Send**
5. Should return status `UP`

### **Step 2: Register a User**
1. Create new request in Postman
2. Set method to `POST`
3. URL: `http://localhost:8080/api/v1/auth/register`
4. Go to **Headers** tab, add: `Content-Type: application/json`
5. Go to **Body** tab, select **raw** and **JSON**
6. Paste the registration JSON above
7. Click **Send**
8. **Save the token** from response!

### **Step 3: Login**
1. Create new request in Postman
2. Set method to `POST`
3. URL: `http://localhost:8080/api/v1/auth/login`
4. Headers: `Content-Type: application/json`
5. Body: Use login JSON above
6. Click **Send**
7. **Copy the new token** from response

### **Step 4: Get Current User Info**
1. Create new request in Postman
2. Set method to `GET`
3. URL: `http://localhost:8080/api/v1/auth/me`
4. Go to **Headers** tab, add:
   - `Content-Type: application/json`
   - `Authorization: Bearer [PASTE_YOUR_TOKEN_HERE]`
5. Click **Send**
6. Should return your user details

---

## 🔧 **Additional Test Users**

### **Staff User Registration:**
```json
{
    "username": "jane_staff",
    "email": "jane@business.com",
    "password": "password123",
    "firstName": "Jane",
    "lastName": "Smith",
    "role": "STAFF",
    "phoneNumber": "+1-234-567-8901"
}
```

### **Admin User Registration:**
```json
{
    "username": "admin_user",
    "email": "admin@business.com",
    "password": "password123",
    "firstName": "Admin",
    "lastName": "User",
    "role": "ADMIN",
    "businessName": "System Administration",
    "phoneNumber": "+1-234-567-8902"
}
```

---

## ⚠️ **Common Issues & Solutions**

### **Issue 1: Token Expired**
- **Error:** `401 Unauthorized`
- **Solution:** Login again to get a new token

### **Issue 2: Invalid JSON**
- **Error:** `400 Bad Request`
- **Solution:** Check JSON syntax and required fields

### **Issue 3: User Already Exists**
- **Error:** `400 Bad Request` - "Username already exists"
- **Solution:** Use different username/email

### **Issue 4: Missing Authorization Header**
- **Error:** `401 Unauthorized`
- **Solution:** Add `Authorization: Bearer [token]` header

---

## 🎯 **Token Usage Notes**

1. **Token Format:** `Bearer [actual-jwt-token]`
2. **Token Expiration:** 24 hours (86400000 ms)
3. **Token Location:** Include in `Authorization` header
4. **Token Scope:** Required for all protected endpoints

---

## 📊 **Expected Status Codes**

- `200 OK` - Successful request
- `201 Created` - User registered successfully
- `400 Bad Request` - Invalid input data
- `401 Unauthorized` - Missing/invalid token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Endpoint not found
- `500 Internal Server Error` - Server error

---

## 🚀 **Ready to Test!**

Your **Ledgerly** application is running and ready for testing. Use these examples in Postman to verify all authentication functionality works correctly.

**Happy Testing! 🎉**
