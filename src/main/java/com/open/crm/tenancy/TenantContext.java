package com.open.crm.tenancy;

import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();
    public static void setCurrentTenant(UUID tenant) { CURRENT.set(tenant); }
    public static UUID getCurrentTenant() { return CURRENT.get(); }
    public static void clear() { CURRENT.remove(); }
}