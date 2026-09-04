package com.open.crm.core.entities.catalog;

import com.open.crm.core.entities.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "catalogItem")
public class CatalogItem extends BaseEntity {
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  private Catalog catalog;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "catalogItem")
  private Set<CatalogItemValue> values;
}
