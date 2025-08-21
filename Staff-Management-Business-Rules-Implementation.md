# Staff Management Business Rules Implementation

## Overview
This document outlines the implementation of business rules that restrict staff self-registration and ensure owners can only manage staff they created.

## Business Rules Implemented

### 1. Staff Cannot Self-Register
- **Rule**: Staff users cannot register themselves through the `/auth/register` endpoint
- **Implementation**: Updated `UserService.register()` method to throw an exception when `Role.STAFF` is requested
- **Error Message**: "STAFF role can only be created by an OWNER."

### 2. Staff Cannot Create Other Staff
- **Rule**: Staff users cannot create, update, or delete other staff accounts
- **Implementation**: All staff management endpoints in `OwnerController` require `OWNER` or `ADMIN` role
- **Access Control**: `@PreAuthorize("hasAnyRole('OWNER','ADMIN')")`

### 3. Owner Can Only Manage Staff They Created
- **Rule**: Owners can only perform CRUD operations on staff accounts they created
- **Implementation**: Added `createdBy` field to `User` entity and updated all staff management methods
- **Access Control**: Filter staff operations based on `createdBy` field

### 4. Admin Can Manage All Staff
- **Rule**: Admin users retain full access to manage all staff accounts
- **Implementation**: Admin role bypasses the `createdBy` restriction in all methods

## Technical Implementation

### Database Changes
- **New Column**: Added `created_by` column to `users` table
- **Foreign Key**: References `users.id` to track creator
- **Migration Script**: `src/main/resources/db/migration-add-created-by.sql`

### Entity Changes
- **User Entity**: Added `createdBy` field with `@ManyToOne` relationship
- **Getter/Setter**: Added accessor methods for the new field

### Service Layer Changes
- **UserService**: Updated registration logic to prevent staff self-registration
- **OwnerController**: Enhanced all staff management methods with creator validation

### Method-Level Security
- **createStaff()**: Sets `createdBy` field to current authenticated user
- **updateStaff()**: Validates creator before allowing updates
- **deleteStaff()**: Validates creator before allowing deletion
- **listStaff()**: Filters results based on creator (for owners)

## API Endpoints Affected

### Owner Controller (`/api/v1/owner/staff`)
- `POST /` - Create staff (sets creator)
- `PUT /{id}` - Update staff (validates creator)
- `DELETE /{id}` - Delete staff (validates creator)
- `GET /` - List staff (filters by creator for owners)

### Auth Controller (`/api/v1/auth`)
- `POST /register` - Registration (blocks staff role)

## Security Features

### Role-Based Access Control
- **ADMIN**: Full access to all staff management operations
- **OWNER**: Limited access to staff they created
- **STAFF**: No access to staff management operations

### Creator Validation
- All staff operations validate the `createdBy` field
- Owners cannot access staff accounts created by other owners
- Maintains data isolation between different business owners

### Audit Trail
- Tracks who created each staff account
- Enables accountability and access control
- Supports future audit and compliance requirements

## Database Migration

### Running the Migration
```sql
-- Execute the migration script
source src/main/resources/db/migration-add-created-by.sql
```

### Migration Details
- Adds `created_by` column (nullable)
- Creates foreign key constraint
- Adds performance index
- Updates existing records

## Testing Scenarios

### Valid Operations
1. **Admin creates staff** → Success, admin becomes creator
2. **Owner creates staff** → Success, owner becomes creator
3. **Admin updates any staff** → Success
4. **Owner updates their staff** → Success
5. **Owner deletes their staff** → Success

### Invalid Operations
1. **Staff self-registration** → Blocked with error message
2. **Staff creates other staff** → Blocked by role restriction
3. **Owner updates other owner's staff** → Blocked by creator validation
4. **Owner deletes other owner's staff** → Blocked by creator validation

## Benefits

### Security
- Prevents unauthorized staff creation
- Ensures data isolation between businesses
- Maintains role hierarchy integrity

### Compliance
- Audit trail for staff account creation
- Clear ownership of staff accounts
- Accountability for user management actions

### Business Logic
- Enforces proper business hierarchy
- Prevents cross-business staff management
- Supports multi-tenant architecture

## Future Enhancements

### Potential Improvements
1. **Bulk Operations**: Support for bulk staff management
2. **Transfer Ownership**: Allow staff transfer between owners
3. **Temporary Access**: Time-limited access grants
4. **Audit Logging**: Comprehensive logging of all operations
5. **Notification System**: Alert owners of staff changes

### Monitoring
- Track staff creation patterns
- Monitor access violations
- Generate compliance reports
- Performance metrics for creator validation

## Conclusion

The implementation successfully enforces the business rules that:
- Prevent staff self-registration
- Restrict staff management to appropriate roles
- Ensure owners can only manage their own staff
- Maintain admin oversight capabilities
- Provide audit trail for compliance

This creates a secure, compliant, and business-logic-aligned staff management system.
