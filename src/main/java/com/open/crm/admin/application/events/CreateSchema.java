package com.open.crm.admin.application.events;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import com.open.crm.admin.entities.user.User;

import lombok.Getter;

@Getter
public class CreateSchema extends ApplicationEvent {

    private final String schemaName;

    private final User user;

    private final UUID tenantId;

    public CreateSchema(Object source, String schemaName, UUID tenantId, User user) {
        super(source);
        this.schemaName = schemaName;
        this.tenantId = tenantId;
        this.user = user;
    }

}
