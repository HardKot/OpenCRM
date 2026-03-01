package com.open.crm.components.services;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.open.crm.components.events.ApplicationEmailEvent;
import com.open.crm.config.AppProperties;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        sendEmailWithTemplate(event.getEmail(), event.getSubject(), event.getTemplateName(), event.getContext());
    }

    public void sendEmail(String email, String subject, String body) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message;
        try {
            message = new MimeMessageHelper(mimeMessage, true, "utf-8");
            message.setTo(email);
            message.setFrom(appProperties.getFromEmail());

            message.setText(body);
            message.setSubject(subject);

            javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage());
        }
    }

    public void sendEmailWithTemplate(String email, String subject, String templateName, Object context) {
        String body = templateEngine.process(templateName, null);

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
