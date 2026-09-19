package com.peak.gaming.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;
    private final AdminAuthInterceptor adminAuthInterceptor;

    public WebConfig(AppProperties appProperties, AdminAuthInterceptor adminAuthInterceptor) {
        this.appProperties = appProperties;
        this.adminAuthInterceptor = adminAuthInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(appProperties.cors().allowedOrigins().split(","))
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor).addPathPatterns("/api/**");
    }
}
