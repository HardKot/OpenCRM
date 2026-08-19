package com.open.crm.core.entities.catalog;

import com.open.crm.core.entities.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "catalog")
public class Catalog extends BaseEntity {
  @NotBlank @NotNull private String name = "";
  private String description = "";
  @NotNull @NotBlank private String code = "";

  private boolean softDeleted = true;
  private boolean readOnly = false;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
  private Set<CatalogField> fields = Set.of();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "items")
  private Set<CatalogItem> items = Set.of();
}
