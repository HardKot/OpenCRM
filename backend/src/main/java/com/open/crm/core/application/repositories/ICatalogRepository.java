package com.open.crm.core.application.repositories;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.catalog.Catalog;
import org.springframework.stereotype.Repository;

@Repository
public interface ICatalogRepository extends IRepository<Catalog> {
  boolean existsByCodeOrName(String code, String name);
}
