package com.open.crm.core.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.open.crm.core.application.errors.ClientException;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.repositories.IClientRepository;
import com.open.crm.core.application.repositories.IInvestigationLogRepository;
import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final IClientRepository clientRepository;

    public Client createClient(Client client) {
        client = clientRepository.save(client);
        return client;
    }

    public Client updateClient(Client client) throws NotFoundException, ClientException {
        Client existingClient = clientRepository.findById(client.getId())
                .orElseThrow(() -> new NotFoundException("Client not found with ID: " + client.getId()));

        if (existingClient.isDeleted()) {
            throw new ClientException("Cannot update a deleted client with ID: " + client.getId());
        }

        Client updatedClient = clientRepository.save(existingClient);
        return updatedClient;
    }

    public Client deleteClient(Client client) throws NotFoundException, ClientException {
        if (client.isDeleted()) {
            throw new ClientException("Cannot delete a already deleted client with ID: " + client.getId());
        }
        client.setDeleted(true);
        Client deletedClient = clientRepository.save(client);
        return deletedClient;
    }

    public Client restoreClient(Client client) throws NotFoundException, ClientException {
        if (!client.isDeleted()) {
            throw new ClientException("Cannot restore a non-deleted client with ID: " + client.getId());
        }
        client.setDeleted(false);
        Client restoredClient = clientRepository.save(client);
        return restoredClient;
    }

    public Client mergeClients(Client targetClient, Client[] sourceClients)
            throws NotFoundException, ClientException {
        if (targetClient.isDeleted()) {
            throw new NotFoundException("Cannot merge into a deleted client with ID: " + targetClient.getId());
        }

        for (Client sourceClient : sourceClients) {
            Client existingSourceClient = clientRepository.findById(sourceClient.getId())
                    .orElseThrow(
                            () -> new NotFoundException("Source client not found with ID: " + sourceClient.getId()));
            if (existingSourceClient.isDeleted()) {
                throw new NotFoundException("Cannot merge deleted client with ID: " + sourceClient.getId());
            }

            targetClient.merge(existingSourceClient);
            existingSourceClient.setDeleted(true);
            clientRepository.save(existingSourceClient);
        }
        Client mergedClient = clientRepository.save(targetClient);
        return mergedClient;
    }

}
