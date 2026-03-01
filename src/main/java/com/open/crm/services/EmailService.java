package com.open.crm.services;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

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


    public boolean sendEmail(String email, String subject, String body) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message;
        try {
            message = new MimeMessageHelper(mimeMessage, true, "utf-8");
            message.setTo(email);
            message.setFrom(appProperties.getFromEmail());

            message.setText(body);
            message.setSubject(subject);

            javaMailSender.send(mimeMessage);
            return true;
        } catch (MailException | MessagingException e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage());
            return false;
        }
    }

    public boolean sendEmailWithTemplate(String email, String subject, String templateName, Object context) {
        String body = templateEngine.process(templateName, null);

        if (Objects.nonNull(body)) {
                String title = body;
                Matcher matcher = titleHtmlRegex.matcher(title);
                while (matcher.find()) {
                    title = matcher.group();
                }
                subject = title.replaceAll("</?title(.*?)>", "").trim();
            }

        return sendEmail(email, subject, body);
    }
}
