package com.open.crm.core.entities.client;

import com.open.crm.core.entities.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Client entity - stored in tenant-specific schema Schema is determined by
 * TenantContext
 * at runtime
 */
@Entity
@Table(name = "clients")
@NoArgsConstructor
@Getter
@Setter
public class Client extends BaseEntity {
    private String firstname;

    private String lastname;

    private String patronymic;

    private String email;

    private String phoneNumber;

}
