package com.open.crm.controllers;

import com.open.crm.components.services.SessionService;
import com.open.crm.core.application.errors.ClientException;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.application.services.ClientService;
import com.open.crm.core.entities.client.Client;
import com.open.crm.dto.ApplicationErrorDto;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
  private final ClientService clientService;
  private final SessionService sessionEmployeeService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public Client actionCreateClient(@RequestBody Client client) {

    return clientService.createClient(
        client, sessionEmployeeService.getAuthor(), sessionEmployeeService.getClientInfoCleaner());
  }

  @GetMapping
  @PreAuthorize("hasPermission(null, 'CLIENT_READ')")
  public List<Client> actionGetClients(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "100") int size) {

    return clientService.getClients(
        page - 1,
        size,
        sessionEmployeeService.isShowDeleted(),
        sessionEmployeeService.getClientInfoCleaner());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'CLIENT_READ')")
  public ResponseEntity<?> actionGetClient(@PathVariable("id") long id) {
    Optional<Client> result =
        clientService.getClientById(
            id,
            sessionEmployeeService.isShowDeleted(),
            sessionEmployeeService.getClientInfoCleaner());
    if (result.isPresent()) {
      return ResponseEntity.ok(result.get());
    } else {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public ResponseEntity<?> actionUpdateClient(
      @PathVariable("id") long id, @RequestBody Client data) {
    data.setId(id);
    ResultApp<Client> result =
        clientService.updateClient(
            data,
            sessionEmployeeService.getAuthor(),
            sessionEmployeeService.getClientInfoCleaner());
    if (result instanceof ResultApp.Ok<Client> ok) {
      Client entity = ok.value();
      if (Objects.nonNull(data.getBalance()) && data.getBalance() != entity.getBalance()) {
        var balanceResult =
            clientService.manualUpdateClientBalance(
                entity, data.getBalance(), sessionEmployeeService.getAuthor());
        if (balanceResult instanceof ResultApp.Ok<Client> okBalance) {
          entity = okBalance.value();
        } else if (balanceResult instanceof ResultApp.InvalidData invalid) {
          return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
        } else if (balanceResult instanceof ResultApp.NotFound) {
          return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
        } else {
          return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
        }
      }
      return ResponseEntity.ok(entity);
    } else if (result instanceof ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public ResponseEntity<?> actionDeleteClient(@PathVariable("id") long id) {
    Optional<Client> getResult =
        clientService.getClientById(id, true, sessionEmployeeService.getClientInfoCleaner());
    if (!getResult.isPresent()) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    }
    var result =
        clientService.deleteClient(
            getResult.get(),
            sessionEmployeeService.getAuthor(),
            sessionEmployeeService.getClientInfoCleaner());
    if (result instanceof ResultApp.Ok okDel) {
      return ResponseEntity.ok(okDel.value());
    } else if (result instanceof ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PostMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public ResponseEntity<?> actionRestoreClient(@PathVariable("id") long id) {
    var getResult =
        clientService.getClientById(id, true, sessionEmployeeService.getClientInfoCleaner());
    if (!getResult.isPresent()) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    }
    var result =
        clientService.restoreClient(
            getResult.get(),
            sessionEmployeeService.getAuthor(),
            sessionEmployeeService.getClientInfoCleaner());
    if (result instanceof ResultApp.Ok<Client> okRestore) {
      return ResponseEntity.ok(okRestore.value());
    } else if (result instanceof ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @PutMapping("/{id}/merge")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public ResponseEntity<?> actionMergeClient(@PathVariable("id") long id, @RequestBody Long[] ids) {
    Optional<Client> targetResult =
        clientService.getClientById(id, false, sessionEmployeeService.getClientInfoCleaner());
    if (!targetResult.isPresent()) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    }
    Client[] sourceClients =
        Arrays.stream(ids)
            .<Optional<Client>>map(
                sourceId ->
                    clientService.getClientById(
                        sourceId, false, sessionEmployeeService.getClientInfoCleaner()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Client[]::new);
    var result =
        clientService.mergeClients(
            targetResult.get(),
            sourceClients,
            sessionEmployeeService.getAuthor(),
            sessionEmployeeService.getClientInfoCleaner());
    if (result instanceof ResultApp.Ok<Client> okMerge) {
      return ResponseEntity.ok(okMerge.value());
    } else if (result instanceof ResultApp.InvalidData invalid) {
      return ResponseEntity.badRequest().body(new ApplicationErrorDto(invalid.message()));
    } else if (result instanceof ResultApp.NotFound) {
      return ResponseEntity.status(404).body(new ApplicationErrorDto("Not found"));
    } else {
      return ResponseEntity.status(500).body(new ApplicationErrorDto("Unknown error"));
    }
  }

  @GetMapping("/{id}/duplicate")
  @PreAuthorize("hasPermission(null, 'CLIENT_READ')")
  public Client[] getMethodName(@PathVariable("id") long id) {
    Optional<Client> clientResult =
        clientService.getClientById(id, false, sessionEmployeeService.getClientInfoCleaner());
    Client client = clientResult.orElseThrow(() -> new RuntimeException("Client not found"));

    List<Client> duplicates =
        clientService.getDuplicateClients(client, sessionEmployeeService.getClientInfoCleaner());
    return duplicates.toArray(new Client[0]);
  }

  @ExceptionHandler({ClientException.class})
  public ResponseEntity<ApplicationErrorDto> handleClientException(ClientException ex) {
    ApplicationErrorDto error = new ApplicationErrorDto(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}
