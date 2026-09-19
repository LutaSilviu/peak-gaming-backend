package com.peak.gaming.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a controller method as requiring a valid X-Admin-Key header. Enforced by AdminAuthInterceptor. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireAdminKey {
}
