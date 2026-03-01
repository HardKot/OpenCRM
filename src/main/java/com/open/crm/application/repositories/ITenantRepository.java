package com.open.crm.application.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.open.crm.domain.common.Tenant;

public interface ITenantRepository extends JpaRepository<Tenant, UUID>{
    
}
