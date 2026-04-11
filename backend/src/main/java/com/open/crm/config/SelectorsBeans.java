package com.open.crm.config;

import com.open.crm.core.application.repositories.IClientRepository;
import com.open.crm.core.application.repositories.ICommodityCategoryRepository;
import com.open.crm.core.application.repositories.ICommodityRepository;
import com.open.crm.core.application.services.SelectorData;
import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.commodity.Commodity;
import com.open.crm.core.entities.commodity.CommodityCategory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SelectorsBeans {
  @Bean("clientSelectorData")
  public SelectorData<Client> clientSelectorData(IClientRepository clientRepository) {
    return new SelectorData<>(clientRepository);
  }

  @Bean("commoditySelectorData")
  public SelectorData<Commodity> commoditySelectorData(ICommodityRepository commodityRepository) {
    return new SelectorData<>(commodityRepository);
  }

  @Bean("commodityCategorySelectorData")
  public SelectorData<CommodityCategory> commodityCategorySelectorData(
      ICommodityCategoryRepository commodityCategoryRepository) {
    return new SelectorData<>(commodityCategoryRepository);
  }
}
