package com.open.crm.core.application.investigation.events;

import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.investigationLog.Author;

public record UpdateClientEvent(Client client, Author author) {}
