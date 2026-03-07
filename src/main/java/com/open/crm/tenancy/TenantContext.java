package com.open.crm.tenancy;

public class TenantContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    public static void setCurrentTenantSchemaName(String tenant) {
        CURRENT.set(tenant);
    }

    public static String getCurrentTenantSchemaName() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

}