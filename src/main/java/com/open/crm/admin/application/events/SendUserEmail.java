package com.open.crm.admin.application.events;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class SendUserEmail extends ApplicationEvent {
    private final String email;
    private final String subject;
    private final String templateName;
    private final Map<String, Object> context;

    public SendUserEmail(Object source, String email, String subject, String templateName,
            Map<String, Object> context) {
        super(source);
        this.email = email;
        this.subject = subject;
        this.templateName = templateName;
        this.context = context;
    }

    public SendUserEmail(Object source, String email, String subject, String templateName) {
        this(source, email, subject, templateName, Map.of());
    }
}
