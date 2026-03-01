package com.open.crm.application.repositories.common;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.open.crm.domain.common.Tenant;

/**
 * Repository for Tenant entity in public schema
 */
@Repository
public interface ITenantRepository extends JpaRepository<Tenant, UUID> {
    
}
