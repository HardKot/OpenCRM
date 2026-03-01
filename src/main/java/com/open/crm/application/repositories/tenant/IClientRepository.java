package com.open.crm.application.repositories.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.open.crm.domain.client.Client;

/**
 * Repository for Client entity in tenant-specific schema
 */
@Repository
public interface IClientRepository extends JpaRepository<Client, Long> {
    
}
