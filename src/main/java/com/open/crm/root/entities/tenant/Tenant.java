package com.open.crm.root.entities.tenant;

import com.open.crm.root.entities.common.BaseRootEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tenants", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class Tenant extends BaseRootEntity {

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "is_ready", nullable = false)
    private boolean ready = false;

    @Column(name = "schema_name", nullable = false, unique = true)
    private String schemaName;

}
