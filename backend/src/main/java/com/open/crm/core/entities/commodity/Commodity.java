package com.open.crm.core.entities.commodity;

import com.open.crm.core.entities.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name = "commodities")
@Getter
@Setter
public class Commodity extends BaseEntity {
  private String name;
  private String description;

  private int cost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private CommodityCategory category;
}
