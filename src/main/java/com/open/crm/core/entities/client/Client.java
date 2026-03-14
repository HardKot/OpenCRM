package com.open.crm.core.entities.client;

import java.util.Objects;

import com.open.crm.core.entities.common.BaseEntity;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
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

    public Client merge(Client other) {
        if (nameIsEmpty(firstname))
            this.firstname = other.getFirstname();
        if (nameIsEmpty(lastname))
            this.lastname = other.getLastname();
        if (nameIsEmpty(patronymic))
            this.patronymic = other.getPatronymic();
        if (nameIsEmpty(email))
            this.email = other.getEmail();
        if (nameIsEmpty(phoneNumber))
            this.phoneNumber = other.getPhoneNumber();
        return this;
    }

    private boolean nameIsEmpty(@Nullable String name) {
        return Objects.isNull(name) || name.isBlank();
    }
}
