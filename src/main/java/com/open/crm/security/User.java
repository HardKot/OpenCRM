package com.open.crm.security;

import java.util.UUID;

import com.open.crm.tenancy.Tenant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    private UUID id;

    private String email;
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Tenant tenant;

    // private long employeeId;
}
