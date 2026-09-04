package com.open.crm.core.application.services;

import com.open.crm.core.application.errors.CatalogException;
import com.open.crm.core.application.investigation.events.CreateCatalogEvent;
import com.open.crm.core.application.investigation.events.DeleteCatalog;
import com.open.crm.core.application.investigation.events.RestoreCatalog;
import com.open.crm.core.application.investigation.events.SaveCatalogItem;
import com.open.crm.core.application.investigation.events.UpdateCatalogEvent;
import com.open.crm.core.application.repositories.ICatalogDataRepository;
import com.open.crm.core.application.repositories.ICatalogRepository;
import com.open.crm.core.entities.catalog.Catalog;
import com.open.crm.core.entities.catalog.CatalogItem;
import com.open.crm.core.entities.catalog.CatalogItemValue;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogService {
  private final ICatalogRepository catalogRepository;
  private final ICatalogDataRepository catalogDataRepository;
  private final IGeneratorService generatorService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
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

  @Transactional
  public Catalog updateCatalog(Catalog catalog, Author author) {
    if (Objects.isNull(catalog.getId())) {
      throw new CatalogException("Catalog ID must not be null for update");
    }
    Optional<Catalog> existingCatalogOpt = catalogRepository.findById(catalog.getId());
    if (existingCatalogOpt.isEmpty()) {
      throw new CatalogException("Catalog not found with ID: " + catalog.getId());
    }
    Catalog existingCatalog = existingCatalogOpt.get();
    if (existingCatalog.isDeleted()) {
      throw new CatalogException("Cannot update a deleted catalog with ID: " + catalog.getId());
    }

    catalog.setDeleted(false);
    catalogRepository.save(catalog);
    eventPublisher.publishEvent(new UpdateCatalogEvent(catalog, author));
    return catalog;
  }

  @Transactional
  public Catalog deleteCatalog(Catalog catalog, Author author) {
    if (Objects.isNull(catalog.getId())) {
      throw new CatalogException("Catalog ID must not be null for deletion");
    }
    Optional<Catalog> existingCatalogOpt = catalogRepository.findById(catalog.getId());
    if (existingCatalogOpt.isEmpty()) {
      throw new CatalogException("Catalog not found with ID: " + catalog.getId());
    }

    catalog = existingCatalogOpt.get();
    if (catalog.isDeleted()) {
      throw new CatalogException("Catalog with ID: " + catalog.getId() + " is already deleted");
    }

    if (!catalog.isSoftDeleted() && catalogDataRepository.existsByCatalog(catalog)) {
      throw new CatalogException(
          "Cannot delete catalog with ID: "
              + catalog.getId()
              + " because it has associated data; Use force delete instead");
    }

    catalog.setDeleted(true);
    catalogRepository.save(catalog);
    catalogDataRepository.hiddenByCatalog(catalog);
    eventPublisher.publishEvent(new DeleteCatalog(catalog, author, false));

    return catalog;
  }

  @Transactional
  public Catalog forceDeleteCatalog(Catalog catalog, Author author) {
    if (Objects.isNull(catalog.getId())) {
      throw new CatalogException("Catalog ID must not be null for force deletion");
    }
    Optional<Catalog> existingCatalogOpt = catalogRepository.findById(catalog.getId());
    if (existingCatalogOpt.isEmpty()) {
      throw new CatalogException("Catalog not found with ID: " + catalog.getId());
    }

    catalog = existingCatalogOpt.get();
    if (!catalog.isDeleted()) {
      throw new CatalogException(
          "Catalog with ID: " + catalog.getId() + " must be deleted before force deletion");
    }
    catalogDataRepository.forceDeleteByCatalog(catalog);
    catalogRepository.delete(catalog);
    eventPublisher.publishEvent(new DeleteCatalog(catalog, author, true));
    return catalog;
  }

  @Transactional
  public Catalog restoreCatalog(Catalog catalog, Author author) {
    if (Objects.isNull(catalog.getId())) {
      throw new CatalogException("Catalog ID must not be null for restoration");
    }
    Optional<Catalog> existingCatalogOpt = catalogRepository.findById(catalog.getId());
    if (existingCatalogOpt.isEmpty()) {
      throw new CatalogException("Catalog not found with ID: " + catalog.getId());
    }
    catalog = existingCatalogOpt.get();
    if (!catalog.isDeleted()) {
      throw new CatalogException("Catalog with ID: " + catalog.getId() + " is not deleted");
    }

    catalog.setDeleted(false);
    catalogRepository.save(catalog);
    eventPublisher.publishEvent(new RestoreCatalog(catalog, author));
    return catalog;
  }

  @Transactional
  public CatalogItem saveCatalogItem(
      CatalogItemValue catalogItemValue, CatalogItem catalogItem, Author author) {
    if (Objects.isNull(catalogItemValue.getCatalog())
        || Objects.isNull(catalogItemValue.getCatalog().getId())) {
      throw new CatalogException("Catalog must not be null for saving catalog item");
    }
    Optional<Catalog> existingCatalogOpt =
        catalogRepository.findById(catalogItemValue.getCatalog().getId());
    if (existingCatalogOpt.isEmpty()) {
      throw new CatalogException(
          "Catalog not found with ID: " + catalogItemValue.getCatalog().getId());
    }
    Catalog existingCatalog = existingCatalogOpt.get();
    if (existingCatalog.isDeleted()) {
      throw new CatalogException(
          "Cannot add item to a deleted catalog with ID: " + existingCatalog.getId());
    }

    catalogItem.setId(null);
    catalogItem.setCreatedAt(null);
    catalogItem.setUpdatedAt(null);
    catalogItem.setCatalog(existingCatalog);
    catalogItem = catalogDataRepository.save(catalogItem);
    eventPublisher.publishEvent(new SaveCatalogItem(existingCatalog, catalogItem, author));

    return catalogItem;
  }

  private void generateCatalogCodeIfNotExists(Catalog catalog) {
    if (Objects.nonNull(catalog.getCode()) && !catalog.getCode().isBlank()) return;
    String generatedCode = generatorService.generateUniqueCode();
    catalog.setCode(generatedCode);
  }
}
