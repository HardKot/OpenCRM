package com.open.crm.config;

import com.open.crm.tenancy.TenantSprngInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final TenantSprngInterceptor tenantInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(tenantInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/auth/**", "/health", "/actuator/**");
  }
}
