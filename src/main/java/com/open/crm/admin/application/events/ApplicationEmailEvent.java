package com.open.crm.admin.application.events;

import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApplicationEmailEvent extends ApplicationEvent {

  private final String email;

  private final String subject;

  private final String templateName;

  private final Map<String, Object> context;

  public ApplicationEmailEvent(
      Object source,
      String email,
      String subject,
      String templateName,
      Map<String, Object> context) {
    super(source);
    this.email = email;
    this.subject = subject;
    this.templateName = templateName;
    this.context = context;
  }
}
