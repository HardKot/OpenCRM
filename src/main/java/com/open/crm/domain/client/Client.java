package com.open.crm.domain.client;

import java.util.UUID;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Client entity - stored in tenant-specific schema
 * Schema is determined by TenantContext at runtime
 */
@Entity
@Table(name = "clients")
@NoArgsConstructor
@Getter
@Setter             
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String patronymic;

    @TenantId
    private UUID tenantId;

    private String email;
    private String phoneNumber;
}
