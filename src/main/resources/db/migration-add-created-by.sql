-- Migration: Add created_by column to users table
-- This migration adds a foreign key reference to track who created each user

USE ledgerly;

-- Add created_by column to users table
ALTER TABLE users ADD COLUMN created_by BIGINT;

-- Add foreign key constraint
ALTER TABLE users ADD CONSTRAINT fk_users_created_by 
    FOREIGN KEY (created_by) REFERENCES users(id);

-- Create index for better performance
CREATE INDEX idx_users_created_by ON users(created_by);

-- Update existing users to set created_by to NULL (they were created before this feature)
-- This is safe as the column allows NULL values
UPDATE users SET created_by = NULL WHERE created_by IS NULL;
