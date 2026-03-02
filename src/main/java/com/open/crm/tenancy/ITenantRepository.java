package com.open.crm.tenancy;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ITenantRepository extends JpaRepository<Tenant, UUID> {
    List<Tenant> findByReady(boolean ready);
}
