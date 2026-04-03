package com.open.crm.core.application.services;

import com.open.crm.core.application.investigation.events.*;
import com.open.crm.core.application.repositories.IClientRepository;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {
  private final IClientRepository clientRepository;
  private final ApplicationEventPublisher eventPublisher;

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
    eventPublisher.publishEvent(new CreateClientEvent(client, author));
    return client;
  }

  @Transactional
  public ResultApp<Client> updateClient(Client client, Author author, ClientInfoCleaner cleaner) {
    var existingOpt = clientRepository.findById(client.getId());
    if (existingOpt.isEmpty()) {
      return new ResultApp.NotFound<>();
    }
    Client existingClient = existingOpt.get();
    if (existingClient.isDeleted()) {
      return new ResultApp.InvalidData<>(
          "Cannot update a deleted client with ID: " + client.getId());
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
    eventPublisher.publishEvent(new UpdateClientEvent(updatedClient, author));
    return new ResultApp.Ok<>(clearClientInfo(updatedClient, cleaner));
  }

  @Transactional
  public ResultApp<Client> deleteClient(Client client, Author author, ClientInfoCleaner cleaner) {
    if (client.isDeleted()) {
      return new ResultApp.InvalidData<>(
          "Cannot delete a already deleted client with ID: " + client.getId());
    }
    client.setDeleted(true);
    Client deletedClient = clientRepository.save(client);
    eventPublisher.publishEvent(new DeleteClientEvent(deletedClient, author));
    return new ResultApp.Ok<>(clearClientInfo(deletedClient, cleaner));
  }

  @Transactional
  public ResultApp<Client> restoreClient(Client client, Author author, ClientInfoCleaner cleaner) {
    if (!client.isDeleted()) {
      return new ResultApp.InvalidData<>(
          "Cannot restore a non-deleted client with ID: " + client.getId());
    }
    client.setDeleted(false);
    Client restoredClient = clientRepository.save(client);
    eventPublisher.publishEvent(new RestoreClientEvent(restoredClient, author));
    return new ResultApp.Ok<>(clearClientInfo(restoredClient, cleaner));
  }

  @Transactional
  public ResultApp<Client> mergeClients(
      Client targetClient, Client[] sourceClients, Author author, ClientInfoCleaner cleaner) {
    if (targetClient.isDeleted()) {
      return new ResultApp.InvalidData<>(
          "Cannot merge into a deleted client with ID: " + targetClient.getId());
    }
    for (Client sourceClient : sourceClients) {
      var existingSourceOpt = clientRepository.findById(sourceClient.getId());
      if (existingSourceOpt.isEmpty()) {
        return new ResultApp.NotFound<>();
      }
      Client existingSourceClient = existingSourceOpt.get();
      if (existingSourceClient.isDeleted()) {
        return new ResultApp.InvalidData<>(
            "Cannot merge deleted client with ID: " + sourceClient.getId());
      }
      if (existingSourceClient.getId() == targetClient.getId()) {
        return new ResultApp.InvalidData<>(
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
    eventPublisher.publishEvent(new MergeClientEvent(mergedClient, sourceClients, author));
    return new ResultApp.Ok<>(mergedClient);
  }

  @Transactional
  public ResultApp<Client> manualUpdateClientBalance(
      Client client, long newBalance, Author author) {
    if (client.isDeleted()) {
      return new ResultApp.InvalidData<>(
          "Cannot update balance of a deleted client with ID: " + client.getId());
    }
    client.setBalance(newBalance);
    Client updatedClient = clientRepository.save(client);
    eventPublisher.publishEvent(new UpdateClientBalanceEvent(updatedClient, author));
    return new ResultApp.Ok<>(updatedClient);
  }

  public Optional<Client> getClientById(long id, boolean withDeleted, ClientInfoCleaner cleaner) {
    var clientOpt = clientRepository.findById(id);
    if (clientOpt.isEmpty()) {
      return Optional.empty();
    }
    Client client = clientOpt.get();
    if (client.isDeleted() && !withDeleted) {
      return Optional.empty();
    }
    return Optional.of(clearClientInfo(client, cleaner));
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
