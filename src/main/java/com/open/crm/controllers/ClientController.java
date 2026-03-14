package com.open.crm.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.crm.components.services.SessionEmployeeService;
import com.open.crm.core.application.services.ClientService;
import com.open.crm.core.entities.client.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final SessionEmployeeService sessionEmployeeService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'CLIENT_UPDATE')")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return null;
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'CLIENT_READ')")
    public ResponseEntity<List<Client>> getMethodName(@RequestParam String param) {
        return null;
    }

}
