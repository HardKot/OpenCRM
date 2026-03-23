package com.open.crm.core.application.investigation.events;

import com.open.crm.core.entities.commodity.CommodityCategory;
import com.open.crm.core.entities.investigationLog.Author;

public record CreateCommodityCategoryEvent(CommodityCategory category, Author author) {}
