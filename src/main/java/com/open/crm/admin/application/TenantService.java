package com.open.crm.admin.application;

import org.springframework.stereotype.Component;

import com.open.crm.admin.application.exceptions.TenantException;
import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.entities.tenant.Tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantService {

    private final ITenantRepository tenantRepository;

    public Tenant generateTenant() throws TenantException {
        try {
            Tenant tenant = new Tenant();
            tenant.setActive(true);
            tenant.setReady(false);
            tenant.setSchemaName(generateUniqSchemaName());

            tenantRepository.save(tenant);
            return tenant;

        }
        catch (Exception e) {
            throw new TenantException("Error creating tenant: " + e.getMessage());
        }
    }

    private String generateUniqSchemaName() {
        long countTenant = tenantRepository.count();

        String schemaName = String.format("tenant_%05d", countTenant + 1);

        return schemaName;
    }

}
