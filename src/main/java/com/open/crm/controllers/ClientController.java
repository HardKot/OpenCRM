package com.open.crm.controllers;

import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.crm.components.services.SessionService;
import com.open.crm.core.application.services.ClientService;
import com.open.crm.core.entities.client.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

        return clientService.createClient(client, sessionEmployeeService.getAuthor(),
                sessionEmployeeService.getClientInfoCleaner());
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'CLIENT_READ')")
    public List<Client> actionGetClients(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "100") int size) {

        return clientService.getClients(page, size, sessionEmployeeService.isShowDeleted(),
                sessionEmployeeService.getClientInfoCleaner());
    }

    @GetMapping("/{id}")
    public Client actionGetClient(@PathVariable("id") long id) {
        return clientService.getClientById(id, sessionEmployeeService.isShowDeleted(),
                sessionEmployeeService.getClientInfoCleaner());
    }

    @PutMapping("/{id}")
    public Client actionUpdateClient(@PathVariable("id") long id, @RequestBody Client entity) {
        entity.setId(id);
        clientService.updateClient(entity, sessionEmployeeService.getAuthor(),
                sessionEmployeeService.getClientInfoCleaner());

        return entity;
    }

    @DeleteMapping("/{id}")
    public Client actionDeleteClient(@PathVariable("id") long id) {
        Client entity = clientService.getClientById(id, true, sessionEmployeeService.getClientInfoCleaner());
        clientService.deleteClient(entity, sessionEmployeeService.getAuthor(),
                sessionEmployeeService.getClientInfoCleaner());

        return entity;
    }

    @PostMapping("/{id}")
    public Client actionRestoreClient(@PathVariable("id") long id) {
        Client entity = clientService.getClientById(id, true, sessionEmployeeService.getClientInfoCleaner());
        clientService.restoreClient(entity, sessionEmployeeService.getAuthor(),
                sessionEmployeeService.getClientInfoCleaner());

        return entity;
    }

}
