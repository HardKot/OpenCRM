package com.open.crm.components.events;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import com.open.crm.security.User;

import lombok.Getter;

@Getter
public class ApplicationSchemaEvent extends ApplicationEvent {
    private final String schemaName;
    private final User user;
    private final UUID tenantId;

    public ApplicationSchemaEvent(Object source, String schemaName, UUID tenantId, User user) {
        super(source);
        this.schemaName = schemaName;
        this.tenantId = tenantId;
        this.user = user;
    }
    
}
