# Ledgerly Application Status Check Guide

This guide provides comprehensive methods to check whether your Ledgerly Spring Boot application is running or not, along with troubleshooting scenarios.

## 🚀 Quick Status Check

### Method 1: Health Endpoint (Recommended)
```bash
# PowerShell
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/health" -UseBasicParsing

# Expected Success Response:
# StatusCode: 200
# Content: {"application":"Ledgerly","message":"Ledgerly application is running successfully","version":"0.0.1-SNAPSHOT","status":"UP"}
```

### Method 2: Port Check
```bash
# Check if port 8080 is in use
netstat -ano | findstr :8080

# Expected Output:
# TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING       12345
```

### Method 3: Process Check
```bash
# Find Java processes
tasklist | findstr java

# Find Spring Boot processes
tasklist | findstr spring-boot
```

## 📊 Application Status Scenarios

### ✅ Scenario 1: Application Running Successfully

**Indicators:**
- Health endpoint returns 200 OK
- Port 8080 shows LISTENING status
- Java process visible in tasklist
- Console shows "Started LedgerlyApplication"

**Console Output:**
```
2025-08-19T08:55:20.859+05:30  INFO 31752 --- [nio-8080-exec-1] c.example.ledgerly.LedgerlyApplication   : Started LedgerlyApplication in 10.1 seconds
2025-08-19T08:55:20.833+05:30 DEBUG 31752 --- [nio-8080-exec-1] o.s.security.web.FilterChainProxy        : Securing GET /api/v1/health
```

**Verification Commands:**
```bash
# Test health endpoint
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/health" -UseBasicParsing

# Check port status
netstat -ano | findstr :8080

# Verify process
tasklist | findstr 31752
```

---

### ❌ Scenario 2: Application Failed to Start

**Indicators:**
- Console shows "APPLICATION FAILED TO START"
- No Java process running
- Port 8080 not in use
- Build failure in Maven

**Console Output:**
```
***************************
APPLICATION FAILED TO START
***************************
Description:
Parameter 1 of constructor in com.example.ledgerly.service.UserService required a bean of type 'org.springframework.security.crypto.password.PasswordEncoder' that could not be found.

Action:
Consider defining a bean of type 'org.springframework.security.crypto.password.PasswordEncoder' in your configuration.
```

**Troubleshooting Steps:**
1. **Check for compilation errors:**
   ```bash
   mvn clean compile
   ```

2. **Verify dependencies in pom.xml:**
   - Ensure Spring Boot starter dependencies are present
   - Check for missing beans in SecurityConfig

3. **Check application.properties:**
   - Verify database connection settings
   - Check for syntax errors

4. **Restart application:**
   ```bash
   mvn spring-boot:run
   ```

---

### ⚠️ Scenario 3: Port Already in Use

**Indicators:**
- Console shows "Port 8080 was already in use"
- Application startup fails
- Multiple Java processes might be running

**Console Output:**
```
Description:
Web server failed to start. Port 8080 was already in use.

Action:
Identify and stop the process that's listening on port 8080 or configure this application to listen on another port.
```

**Troubleshooting Steps:**
1. **Find process using port 8080:**
   ```bash
   netstat -ano | findstr :8080
   ```

2. **Kill the process:**
   ```bash
   # Replace PID with actual process ID
   taskkill /PID 12345 /F
   ```

3. **Alternative: Change port in application.properties:**
   ```properties
   server.port=8081
   ```

4. **Restart application:**
   ```bash
   mvn spring-boot:run
   ```

---

### 🔄 Scenario 4: Application Starting Up

**Indicators:**
- Console shows startup progress
- Database connection being established
- Hibernate creating tables
- Port not yet listening

**Console Output:**
```
2025-08-19T08:54:00.209+05:30  INFO 31752 --- [main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2025-08-19T08:54:04.639+05:30 DEBUG 31752 --- [main] o.s.s.web.DefaultSecurityFilterChain     : Will secure any request with filters: ...
2025-08-19T08:54:04.968+05:30  INFO 31752 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http)
```

**Wait for completion:**
- Look for "Started LedgerlyApplication" message
- Health endpoint will return 200 only after full startup
- Typically takes 10-15 seconds

---

### 🚫 Scenario 5: Application Crashed After Startup

**Indicators:**
- Application started successfully but then stopped
- Port 8080 no longer listening
- Java process terminated
- Health endpoint returns connection refused

**Troubleshooting Steps:**
1. **Check application logs for errors:**
   ```bash
   # Look for error messages in console
   # Common causes: Database connection lost, OutOfMemoryError, etc.
   ```

2. **Check system resources:**
   ```bash
   # Check available memory
   wmic OS get TotalVisibleMemorySize,FreePhysicalMemory
   
   # Check disk space
   dir C:\
   ```

3. **Restart application:**
   ```bash
   mvn spring-boot:run
   ```

---

## 🛠️ Diagnostic Commands

### System Health Check
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check available ports
netstat -an | findstr LISTENING

# Check running processes
tasklist | findstr java
```

### Application Health Check
```bash
# Test health endpoint
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/health" -UseBasicParsing

# Test actuator endpoints
Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing

# Test authentication endpoint
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/auth/health" -UseBasicParsing
```

### Database Connection Check
```bash
# Test MySQL connection (if using MySQL)
mysql -u root -p -h localhost -P 3306

# Check if MySQL service is running
sc query mysql
```

## 📋 Status Check Checklist

### Before Starting Application
- [ ] Java 17+ installed and in PATH
- [ ] Maven installed and in PATH
- [ ] MySQL running (if using MySQL)
- [ ] Port 8080 available
- [ ] All dependencies resolved

### During Startup
- [ ] Maven compilation successful
- [ ] Database connection established
- [ ] JPA entities created
- [ ] Security configuration loaded
- [ ] Tomcat started on port 8080
- [ ] Application context initialized

### After Startup
- [ ] Health endpoint returns 200
- [ ] Port 8080 shows LISTENING
- [ ] Java process visible in tasklist
- [ ] Console shows "Started LedgerlyApplication"
- [ ] Database tables accessible
- [ ] Authentication endpoints working

## 🚨 Emergency Recovery

### If Application Won't Start
1. **Kill all Java processes:**
   ```bash
   taskkill /F /IM java.exe
   ```

2. **Clear Maven cache:**
   ```bash
   mvn clean
   rm -rf target/
   ```

3. **Restart from scratch:**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

### If Port is Blocked
1. **Find blocking process:**
   ```bash
   netstat -ano | findstr :8080
   ```

2. **Kill blocking process:**
   ```bash
   taskkill /PID <PID> /F
   ```

3. **Or change port:**
   ```properties
   # In application.properties
   server.port=8081
   ```

## 📞 Common Error Messages & Solutions

| Error Message | Cause | Solution |
|---------------|-------|----------|
| "Port 8080 was already in use" | Another application using port | Kill process or change port |
| "PasswordEncoder bean not found" | Missing SecurityConfig bean | Check SecurityConfig class |
| "Database connection failed" | MySQL not running | Start MySQL service |
| "Class not found" | Missing dependency | Check pom.xml and run `mvn clean compile` |
| "OutOfMemoryError" | Insufficient memory | Increase heap size or close other applications |

## 🔍 Advanced Debugging

### Enable Debug Logging
```properties
# In application.properties
logging.level.org.springframework.security=DEBUG
logging.level.com.example.ledgerly=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Check Application Context
```bash
# View all beans
curl http://localhost:8080/actuator/beans

# View configuration properties
curl http://localhost:8080/actuator/configprops
```

### Monitor Application in Real-time
```bash
# Watch port usage
while ($true) { netstat -ano | findstr :8080; Start-Sleep -Seconds 2; Clear-Host }

# Monitor Java process
Get-Process java | Select-Object Id,ProcessName,CPU,WorkingSet
```

---

## 📝 Summary

- **✅ Running**: Health endpoint returns 200, port listening, process visible
- **❌ Failed**: Console shows errors, no process, port not listening
- **🔄 Starting**: Console shows startup progress, wait for completion
- **⚠️ Port Conflict**: Kill blocking process or change port
- **🚫 Crashed**: Check logs and restart application

Always start with the health endpoint check as it's the most reliable indicator of application status.
