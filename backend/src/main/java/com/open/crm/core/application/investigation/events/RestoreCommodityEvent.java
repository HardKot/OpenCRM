package com.open.crm.core.application.investigation.events;

import com.open.crm.core.entities.commodity.Commodity;
import com.open.crm.core.entities.investigationLog.Author;

public record RestoreCommodityEvent(Commodity commodity, Author author) {}
