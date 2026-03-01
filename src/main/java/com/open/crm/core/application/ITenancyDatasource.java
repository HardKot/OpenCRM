package com.open.crm.core.application;

import com.open.crm.tenancy.Tenant;

public interface ITenancyDatasource {
    void createSchemaForTenant(Tenant tenant);
}
