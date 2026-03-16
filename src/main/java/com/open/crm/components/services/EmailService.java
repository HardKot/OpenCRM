package com.open.crm.components.services;

import com.open.crm.admin.application.events.ApplicationEmailEvent;
import com.open.crm.config.AppProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final AppProperties appProperties;

  private final SpringTemplateEngine templateEngine;

  private final JavaMailSender javaMailSender;

  private final Pattern titleHtmlRegex = Pattern.compile("<title>(.*?)<\\/title>");

  @Async
  @EventListener
  public void onApplicationEvent(ApplicationEmailEvent event) {
    Context context = new Context();
    for (Map.Entry<String, Object> entry : event.getContext().entrySet()) {
      context.setVariable(entry.getKey(), entry.getValue());
    }

    sendEmailWithTemplate(event.getEmail(), event.getSubject(), event.getTemplateName(), context);
  }

  public void sendEmail(String email, String subject, String body) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper message;
    try {
      message = new MimeMessageHelper(mimeMessage, true, "utf-8");
      message.setTo(email);
      message.setFrom(appProperties.getEmail());

      message.setText(body, true);
      message.setSubject(subject);

      javaMailSender.send(mimeMessage);
    } catch (MailException | MessagingException e) {
      log.error("Failed to send email to {}: {}", email, e.getMessage());
    }
  }

  public void sendEmailWithTemplate(
      String email, String subject, String templateName, IContext context) {
    String body = templateEngine.process(templateName, context);

    if (Objects.nonNull(body)) {
      String title = body;
      Matcher matcher = titleHtmlRegex.matcher(title);
      while (matcher.find()) {
        title = matcher.group();
      }
      subject = title.replaceAll("</?title(.*?)>", "").trim();
    }

    sendEmail(email, subject, body);
  }
}
