package com.open.crm.components.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.open.crm.config.AppProperties;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private AppProperties appProperties;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(appProperties.getEmail()).thenReturn("from@example.com");
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        emailService.sendEmail("to@example.com", "Subject", "Body");

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    public void testSendEmailWithTemplate() {
        when(templateEngine.process(anyString(), any(IContext.class)))
                .thenReturn("<title>Test Title</title><body>Content</body>");
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(appProperties.getEmail()).thenReturn("from@example.com");
        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        emailService.sendEmailWithTemplate("to@example.com", "Subject", "template", new Context());

        verify(javaMailSender).send(mimeMessage);
    }
}