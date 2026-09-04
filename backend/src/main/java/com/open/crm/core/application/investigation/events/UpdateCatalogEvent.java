package com.open.crm.core.application.investigation.events;

import com.open.crm.core.entities.catalog.Catalog;
import com.open.crm.core.entities.investigationLog.Author;

public record UpdateCatalogEvent(Catalog catalog, Author author) {}
