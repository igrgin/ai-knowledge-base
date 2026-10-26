package com.aiknowledgebase.support;

import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Database plumbing only; JUnit owns the container for each test class.
 * Boot consumers must close/evict their contexts after the class before this container is reused.
 * Intentionally fails rather than skips when Docker is unavailable.
 */
@Testcontainers
public abstract class PostgresIntegrationTest {
    private static final DockerImageName IMAGE = DockerImageName.parse(
            "pgvector/pgvector:0.8.7-pg17@sha256:ac08538c6f8b9904c33c8224c5e5706dbe760aca29db1d096972b4052c22a75d")
            .asCompatibleSubstituteFor("postgres");

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(IMAGE);

    protected static Database freshDatabase() {
        String name = "test_" + UUID.randomUUID().toString().replace("-", "");
        var admin = new DriverManagerDataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        JdbcClient.create(admin).sql("CREATE DATABASE " + name).update();
        String url = "jdbc:postgresql://" + POSTGRES.getHost() + ":" + POSTGRES.getMappedPort(5432)
                + "/" + name + "?connectTimeout=2&socketTimeout=5";
        return new Database(url, POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    protected record Database(String url, String username, String password) {
        public void registerProperties(DynamicPropertyRegistry registry) {
            registry.add("spring.datasource.url", this::url);
            registry.add("spring.datasource.username", this::username);
            registry.add("spring.datasource.password", this::password);
        }

        public DriverManagerDataSource dataSource() {
            return new DriverManagerDataSource(url, username, password);
        }
    }
}
