package com.open.crm.admin.application.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.open.crm.admin.entities.tenant.Tenant;

@Repository
public interface ITenantRepository extends JpaRepository<Tenant, UUID> {

    List<Tenant> findByReady(boolean ready);

}
