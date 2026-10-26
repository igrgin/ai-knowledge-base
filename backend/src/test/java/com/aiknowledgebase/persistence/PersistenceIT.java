package com.aiknowledgebase.persistence;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.aiknowledgebase.KnowledgeBaseApplication;
import com.aiknowledgebase.shared.AuthenticatedActor;
import com.aiknowledgebase.support.PostgresIntegrationTest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceIT extends PostgresIntegrationTest {
    @Test
    void cleanMigrationThenApplicationRestartKeepsSchemaAndCommittedUuidData() throws Exception {
        Database database = freshDatabase();
        UUID accountId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        try (var app = startWeb(database)) {
            JdbcClient jdbc = app.getBean(JdbcClient.class);
            assertEquals(List.of("flyway_schema_history"), jdbc.sql("""
                    SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename
                    """).query(String.class).list());
            assertEquals("0.8.7", jdbc.sql("SELECT extversion FROM pg_extension WHERE extname = 'vector'")
                    .query(String.class).single());
            assertEquals(0.0, jdbc.sql("SELECT '[1,2,3]'::vector <-> '[1,2,3]'::vector")
                    .query(Double.class).single());
            createConsumerTable(jdbc);
            TransactionTemplate transaction = new TransactionTemplate(app.getBean(PlatformTransactionManager.class));
            AuthenticatedActor actor = new AuthenticatedActor(accountId);
            transaction.executeWithoutResult(status -> insert(jdbc, actor, resourceId, requestId));
        }

        try (var restarted = startWeb(database)) {
            JdbcClient jdbc = restarted.getBean(JdbcClient.class);
            assertEquals(accountId, jdbc.sql("SELECT account_id FROM contract_consumer WHERE resource_id = :id")
                    .param("id", resourceId).query(UUID.class).single());
            assertEquals(requestId, jdbc.sql("SELECT request_id FROM contract_consumer WHERE resource_id = :id")
                    .param("id", resourceId).query(UUID.class).single());
            assertEquals(1, restarted.getBean(Flyway.class).info().applied().length);
            assertEquals(0, restarted.getBean(Flyway.class).info().pending().length);
            assertPublicErrorContract(restarted);
        }
    }

    @Test
    void existingVectorExtensionFromTheLocalDatabaseIsAccepted() {
        Database database = freshDatabase();
        JdbcClient.create(database.dataSource()).sql("CREATE EXTENSION vector").update();

        try (var app = start(database)) {
            assertEquals(1, app.getBean(Flyway.class).info().applied().length);
            assertEquals("0.8.7", app.getBean(JdbcClient.class)
                    .sql("SELECT extversion FROM pg_extension WHERE extname = 'vector'")
                    .query(String.class).single());
        }
    }

    @Test
    void jdbcConsumerCommitsOrRollsBackBeforeExternalWork() {
        Database database = freshDatabase();
        try (var app = start(database)) {
            JdbcClient jdbc = app.getBean(JdbcClient.class);
            createConsumerTable(jdbc);
            TransactionTemplate transaction = new TransactionTemplate(app.getBean(PlatformTransactionManager.class));
            AuthenticatedActor actor = new AuthenticatedActor(UUID.randomUUID());

            assertThrows(IllegalStateException.class, () -> transaction.executeWithoutResult(status -> {
                assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                insert(jdbc, actor, UUID.randomUUID(), UUID.randomUUID());
                throw new IllegalStateException("rollback this operation");
            }));
            assertEquals(0, jdbc.sql("SELECT count(*) FROM contract_consumer").query(Integer.class).single());

            transaction.executeWithoutResult(status -> insert(jdbc, actor, UUID.randomUUID(), UUID.randomUUID()));
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            assertTrue(TransactionSynchronizationManager.getResourceMap().isEmpty(),
                    "The JDBC connection must be released before an external call");
            assertEquals(1, jdbc.sql("SELECT count(*) FROM contract_consumer").query(Integer.class).single());
        }
    }

    @Test
    void brokenMigrationPreventsStartupRollsBackDdlAndPreservesExistingData() {
        Database database = freshDatabase();
        UUID resourceId = UUID.randomUUID();
        try (var app = start(database)) {
            JdbcClient jdbc = app.getBean(JdbcClient.class);
            createConsumerTable(jdbc);
            insert(jdbc, new AuthenticatedActor(UUID.randomUUID()), resourceId, UUID.randomUUID());
        }

        RuntimeException failure = assertThrows(RuntimeException.class,
                () -> start(database, "--spring.flyway.locations=classpath:db/migration,classpath:db/broken-migration"));
        assertTrue(hasCause(failure, FlywayException.class), "Startup must fail because Flyway rejected V2");
        JdbcClient jdbc = JdbcClient.create(database.dataSource());
        assertEquals(1, jdbc.sql("SELECT count(*) FROM contract_consumer WHERE resource_id = :id")
                .param("id", resourceId).query(Integer.class).single());
        assertTrue(jdbc.sql("SELECT to_regclass('migration_must_rollback') IS NULL")
                .query(Boolean.class).single());

        try (var recovered = start(database)) {
            assertEquals(1, recovered.getBean(Flyway.class).info().applied().length);
        }
    }

    private ConfigurableApplicationContext start(Database database, String... extraArgs) {
        return start(database, WebApplicationType.NONE, extraArgs);
    }

    private ConfigurableApplicationContext startWeb(Database database) {
        return start(database, WebApplicationType.SERVLET);
    }

    private ConfigurableApplicationContext start(Database database, WebApplicationType webType, String... extraArgs) {
        List<String> args = new ArrayList<>(List.of(
                "--spring.profiles.active=lvl2test", "--spring.main.banner-mode=off",
                "--spring.datasource.url=" + database.url(),
                "--spring.datasource.username=" + database.username(),
                "--spring.datasource.password=" + database.password()));
        args.addAll(List.of(extraArgs));
        return new SpringApplicationBuilder(KnowledgeBaseApplication.class)
                .web(webType).run(args.toArray(String[]::new));
    }

    private void assertPublicErrorContract(ConfigurableApplicationContext app) throws Exception {
        String port = app.getEnvironment().getRequiredProperty("local.server.port");
        try (HttpClient client = HttpClient.newHttpClient()) {
            var response = client.send(HttpRequest.newBuilder(
                            URI.create("http://127.0.0.1:" + port + "/api/k0-no-such-route"))
                    .header("X-Request-ID", "untrusted-input").GET().build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
            var body = JsonMapper.builder().build().readTree(response.body());
            assertEquals(3, body.size());
            assertEquals("NOT_FOUND", body.path("code").asString());
            assertEquals("Not Found", body.path("message").asString());
            assertEquals(UUID.fromString(response.headers().firstValue("X-Request-ID").orElseThrow()),
                    UUID.fromString(body.path("requestId").asString()));
        }
    }

    private void createConsumerTable(JdbcClient jdbc) {
        // A test consumer's schema, never part of an application migration.
        jdbc.sql("""
                CREATE TABLE contract_consumer (
                    resource_id uuid PRIMARY KEY,
                    account_id uuid NOT NULL,
                    request_id uuid NOT NULL UNIQUE
                )
                """).update();
    }

    private void insert(JdbcClient jdbc, AuthenticatedActor actor, UUID resourceId, UUID requestId) {
        jdbc.sql("INSERT INTO contract_consumer (resource_id, account_id, request_id) VALUES (:resource, :actor, :request)")
                .param("resource", resourceId).param("actor", actor.accountId()).param("request", requestId).update();
    }

    private boolean hasCause(Throwable failure, Class<? extends Throwable> expected) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            if (expected.isInstance(cause)) {
                return true;
            }
        }
        return false;
    }
}
