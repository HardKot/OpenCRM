package com.open.crm.core.application.repositories;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.catalog.Catalog;
import com.open.crm.core.entities.catalog.CatalogItem;
import java.util.List;
import org.springframework.data.domain.PageRequest;

public interface ICatalogDataRepository extends IRepository<CatalogItem> {
  List<CatalogItem> findByCode(String code, PageRequest pageRequest);

  boolean existsByCatalog(Catalog catalog);

  void hiddenByCatalog(Catalog catalog);

  void forceDeleteByCatalog(Catalog catalog);
}
