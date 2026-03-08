package com.open.crm.tenancy;

import com.open.crm.admin.entities.tenant.Tenant;

public class TenantContext {

    private static final ThreadLocal<Tenant> CURRENT = new ThreadLocal<>();

    public static void setCurrentTenant(Tenant tenant) {
        CURRENT.set(tenant);
    }

    public static Tenant getCurrentTenant() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

}