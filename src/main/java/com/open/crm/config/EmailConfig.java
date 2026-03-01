package com.open.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class EmailConfig {


    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("/mails/MailMessage");
        return messageSource;
    }

}
