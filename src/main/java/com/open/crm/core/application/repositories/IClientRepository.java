package com.open.crm.core.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.open.crm.core.domain.client.Client;

/**
 * Repository for Client entity in tenant-specific schema
 */
@Repository
public interface IClientRepository extends JpaRepository<Client, Long> {

}
