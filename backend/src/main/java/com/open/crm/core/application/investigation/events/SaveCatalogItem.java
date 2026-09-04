package com.open.crm.core.application.investigation.events;

import com.open.crm.core.entities.catalog.Catalog;
import com.open.crm.core.entities.catalog.CatalogItem;
import com.open.crm.core.entities.investigationLog.Author;

public record SaveCatalogItem(Catalog catalog, CatalogItem catalogItem, Author author) {}
