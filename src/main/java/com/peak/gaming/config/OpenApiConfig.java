package com.peak.gaming.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Peak Gaming API",
                version = "v1",
                description = "Booking, availability and admin API for the Peak Gaming Suceava room. "
                        + "Most endpoints are public; admin endpoints are individually marked and require the "
                        + AdminAuthInterceptor.HEADER + " header."
        )
)
@SecurityScheme(
        name = "adminKey",
        type = SecuritySchemeType.APIKEY,
        in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER,
        paramName = AdminAuthInterceptor.HEADER,
        description = "Required only on admin endpoints (confirm/cancel a reservation, day stats)."
)
public class OpenApiConfig {
}
