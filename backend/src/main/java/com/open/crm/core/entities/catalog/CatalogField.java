package com.open.crm.core.entities.catalog;

import com.open.crm.core.entities.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "catalogField")
public class CatalogField extends BaseEntity {
  @NotBlank @NotNull private String name = "";
  private String description = "";
  private CatalogFieldType type;
  private String defaultValue;
  private boolean required;
  private boolean unique;
  private boolean searchable;
  private int order;
  private boolean readOnly;
  private Map<String, String> options;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "catalog_id")
  private Catalog catalog;

  public String getOption(String key) {
    if (Objects.isNull(key) || key.isEmpty() || Objects.isNull(options)) return null;
    return options.get(key);
  }
}
