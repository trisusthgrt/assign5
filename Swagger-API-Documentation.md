# 📚 **Swagger API Documentation - Ledgerly**

## 🚀 **Overview**

Your Ledgerly Spring Boot application now includes comprehensive API documentation powered by **Swagger/OpenAPI 3**. This provides an interactive interface to explore, test, and understand all your API endpoints.

## 🌐 **Accessing Swagger UI**

### **Local Development:**
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

### **Production (when deployed):**
- **Swagger UI**: https://your-domain.com/swagger-ui.html
- **OpenAPI JSON**: https://your-domain.com/api-docs

## 🔧 **Features**

### **✅ Interactive Documentation:**
- **Try It Out**: Test API endpoints directly from the browser
- **Request/Response Examples**: See expected data formats
- **Authentication**: JWT Bearer token support
- **Parameter Validation**: Clear parameter descriptions
- **Response Codes**: Detailed HTTP status explanations

### **✅ Security Integration:**
- **JWT Authentication**: Bearer token support
- **Role-Based Access**: Clear permission requirements
- **Secure Endpoints**: Protected route documentation

### **✅ API Organization:**
- **Tagged Endpoints**: Grouped by functionality
- **Method Sorting**: HTTP methods organized logically
- **Alpha Sorting**: Endpoints sorted alphabetically

## 📋 **Available API Groups**

### **🔐 Authentication**
- User registration and login
- Profile management
- User administration
- Role assignment

### **🏥 Health Check**
- Basic health monitoring
- Detailed system information
- Application status

### **🏪 Shop Management**
- Shop creation and management
- Shop assignment
- Location-based queries

### **👥 Customer Management**
- Customer CRUD operations
- Search and filtering
- Relationship management

### **📊 Ledger Management**
- Transaction recording
- Balance calculations
- Financial reporting

### **💰 Payment Processing**
- Payment recording
- Status tracking
- Reconciliation

## 🛠️ **Using Swagger UI**

### **1. Authentication Setup:**
1. Click the **"Authorize"** button (🔒) at the top
2. Enter your JWT token: `Bearer <your-jwt-token>`
3. Click **"Authorize"**
4. Close the dialog

### **2. Testing Endpoints:**
1. Find the endpoint you want to test
2. Click **"Try it out"**
3. Fill in required parameters
4. Click **"Execute"**
5. View the response

### **3. Understanding Responses:**
- **200**: Success
- **400**: Bad Request (validation errors)
- **401**: Unauthorized (missing/invalid token)
- **403**: Forbidden (insufficient permissions)
- **404**: Not Found
- **500**: Internal Server Error

## 🔑 **Getting JWT Token**

### **1. Register a User:**
```bash
POST /api/v1/auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User",
  "role": "STAFF"
}
```

### **2. Login to Get Token:**
```bash
POST /api/v1/auth/login
{
  "usernameOrEmail": "testuser",
  "password": "password123"
}
```

### **3. Use the Token:**
Copy the `token` from the response and use it in Swagger UI:
```
Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 📱 **Mobile-Friendly**

Swagger UI is fully responsive and works great on:
- Desktop browsers
- Tablets
- Mobile devices

## 🎨 **Customization Options**

### **Configuration in `application.properties`:**
```properties
# Swagger UI path
springdoc.swagger-ui.path=/swagger-ui.html

# API docs path
springdoc.api-docs.path=/api-docs

# UI customization
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.doc-expansion=none
springdoc.swagger-ui.display-request-duration=true
```

### **Available Customizations:**
- **Path customization**: Change Swagger UI URL
- **Sorting**: Method-based or alphabetical
- **Expansion**: Collapse/expand sections
- **Timing**: Show request duration
- **Filtering**: Search through endpoints

## 🚨 **Security Notes**

### **Development vs Production:**
- **Development**: Swagger UI accessible to all
- **Production**: Consider restricting access to admin users only

### **Best Practices:**
- Don't expose Swagger UI in production without proper security
- Use environment-specific configurations
- Monitor API usage through Swagger

## 🔍 **Troubleshooting**

### **Common Issues:**

1. **Swagger UI not accessible:**
   - Check if application is running
   - Verify security configuration allows access
   - Check console for errors

2. **Authentication not working:**
   - Ensure JWT token is valid
   - Check token format: `Bearer <token>`
   - Verify token hasn't expired

3. **Endpoints not showing:**
   - Check controller annotations
   - Verify Spring Boot version compatibility
   - Check for compilation errors

## 📚 **Additional Resources**

- [OpenAPI 3.0 Specification](https://swagger.io/specification/)
- [SpringDoc Documentation](https://springdoc.org/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

## 🎯 **Next Steps**

1. **Test all endpoints** using Swagger UI
2. **Document additional controllers** with annotations
3. **Customize the UI** appearance if needed
4. **Add more detailed descriptions** to endpoints
5. **Include request/response examples** for complex operations

---

**Happy API Testing! 🚀**

