package com.open.crm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
public class AppProperties {
    private Boolean multiTenancyEnabled;
    private String version;
    private String fromEmail;
    private String domain;
}
