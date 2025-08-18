package com.example.ledgerly.entity;

/**
 * User roles in the Ledgerly system
 */
public enum Role {
    /**
     * Business owner - full access to all business-related features
     * Can invite, assign, and revoke staff
     * Can view and manage all customers, ledgers, payments, analytics, and reminders
     * Can export data and view activity logs for their business
     */
    OWNER,

    /**
     * Staff member - limited access as delegated by the owner
     * Can manage customers, ledgers, and payments if granted permission
     * Can view analytics (read-only, no export)
     * Can receive/send reminders if allowed
     * Cannot invite other staff, change roles, or access admin panel
     */
    STAFF,

    /**
     * System administrator - system-wide management capabilities
     * Can manage multiple businesses and users
     * Can access system-level analytics and configurations
     */
    ADMIN
}
