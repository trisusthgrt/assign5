-- MySQL Database Setup for Ledgerly Application
-- Run this script if you need to manually create the database

-- Create database (this will be done automatically due to createDatabaseIfNotExist=true)
CREATE DATABASE IF NOT EXISTS ledgerly;

-- Use the database
USE ledgerly;

-- Grant privileges to root user (if needed)
-- GRANT ALL PRIVILEGES ON ledgerly.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

-- The tables will be automatically created by Hibernate/JPA
-- when the application starts due to spring.jpa.hibernate.ddl-auto=update

-- Sample data insertion (optional - you can run this manually)
-- INSERT INTO users (username, email, password, first_name, last_name, role, is_active, is_email_verified, created_at, updated_at) 
-- VALUES ('admin', 'admin@ledgerly.com', '$2a$10$encoded_password_here', 'Admin', 'User', 'ADMIN', true, true, NOW(), NOW());
