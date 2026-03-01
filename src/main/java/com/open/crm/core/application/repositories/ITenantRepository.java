package com.open.crm.core.application.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.open.crm.tenancy.Tenant;


@Repository
public interface ITenantRepository extends JpaRepository<Tenant, UUID> {
    
}
