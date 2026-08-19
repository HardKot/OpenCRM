package com.open.crm.core.application.services;

import com.open.crm.core.application.repositories.ICatalogRepository;
import com.open.crm.core.entities.catalog.Catalog;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogValidator {
  private final ICatalogRepository catalogRepository;

  public void validateCatalogCode(Catalog catalog) {
    if (Objects.isNull(catalog.getCode()) || catalog.getCode().isBlank()) {
      throw new IllegalArgumentException("Catalog code cannot be null or blank");
    }
  }

  public void validateCatalogName(Catalog catalog) {
    if (Objects.isNull(catalog.getName()) || catalog.getName().isBlank()) {
      throw new IllegalArgumentException("Catalog name cannot be null or blank");
    }
  }
}
