package com.open.crm.core.application.repositories;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.commodity.CommodityCategory;

public interface ICommodityCategoryRepository extends IRepository<CommodityCategory> {
  boolean existsByName(String name);
}
