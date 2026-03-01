package com.open.crm.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.open.crm.application.repositories.tenant.IClientRepository;
import com.open.crm.domain.client.Client;
import com.open.crm.tenancy.TenantContext;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing Clients in tenant-specific schemas
 * Demonstrates usage of tenant-specific repositories
 */
@Service
@RequiredArgsConstructor
public class ClientService {
    
    private final IClientRepository clientRepository;

    /**
     * Create a new client in the current tenant's schema
     * TenantContext must be set before calling this method
     */
    @Transactional("tenantTransactionManager")
    public Client createClient(String firstName, String lastName, String email, String phoneNumber) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPhoneNumber(phoneNumber);
        // tenantId will be automatically set by Hibernate @TenantId
        return clientRepository.save(client);
    }

    /**
     * Get all clients for the current tenant
     */
    @Transactional(value = "tenantTransactionManager", readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Get client by ID in the current tenant's schema
     */
    @Transactional(value = "tenantTransactionManager", readOnly = true)
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found: " + id));
    }

    /**
     * Update client in the current tenant's schema
     */
    @Transactional("tenantTransactionManager")
    public Client updateClient(Long id, String firstName, String lastName) {
        Client client = getClientById(id);
        client.setFirstName(firstName);
        client.setLastName(lastName);
        return clientRepository.save(client);
    }

    /**
     * Delete client from the current tenant's schema
     */
    @Transactional("tenantTransactionManager")
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    /**
     * Example of working with a specific tenant
     * This method sets the tenant context explicitly
     */
    public Client createClientForTenant(UUID tenantId, String firstName, String lastName, String email) {
        try {
            TenantContext.setCurrentTenant(tenantId);
            return createClient(firstName, lastName, email, null);
        } finally {
            TenantContext.clear();
        }
    }
}
