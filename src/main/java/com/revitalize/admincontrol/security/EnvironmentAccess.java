package com.revitalize.admincontrol.security;

public enum EnvironmentAccess {
    DEV,
    STAGING,
    PROD;

    public static EnvironmentAccess from(String value) {
        for (EnvironmentAccess access : values()) {
            if (access.name().equalsIgnoreCase(value)) {
                return access;
            }
        }
        throw new IllegalArgumentException("Unsupported environment: " + value);
    }
}
