package com.open.crm.core.application.investigation.events;

import com.open.crm.core.entities.catalog.Catalog;
import com.open.crm.core.entities.investigationLog.Author;

public record DeleteCatalog(Catalog catalog, Author author, boolean forceDelete) {}
