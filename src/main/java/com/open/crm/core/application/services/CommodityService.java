package com.open.crm.core.application.services;

import com.open.crm.core.application.InvestigationLogCreator;
import com.open.crm.core.application.errors.CommodityException;
import com.open.crm.core.application.repositories.ICommodityCategoryRepository;
import com.open.crm.core.application.repositories.ICommodityRepository;
import com.open.crm.core.entities.commodity.Commodity;
import com.open.crm.core.entities.commodity.CommodityCategory;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommodityService {
  private final ICommodityRepository commodityRepository;
  private final ICommodityCategoryRepository commodityCategoryRepository;
  private final InvestigationLogService investigationLogService;
  private final InvestigationLogCreator investigationLogCreator;

  @Transactional
  public CommodityCategory createCategory(CommodityCategory category, Author author)
      throws CommodityException {
    category.setId(null);
    category.setCommodities(List.of());
    category.setSubCategories(List.of());

    if (commodityCategoryRepository.existsByName(category.getName())) {
      throw new CommodityException("Category with the same name already exists");
    }

    if (Objects.nonNull(category.getParentCategory())) {
      CommodityCategory parenCategory =
          commodityCategoryRepository
              .findById(category.getParentCategory().getId())
              .orElseThrow(() -> new CommodityException("Parent category not found"));
      if (parenCategory.isDeleted()) {
        throw new CommodityException("Parent category is deleted");
      }
      category.setParentCategory(parenCategory);
    }

    category = commodityCategoryRepository.save(category);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityCategoryCreationLog(category, author));

    return category;
  }

  @Transactional
  public CommodityCategory updateCategory(CommodityCategory category, Author author)
      throws CommodityException {
    CommodityCategory existingCategory =
        commodityCategoryRepository
            .findById(category.getId())
            .orElseThrow(() -> new CommodityException("Category not found"));

    if (existingCategory.isDeleted()) {
      throw new CommodityException("Cannot update a deleted category");
    }

    if (!existingCategory.getName().equals(category.getName())
        && commodityCategoryRepository.existsByName(category.getName())) {
      throw new CommodityException("Category with the same name already exists");
    }

    existingCategory.setName(category.getName());
    existingCategory.setDescription(category.getDescription());

    if (Objects.nonNull(category.getParentCategory())) {
      CommodityCategory parentCategory =
          commodityCategoryRepository
              .findById(category.getParentCategory().getId())
              .orElseThrow(() -> new CommodityException("Parent category not found"));
      if (parentCategory.isDeleted()) {
        throw new CommodityException("Parent category is deleted");
      }
      existingCategory.setParentCategory(parentCategory);
    } else {
      existingCategory.setParentCategory(null);
    }

    if (recursiveDetectCycle(existingCategory, existingCategory.getParentCategory())) {
      throw new CommodityException("Cannot set parent category: cycle detected");
    }

    existingCategory = commodityCategoryRepository.save(existingCategory);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityCategoryUpdateLog(existingCategory, author));

    return existingCategory;
  }

  private boolean recursiveDetectCycle(CommodityCategory category, CommodityCategory parent) {
    if (parent == null) {
      return false;
    }
    if (parent.getId().equals(category.getId())) {
      return true;
    }
    return recursiveDetectCycle(category, parent.getParentCategory());
  }

  @Transactional
  public CommodityCategory deleteCategory(CommodityCategory category, Author author)
      throws CommodityException {
    CommodityCategory existsCategory =
        commodityCategoryRepository
            .findById(category.getId())
            .orElseThrow(() -> new CommodityException("Category not found"));

    if (existsCategory.isDeleted()) {
      throw new CommodityException("Category is already deleted");
    }

    existsCategory.softDelete();
    existsCategory = commodityCategoryRepository.save(existsCategory);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityCategoryUpdateLog(existsCategory, author));

    return existsCategory;
  }

  @Transactional
  public CommodityCategory restoreCategory(CommodityCategory category, Author author)
      throws CommodityException {
    CommodityCategory existsCategory =
        commodityCategoryRepository
            .findById(category.getId())
            .orElseThrow(() -> new CommodityException("Category not found"));

    if (!existsCategory.isDeleted()) {
      throw new CommodityException("Category is not deleted");
    }

    existsCategory.restore();
    existsCategory = commodityCategoryRepository.save(existsCategory);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityCategoryUpdateLog(existsCategory, author));

    return existsCategory;
  }

  @Transactional
  public Commodity createCommodity(Commodity commodity, Author author) throws CommodityException {
    commodity.setId(null);

    if (commodityRepository.existsByName(commodity.getName())) {
      throw new CommodityException("Commodity with the same name already exists");
    }

    if (Objects.nonNull(commodity.getCategory())) {
      CommodityCategory category =
          commodityCategoryRepository
              .findById(commodity.getCategory().getId())
              .orElseThrow(() -> new CommodityException("Category not found"));
      if (category.isDeleted()) {
        throw new CommodityException("Category is deleted");
      }
      commodity.setCategory(category);
    }

    commodity = commodityRepository.save(commodity);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityCreationLog(commodity, author));

    return commodity;
  }

  @Transactional
  public Commodity updateCommodity(Commodity commodity, Author author) throws CommodityException {
    Commodity existingCommodity =
        commodityRepository
            .findById(commodity.getId())
            .orElseThrow(() -> new CommodityException("Commodity not found"));

    if (existingCommodity.isDeleted()) {
      throw new CommodityException("Cannot update a deleted commodity");
    }

    if (!existingCommodity.getName().equals(commodity.getName())
        && commodityRepository.existsByName(commodity.getName())) {
      throw new CommodityException("Commodity with the same name already exists");
    }

    existingCommodity.setName(commodity.getName());
    existingCommodity.setDescription(commodity.getDescription());
    existingCommodity.setCost(commodity.getCost());

    if (Objects.nonNull(commodity.getCategory())) {
      CommodityCategory category =
          commodityCategoryRepository
              .findById(commodity.getCategory().getId())
              .orElseThrow(() -> new CommodityException("Category not found"));
      if (category.isDeleted()) {
        throw new CommodityException("Category is deleted");
      }
      existingCommodity.setCategory(category);
    } else {
      existingCommodity.setCategory(null);
    }

    existingCommodity = commodityRepository.save(existingCommodity);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityUpdateLog(existingCommodity, author));
    return existingCommodity;
  }

  @Transactional
  public Commodity deleteCommodity(Commodity commodity, Author author) throws CommodityException {
    Commodity existingCommodity =
        commodityRepository
            .findById(commodity.getId())
            .orElseThrow(() -> new CommodityException("Commodity not found"));

    if (existingCommodity.isDeleted()) {
      throw new CommodityException("Commodity is already deleted");
    }
    existingCommodity.softDelete();
    existingCommodity = commodityRepository.save(existingCommodity);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityUpdateLog(existingCommodity, author));
    return existingCommodity;
  }

  @Transactional
  public Commodity restoreCommodity(Commodity commodity, Author author) throws CommodityException {
    Commodity existingCommodity =
        commodityRepository
            .findById(commodity.getId())
            .orElseThrow(() -> new CommodityException("Commodity not found"));
    if (!existingCommodity.isDeleted()) {
      throw new CommodityException("Commodity is not deleted");
    }
    existingCommodity.restore();
    existingCommodity = commodityRepository.save(existingCommodity);
    investigationLogService.saveLog(
        investigationLogCreator.createCommodityUpdateLog(existingCommodity, author));
    return existingCommodity;
  }
}
