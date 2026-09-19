package com.peak.gaming.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Admin admin, Cors cors, Booking booking) {

    public record Admin(String apiKey) {
    }

    public record Cors(String allowedOrigins) {
    }

    public record Booking(int openHour, int closeHour) {
    }
}
