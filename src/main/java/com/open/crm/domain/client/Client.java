package com.open.crm.domain.client;

import java.util.UUID;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@NoArgsConstructor
@Getter
@Setter             
public class Client {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String patronymic;

    @TenantId
    private UUID tenantId;

    private String email;
    private String phoneNumber;
}
