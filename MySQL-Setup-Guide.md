# MySQL Setup Guide for Ledgerly Application

## Database Configuration

The Ledgerly application is now configured to use MySQL database with the following settings:

### Database Details
- **Database Name**: `ledgerly`
- **Username**: `root` 
- **Password**: `4566`
- **Host**: `localhost`
- **Port**: `3306`

## Prerequisites

1. **Install MySQL Server**
   - Download and install MySQL Server from [MySQL Official Website](https://dev.mysql.com/downloads/mysql/)
   - During installation, set the root password to `4566`

2. **Start MySQL Service**
   - On Windows: Start MySQL service from Services panel or MySQL Workbench
   - On macOS: `brew services start mysql`
   - On Linux: `sudo systemctl start mysql`

## Database Setup

### Option 1: Automatic Setup (Recommended)
The application will automatically:
- Create the `ledgerly` database if it doesn't exist
- Create all required tables using Hibernate/JPA
- Handle schema updates automatically

Just start the application with `mvn spring-boot:run`

### Option 2: Manual Setup
If you prefer manual setup:

1. **Connect to MySQL**
   ```bash
   mysql -u root -p4566
   ```

2. **Create Database**
   ```sql
   CREATE DATABASE ledgerly;
   USE ledgerly;
   ```

3. **Verify Connection**
   ```sql
   SHOW DATABASES;
   ```

## Configuration Details

### Application Properties
```properties
# Database URL with auto-create option
spring.datasource.url=jdbc:mysql://localhost:3306/ledgerly?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

# Database credentials
spring.datasource.username=root
spring.datasource.password=4566

# JPA settings for MySQL
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### DDL Mode Explanation
- `ddl-auto=update`: Hibernate will update the schema automatically
- Tables will be created on first run
- Schema changes will be applied automatically
- Data is preserved between application restarts

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure MySQL server is running
   - Check if port 3306 is available
   - Verify credentials are correct

2. **Authentication Failed**
   - Make sure root password is set to `4566`
   - Try resetting MySQL root password if needed

3. **Database Not Found**
   - The application will create the database automatically
   - Ensure `createDatabaseIfNotExist=true` is in the URL

4. **SSL/Security Issues**
   - Current config uses `useSSL=false` for development
   - For production, enable SSL and remove `allowPublicKeyRetrieval=true`

### Testing Connection
Run the database connection test:
```bash
mvn test -Dtest=DatabaseConnectionTest
```

## Production Considerations

For production deployment:

1. **Change Credentials**
   - Create a dedicated database user instead of using root
   - Use environment variables for credentials

2. **Enable SSL**
   - Remove `useSSL=false` from URL
   - Configure SSL certificates

3. **Connection Pooling**
   - Configure HikariCP settings for optimal performance

4. **Backup Strategy**
   - Set up regular database backups
   - Consider MySQL replication for high availability

## Environment Variables (Optional)

You can override database settings using environment variables:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=ledgerly
export DB_USERNAME=root
export DB_PASSWORD=4566
```

Then update application.properties to use them:
```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:ledgerly}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:4566}
```
