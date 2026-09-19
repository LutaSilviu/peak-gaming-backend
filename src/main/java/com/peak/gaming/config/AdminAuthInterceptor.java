package com.peak.gaming.config;

import com.peak.gaming.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** Rejects requests to @RequireAdminKey endpoints unless X-Admin-Key matches app.admin.api-key. */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public static final String HEADER = "X-Admin-Key";

    private final AppProperties appProperties;

    public AdminAuthInterceptor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (!handlerMethod.hasMethodAnnotation(RequireAdminKey.class)) {
            return true;
        }
        String providedKey = request.getHeader(HEADER);
        String expectedKey = appProperties.admin().apiKey();
        if (expectedKey == null || expectedKey.isBlank() || !expectedKey.equals(providedKey)) {
            throw new UnauthorizedException("Missing or invalid " + HEADER + " header");
        }
        return true;
    }
}
