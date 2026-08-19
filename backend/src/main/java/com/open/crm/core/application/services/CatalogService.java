package com.open.crm.core.application.services;

import com.open.crm.core.application.errors.CatalogException;
import com.open.crm.core.application.investigation.events.CreateCatalogEvent;
import com.open.crm.core.application.repositories.ICatalogDataRepository;
import com.open.crm.core.application.repositories.ICatalogRepository;
import com.open.crm.core.entities.catalog.Catalog;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogService {
  private final ICatalogRepository catalogRepository;
  private final ICatalogDataRepository catalogDataRepository;
  private final IGeneratorService generatorService;
  private final ApplicationEventPublisher eventPublisher;

  public Catalog saveCatalog(Catalog catalog, Author author) {
    if (catalogRepository.existsByCodeOrName(catalog.getCode(), catalog.getName())) {
      throw new CatalogException("Catalog with the same code or name already exists");
    }

    catalog.setId(null);
    catalog.setCreatedAt(null);
    catalog.setUpdatedAt(null);
    catalog.setDeleted(false);
    catalog.setItems(Set.of());
    generateCatalogCodeIfNotExists(catalog);
    catalog = catalogRepository.save(catalog);

    eventPublisher.publishEvent(new CreateCatalogEvent(catalog, author));
    return catalog;
  }

  private void generateCatalogCodeIfNotExists(Catalog catalog) {
    if (Objects.nonNull(catalog.getCode()) && !catalog.getCode().isBlank()) return;
    String generatedCode = generatorService.generateUniqueCode();
    catalog.setCode(generatedCode);
  }
}
