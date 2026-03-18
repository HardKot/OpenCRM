package com.open.crm.apiControllers;

import com.open.crm.apiControllers.dto.ApplicationErrorDto;
import com.open.crm.components.services.SessionService;
import com.open.crm.core.application.errors.ClientException;
import com.open.crm.core.application.services.ClientService;
import com.open.crm.core.entities.client.Client;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
  public Client actionGetClient(@PathVariable("id") long id) {
    return clientService.getClientById(
        id, sessionEmployeeService.isShowDeleted(), sessionEmployeeService.getClientInfoCleaner());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public Client actionUpdateClient(@PathVariable("id") long id, @RequestBody Client data) {
    data.setId(id);
    Client entity =
        clientService.updateClient(
            data,
            sessionEmployeeService.getAuthor(),
            sessionEmployeeService.getClientInfoCleaner());

    if (Objects.nonNull(data.getBalance()) && data.getBalance() != entity.getBalance()) {
      entity =
          clientService.manualUpdateClientBalance(
              entity, data.getBalance(), sessionEmployeeService.getAuthor());
    }

    return entity;
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public Client actionDeleteClient(@PathVariable("id") long id) {
    Client entity =
        clientService.getClientById(id, true, sessionEmployeeService.getClientInfoCleaner());
    clientService.deleteClient(
        entity, sessionEmployeeService.getAuthor(), sessionEmployeeService.getClientInfoCleaner());

    return entity;
  }

  @PostMapping("/{id}")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public Client actionRestoreClient(@PathVariable("id") long id) {
    Client entity =
        clientService.getClientById(id, true, sessionEmployeeService.getClientInfoCleaner());
    clientService.restoreClient(
        entity, sessionEmployeeService.getAuthor(), sessionEmployeeService.getClientInfoCleaner());

    return entity;
  }

  @PutMapping("/{id}/merge")
  @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
  public Client actionMergeClient(@PathVariable("id") long id, @RequestBody Long[] ids) {
    Client targetClient =
        clientService.getClientById(id, false, sessionEmployeeService.getClientInfoCleaner());
    Client[] sourceClients =
        Arrays.stream(ids)
            .map(
                sourceId ->
                    clientService.getClientById(
                        sourceId, false, sessionEmployeeService.getClientInfoCleaner()))
            .toArray(Client[]::new);

    targetClient =
        clientService.mergeClients(
            targetClient,
            sourceClients,
            sessionEmployeeService.getAuthor(),
            sessionEmployeeService.getClientInfoCleaner());

    return targetClient;
  }

  @GetMapping("/{id}/duplicate")
  @PreAuthorize("hasPermission(null, 'CLIENT_READ')")
  public Client[] getMethodName(@PathVariable("id") long id) {
    Client client =
        clientService.getClientById(id, false, sessionEmployeeService.getClientInfoCleaner());

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
