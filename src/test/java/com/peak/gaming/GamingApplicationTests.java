package com.peak.gaming;

import com.peak.gaming.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;

class GamingApplicationTests extends PostgresIntegrationTest {

    @Test
    void contextLoads() {
        // Verifies the full application context, including Flyway migrations
        // against a real Postgres instance, starts without error.
    }
}
