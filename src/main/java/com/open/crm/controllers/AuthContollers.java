package com.open.crm.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.crm.controllers.dto.RegisterTenantRequest;
import com.open.crm.controllers.dto.RegisterTenantResponse;
import com.open.crm.tenancy.TenantService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthContollers {
    private final TenantService tenantService;


    
    
    @PostMapping("/register/tenant")
    public ResponseEntity<RegisterTenantResponse> registerTenant(@RequestBody RegisterTenantRequest request) {
        try {
            tenantService.createTenant(request.email());
            return ResponseEntity.ok(new RegisterTenantResponse(true, "Tenant registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new RegisterTenantResponse(false, e.getMessage()));
        }
    } 
}
