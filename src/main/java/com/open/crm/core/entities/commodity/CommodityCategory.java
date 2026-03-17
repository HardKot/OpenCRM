package com.open.crm.core.entities.commodity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.open.crm.core.application.errors.CommodityException;
import com.open.crm.core.entities.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "commodity_categories")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class CommodityCategory extends BaseEntity {
  public static final String BLOCK_SYMBOLS = "<>;\\[\\]\\{}";

  private String name;
  private String description;

  @Column(name = "sort_order")
  private int sortOrder = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_category_id")
  private CommodityCategory parentCategory;

  @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
  private List<CommodityCategory> subCategories;

  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
  private List<Commodity> commodities;

  public void setName(String name) {
    if (name.toLowerCase().matches("[" + BLOCK_SYMBOLS + "]+")) {
      throw new CommodityException(
          "Category name cannot contain special characters like <, >, ;, [, ], {, }");
    }
    name = name.trim().replaceAll("\\s+", " ");

    this.name = name;
  }

  public void setDescription(String description) {
    if (description != null && description.matches(BLOCK_SYMBOLS)) {
      throw new CommodityException(
          "Category description cannot contain special characters like <, >, ;, [, ], {, }");
    }
    this.description = description;
  }

  public void setSubCategories(List<CommodityCategory> subCategories) {
    if (subCategories.size() > 100) {
      throw new CommodityException("A category cannot have more than 100 subcategories");
    }

    for (CommodityCategory subCategory : subCategories) {
      if (Objects.equals(subCategory.getId(), this.getId())) {
        throw new CommodityException("A category cannot be a subcategory of itself");
      }
    }

    this.subCategories = subCategories;
  }

  public void setCommodities(List<Commodity> commodities) {
    if (commodities.size() > 1000) {
      throw new CommodityException("A category cannot have more than 1000 commodities");
    }
    this.commodities = commodities;
  }

  public void setParentCategory(CommodityCategory parentCategory) {
    if (Objects.nonNull(parentCategory) && Objects.equals(parentCategory.getId(), this.getId())) {
      throw new CommodityException("A category cannot be a parent of itself");
    }
    this.parentCategory = parentCategory;
  }

  @Override
  public void softDelete() {
    super.setDeleted(true);
    for (CommodityCategory subCategory : subCategories) {
      subCategory.softDelete();
    }
    for (Commodity commodity : commodities) {
      commodity.softDelete();
    }
  }

  @Override
  public void restore() {
    super.setDeleted(false);
    if (Objects.nonNull(parentCategory)) {
      parentCategory.restore();
    }
  }
}
