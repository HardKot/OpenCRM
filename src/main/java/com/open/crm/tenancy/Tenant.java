package com.open.crm.tenancy;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "tenants", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private boolean isReady = false;


    public String getSchemaName() {
        return "tenant_" + id.toString().replace("-", "_");
    }
}
