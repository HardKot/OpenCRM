package com.open.crm.core.application.services;

import com.open.crm.core.application.InvestigationLogCreator;
import com.open.crm.core.application.errors.ClientException;
import com.open.crm.core.application.errors.NotFoundException;
import com.open.crm.core.application.repositories.IClientRepository;
import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {
  private final IClientRepository clientRepository;
  private final InvestigationLogCreator investigationLogCreator;
  private final InvestigationLogService investigationLogService;

  @Qualifier("clientSelectorData") private final SelectorData<Client> clientSelector;

  @Transactional
  public Client createClient(Client client, Author author, ClientInfoCleaner cleaner) {
    client = clearClientInfo(client, cleaner);
    client.setId(null);
    client.setDeleted(false);
    client.setBalance(0);
    client.setCreatedAt(null);
    client.setUpdatedAt(null);

    client = clientRepository.save(client);
    InvestigationLog log = investigationLogCreator.createClientLog(client, author);
    investigationLogService.saveLog(log);

    return client;
  }

  @Transactional
  public Client updateClient(Client client, Author author, ClientInfoCleaner cleaner)
      throws NotFoundException, ClientException {
    Client existingClient =
        clientRepository
            .findById(client.getId())
            .orElseThrow(
                () -> new NotFoundException("Client not found with ID: " + client.getId()));

    if (existingClient.isDeleted()) {
      throw new ClientException("Cannot update a deleted client with ID: " + client.getId());
    }

    if (!cleaner.cleanName()) {
      existingClient.setFirstname(client.getFirstname());
      existingClient.setLastname(client.getLastname());
      existingClient.setPatronymic(client.getPatronymic());
    }

    if (!cleaner.cleanContact()) {
      existingClient.setEmail(client.getEmail());
      existingClient.setPhoneNumber(client.getPhoneNumber());
    }

    Client updatedClient = clientRepository.save(existingClient);
    InvestigationLog log = investigationLogCreator.updateClientLog(updatedClient, author);
    investigationLogService.saveLog(log);

    return clearClientInfo(updatedClient, cleaner);
  }

  @Transactional
  public Client deleteClient(Client client, Author author, ClientInfoCleaner cleaner)
      throws NotFoundException, ClientException {
    if (client.isDeleted()) {
      throw new ClientException(
          "Cannot delete a already deleted client with ID: " + client.getId());
    }
    client.setDeleted(true);
    Client deletedClient = clientRepository.save(client);
    InvestigationLog log = investigationLogCreator.updateClientLog(deletedClient, author);
    investigationLogService.saveLog(log);
    return clearClientInfo(deletedClient, cleaner);
  }

  @Transactional
  public Client restoreClient(Client client, Author author, ClientInfoCleaner cleaner)
      throws NotFoundException, ClientException {
    if (!client.isDeleted()) {
      throw new ClientException("Cannot restore a non-deleted client with ID: " + client.getId());
    }
    client.setDeleted(false);
    Client restoredClient = clientRepository.save(client);
    InvestigationLog log = investigationLogCreator.updateClientLog(restoredClient, author);
    investigationLogService.saveLog(log);
    return clearClientInfo(restoredClient, cleaner);
  }

  @Transactional
  public Client mergeClients(
      Client targetClient, Client[] sourceClients, Author author, ClientInfoCleaner cleaner)
      throws NotFoundException, ClientException {
    if (targetClient.isDeleted()) {
      throw new NotFoundException(
          "Cannot merge into a deleted client with ID: " + targetClient.getId());
    }

    for (Client sourceClient : sourceClients) {
      Client existingSourceClient =
          clientRepository
              .findById(sourceClient.getId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Source client not found with ID: " + sourceClient.getId()));
      if (existingSourceClient.isDeleted()) {
        throw new NotFoundException("Cannot merge deleted client with ID: " + sourceClient.getId());
      }

      if (existingSourceClient.getId() == targetClient.getId()) {
        throw new ClientException(
            "Cannot merge client with itself. Client ID: " + targetClient.getId());
      }

      if (targetClient.getFirstname().isBlank()) {
        targetClient.setFirstname(existingSourceClient.getFirstname());
      }
      if (targetClient.getLastname().isBlank()) {
        targetClient.setLastname(existingSourceClient.getLastname());
      }
      if (targetClient.getPatronymic().isBlank()) {
        targetClient.setPatronymic(existingSourceClient.getPatronymic());
      }
      if (targetClient.getEmail().isBlank()) {
        targetClient.setEmail(existingSourceClient.getEmail());
      }
      if (targetClient.getPhoneNumber().isBlank()) {
        targetClient.setPhoneNumber(existingSourceClient.getPhoneNumber());
      }

      targetClient.setBalance(targetClient.getBalance() + existingSourceClient.getBalance());

      existingSourceClient.setDeleted(true);
      clientRepository.save(existingSourceClient);
    }
    Client mergedClient = clientRepository.save(targetClient);
    InvestigationLog log =
        investigationLogCreator.mergeClientLog(mergedClient, sourceClients, author);
    investigationLogService.saveLog(log);
    return mergedClient;
  }

  @Transactional
  public Client manualUpdateClientBalance(Client client, long newBalance, Author author)
      throws NotFoundException {
    if (client.isDeleted()) {
      throw new NotFoundException(
          "Cannot update balance of a deleted client with ID: " + client.getId());
    }
    client.setBalance(newBalance);
    Client updatedClient = clientRepository.save(client);
    InvestigationLog log = investigationLogCreator.updateClientBalanceLog(updatedClient, author);
    investigationLogService.saveLog(log);
    return updatedClient;
  }

  public Client getClientById(long id, boolean withDeleted, ClientInfoCleaner cleaner)
      throws NotFoundException {
    Client client =
        clientRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Client not found with ID: " + id));
    if (client.isDeleted() && !withDeleted) {
      throw new NotFoundException("Client not found with ID: " + id);
    }
    return clearClientInfo(client, cleaner);
  }

  public List<Client> getClients(
      int page, int size, boolean withDeleted, ClientInfoCleaner cleaner) {
    List<Client> clients = clientSelector.getItems(page, size, withDeleted);
    return clients.stream().map(client -> clearClientInfo(client, cleaner)).toList();
  }

  public List<Client> getDuplicateClients(Client client, ClientInfoCleaner cleaner) {
    List<Client> byEmail = List.of();
    List<Client> byPhone = List.of();

    if (Objects.nonNull(client.getEmail()) && !client.getEmail().isBlank()) {
      byEmail = clientRepository.findByEmailAndIsDeleted(client.getEmail(), false);
    }

    if (Objects.nonNull(client.getPhoneNumber()) && !client.getPhoneNumber().isBlank()) {
      byPhone = clientRepository.findByPhoneNumberAndIsDeleted(client.getPhoneNumber(), false);
    }

    return Stream.concat(byEmail.stream(), byPhone.stream())
        .filter(c -> c.getId() != client.getId())
        .distinct()
        .map(it -> clearClientInfo(it, cleaner))
        .toList();
  }

  private Client clearClientInfo(Client client, ClientInfoCleaner cleaner) {
    if (cleaner.cleanName()) {
      client.setFirstname("");
      client.setLastname("");
      client.setPatronymic("");
    }
    if (cleaner.cleanContact()) {
      client.setEmail("");
      client.setPhoneNumber("");
    }
    return client;
  }
}
