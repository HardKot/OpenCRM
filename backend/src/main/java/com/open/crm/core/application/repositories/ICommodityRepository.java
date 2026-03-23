package com.open.crm.core.application.repositories;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.commodity.Commodity;

public interface ICommodityRepository extends IRepository<Commodity> {
  boolean existsByName(String name);
}
