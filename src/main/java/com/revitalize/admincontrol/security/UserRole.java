package com.revitalize.admincontrol.security;

public enum UserRole {
    SUPER_ADMIN,
    ADMIN,
    OPERATOR,
    API;

    public boolean canManageUsers() {
        return this == SUPER_ADMIN || this == ADMIN;
    }

    public boolean isApiClient() {
        return this == API;
    }
}
