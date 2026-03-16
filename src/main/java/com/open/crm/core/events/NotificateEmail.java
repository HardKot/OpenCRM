package com.open.crm.core.events;

public record NotificateEmail(String email, String subject, String templateName, Object context) {}
