package com.open.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class EmailConfig {

    @Bean
    public SpringResourceTemplateResolver webTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("/mails/MailMessage");
        return messageSource;
    }

}
