package com.open.crm.admin.entities.tenant;

import com.open.crm.admin.entities.common.BaseAdminEntity;
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
public class Tenant extends BaseAdminEntity {

  @Column(nullable = false)
  private Boolean active = true;

  @Column(name = "is_ready", nullable = false)
  private boolean ready = false;

  @Column(name = "schema_name", nullable = false, unique = true)
  private String schemaName;
}
