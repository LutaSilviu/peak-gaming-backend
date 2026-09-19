package com.peak.gaming.support;

import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for tests that need a real Postgres instance (Flyway migrations
 * use Postgres-specific SQL, so H2 would not exercise the real schema).
 *
 * Uses the "singleton container" pattern: one Postgres instance for the
 * whole test JVM, started once and never explicitly stopped (Testcontainers'
 * Ryuk reaper cleans it up when the JVM exits). This is deliberate rather
 * than a per-class @Container: JUnit's static-field-per-subclass semantics
 * combined with Spring's ApplicationContext cache otherwise let one test
 * class's cached context outlive another class's already-stopped container,
 * causing connection failures partway through a full suite run.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public abstract class PostgresIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
