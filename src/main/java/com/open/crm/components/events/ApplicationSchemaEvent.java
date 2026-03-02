package com.open.crm.components.events;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ApplicationSchemaEvent extends ApplicationEvent {
    private final String schemaName;
    private final UUID tenantId;

    public ApplicationSchemaEvent(Object source, String schemaName, UUID tenantId) {
        super(source);
        this.schemaName = schemaName;
        this.tenantId = tenantId;
    }
    
}
