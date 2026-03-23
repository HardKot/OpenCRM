package com.open.crm.core.application.services;

import com.open.crm.core.application.investigation.events.*;
import com.open.crm.core.application.repositories.ICommodityCategoryRepository;
import com.open.crm.core.application.repositories.ICommodityRepository;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.entities.commodity.Commodity;
import com.open.crm.core.entities.commodity.CommodityCategory;
import com.open.crm.core.entities.investigationLog.Author;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommodityService {
  private final ICommodityRepository commodityRepository;
  private final ICommodityCategoryRepository commodityCategoryRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public ResultApp<CommodityCategory> createCategory(CommodityCategory category, Author author) {
    category.setId(null);
    category.setCommodities(List.of());
    category.setSubCategories(List.of());

    if (commodityCategoryRepository.existsByName(category.getName())) {
      return new ResultApp.InvalidData<>("Category with the same name already exists");
    }

    if (Objects.nonNull(category.getParentCategory())) {
      var parentOpt = commodityCategoryRepository.findById(category.getParentCategory().getId());
      if (parentOpt.isEmpty()) {
        return new ResultApp.NotFound<>();
      }
      CommodityCategory parenCategory = parentOpt.get();
      if (parenCategory.isDeleted()) {
        return new ResultApp.InvalidData<>("Parent category is deleted");
      }
      category.setParentCategory(parenCategory);
    }

    category = commodityCategoryRepository.save(category);
    eventPublisher.publishEvent(new CreateCommodityCategoryEvent(category, author));
    return new ResultApp.Ok<>(category);
  }

  @Transactional
  public ResultApp<CommodityCategory> updateCategory(CommodityCategory category, Author author) {
    var existingOpt = commodityCategoryRepository.findById(category.getId());
    if (existingOpt.isEmpty()) {
      return new ResultApp.NotFound<>();
    }
    CommodityCategory existingCategory = existingOpt.get();

    if (existingCategory.isDeleted()) {
      return new ResultApp.InvalidData<>("Cannot update a deleted category");
    }

    if (!existingCategory.getName().equals(category.getName())
        && commodityCategoryRepository.existsByName(category.getName())) {
      return new ResultApp.InvalidData<>("Category with the same name already exists");
    }

    existingCategory.setName(category.getName());
    existingCategory.setDescription(category.getDescription());

    if (Objects.nonNull(category.getParentCategory())) {
      var parentOpt = commodityCategoryRepository.findById(category.getParentCategory().getId());
      if (parentOpt.isEmpty()) {
        return new ResultApp.NotFound<>();
      }
      CommodityCategory parentCategory = parentOpt.get();
      if (parentCategory.isDeleted()) {
        return new ResultApp.InvalidData<>("Parent category is deleted");
      }
      existingCategory.setParentCategory(parentCategory);
    } else {
      existingCategory.setParentCategory(null);
    }

    if (recursiveDetectCycle(existingCategory, existingCategory.getParentCategory())) {
      return new ResultApp.InvalidData<>("Cannot set parent category: cycle detected");
    }

    existingCategory = commodityCategoryRepository.save(existingCategory);
    eventPublisher.publishEvent(new UpdateCommodityCategoryEvent(existingCategory, author));
    return new ResultApp.Ok<>(existingCategory);
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
  public ResultApp<CommodityCategory> deleteCategory(CommodityCategory category, Author author) {
    var existsOpt = commodityCategoryRepository.findById(category.getId());
    if (existsOpt.isEmpty()) {
      return new ResultApp.NotFound<>();
    }
    CommodityCategory existsCategory = existsOpt.get();

    if (existsCategory.isDeleted()) {
      return new ResultApp.InvalidData<>("Category is already deleted");
    }

    existsCategory.softDelete();
    existsCategory = commodityCategoryRepository.save(existsCategory);
    eventPublisher.publishEvent(new DeleteCommodityCategoryEvent(existsCategory, author));
    return new ResultApp.Ok<>(existsCategory);
  }

  @Transactional
  public ResultApp<CommodityCategory> restoreCategory(CommodityCategory category, Author author) {
    var existsOpt = commodityCategoryRepository.findById(category.getId());
    if (existsOpt.isEmpty()) {
      return new ResultApp.NotFound<>();
    }
    CommodityCategory existsCategory = existsOpt.get();

    if (!existsCategory.isDeleted()) {
      return new ResultApp.InvalidData<>("Category is not deleted");
    }

    existsCategory.restore();
    existsCategory = commodityCategoryRepository.save(existsCategory);
    eventPublisher.publishEvent(new RestoreCommodityCategoryEvent(existsCategory, author));
    return new ResultApp.Ok<>(existsCategory);
  }

  @Transactional
  public ResultApp<Commodity> createCommodity(Commodity commodity, Author author) {
    commodity.setId(null);

    if (commodityRepository.existsByName(commodity.getName())) {
      return new ResultApp.InvalidData<>("Commodity with the same name already exists");
    }

    if (Objects.nonNull(commodity.getCategory())) {
      var categoryOpt = commodityCategoryRepository.findById(commodity.getCategory().getId());
      if (categoryOpt.isEmpty()) {
        return new ResultApp.NotFound<>();
      }
      CommodityCategory category = categoryOpt.get();
      if (category.isDeleted()) {
        return new ResultApp.InvalidData<>("Category is deleted");
      }
      commodity.setCategory(category);
    }

    commodity = commodityRepository.save(commodity);
    eventPublisher.publishEvent(new CreateCommodityEvent(commodity, author));
    return new ResultApp.Ok<>(commodity);
  }

  @Transactional
  public ResultApp<Commodity> updateCommodity(Commodity commodity, Author author) {
    var existingOpt = commodityRepository.findById(commodity.getId());
    if (existingOpt.isEmpty()) {
      return new ResultApp.NotFound<>();
    }
    Commodity existingCommodity = existingOpt.get();

    if (existingCommodity.isDeleted()) {
      return new ResultApp.InvalidData<>("Cannot update a deleted commodity");
    }

    if (!existingCommodity.getName().equals(commodity.getName())
        && commodityRepository.existsByName(commodity.getName())) {
      return new ResultApp.InvalidData<>("Commodity with the same name already exists");
    }

    existingCommodity.setName(commodity.getName());
    existingCommodity.setDescription(commodity.getDescription());
    existingCommodity.setCost(commodity.getCost());

    if (Objects.nonNull(commodity.getCategory())) {
      var categoryOpt = commodityCategoryRepository.findById(commodity.getCategory().getId());
      if (categoryOpt.isEmpty()) {
        return new ResultApp.NotFound<>();
      }
      CommodityCategory category = categoryOpt.get();
      if (category.isDeleted()) {
        return new ResultApp.InvalidData<>("Category is deleted");
      }
      existingCommodity.setCategory(category);
    } else {
      existingCommodity.setCategory(null);
    }

    existingCommodity = commodityRepository.save(existingCommodity);
    eventPublisher.publishEvent(new UpdateCommodityEvent(existingCommodity, author));
    return new ResultApp.Ok<>(existingCommodity);
  }

  @Transactional
  public ResultApp<Commodity> deleteCommodity(Commodity commodity, Author author) {
    var existingOpt = commodityRepository.findById(commodity.getId());
    if (existingOpt.isEmpty()) {
      return new ResultApp.NotFound<>();
    }
    Commodity existingCommodity = existingOpt.get();

    if (existingCommodity.isDeleted()) {
      return new ResultApp.InvalidData<>("Commodity is already deleted");
    }
    existingCommodity.softDelete();
    existingCommodity = commodityRepository.save(existingCommodity);
    eventPublisher.publishEvent(new DeleteCommodityEvent(existingCommodity, author));
    return new ResultApp.Ok<>(existingCommodity);
  }

  @Transactional
  public ResultApp<Commodity> restoreCommodity(Commodity commodity, Author author) {
    var existingOpt = commodityRepository.findById(commodity.getId());
    if (existingOpt.isEmpty()) {
      return new ResultApp.NotFound<>();
    }
    Commodity existingCommodity = existingOpt.get();
    if (!existingCommodity.isDeleted()) {
      return new ResultApp.InvalidData<>("Commodity is not deleted");
    }
    existingCommodity.restore();
    existingCommodity = commodityRepository.save(existingCommodity);
    eventPublisher.publishEvent(new RestoreCommodityEvent(existingCommodity, author));
    return new ResultApp.Ok<>(existingCommodity);
  }
}
