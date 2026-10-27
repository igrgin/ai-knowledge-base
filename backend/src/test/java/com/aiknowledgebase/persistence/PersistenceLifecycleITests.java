package com.aiknowledgebase.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.aiknowledgebase.KnowledgeBaseApplication;
import com.aiknowledgebase.support.PostgresIntegrationTest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceLifecycleITests extends PostgresIntegrationTest {
    @Test
    void cleanMigrationThenApplicationRestartKeepsCommittedData() {
        Database database = freshDatabase();
        UUID sentinel = UUID.randomUUID();
        try (var app = start(database)) {
            assertMigrationsComplete(app.getBean(Flyway.class));
            JdbcClient jdbc = app.getBean(JdbcClient.class);
            assertEquals("0.8.7", jdbc.sql("SELECT extversion FROM pg_extension WHERE extname = 'vector'")
                    .query(String.class).single());
            assertEquals(0.0, jdbc.sql("SELECT '[1,2,3]'::vector <-> '[1,2,3]'::vector")
                    .query(Double.class).single());
            createSentinel(jdbc, sentinel);
        }

        try (var restarted = start(database)) {
            assertSentinel(restarted.getBean(JdbcClient.class), sentinel);
            assertMigrationsComplete(restarted.getBean(Flyway.class));
        }
    }

    @Test
    void brokenMigrationPreventsStartupRollsBackDdlAndPreservesExistingData() {
        Database database = freshDatabase();
        UUID sentinel = UUID.randomUUID();
        try (var app = start(database)) {
            createSentinel(app.getBean(JdbcClient.class), sentinel);
        }

        RuntimeException failure = assertThrows(RuntimeException.class,
                () -> start(database, "--spring.flyway.locations=classpath:db/migration,classpath:db/broken-migration"));
        assertTrue(hasCause(failure, FlywayException.class), "Startup must fail because Flyway rejected the test migration");
        JdbcClient jdbc = JdbcClient.create(database.dataSource());
        assertSentinel(jdbc, sentinel);
        assertTrue(jdbc.sql("SELECT to_regclass('migration_must_rollback') IS NULL")
                .query(Boolean.class).single());

        try (var recovered = start(database)) {
            assertMigrationsComplete(recovered.getBean(Flyway.class));
            assertSentinel(recovered.getBean(JdbcClient.class), sentinel);
        }
    }

    private ConfigurableApplicationContext start(Database database, String... extraArgs) {
        List<String> args = new ArrayList<>(List.of(
                "--spring.profiles.active=itest", "--spring.main.banner-mode=off",
                "--spring.datasource.url=" + database.url(),
                "--spring.datasource.username=" + database.username(),
                "--spring.datasource.password=" + database.password()));
        args.addAll(List.of(extraArgs));
        return new SpringApplicationBuilder(KnowledgeBaseApplication.class)
                .web(WebApplicationType.NONE).run(args.toArray(String[]::new));
    }

    private void assertMigrationsComplete(Flyway flyway) {
        assertTrue(Arrays.stream(flyway.info().applied())
                .anyMatch(migration -> "1".equals(migration.getVersion().getVersion())), "V1 must be applied");
        assertEquals(0, flyway.info().pending().length);
    }

    private void createSentinel(JdbcClient jdbc, UUID sentinel) {
        // Test-only committed data; never part of an application migration.
        jdbc.sql("CREATE TABLE restart_sentinel (id uuid PRIMARY KEY)").update();
        jdbc.sql("INSERT INTO restart_sentinel (id) VALUES (:id)").param("id", sentinel).update();
    }

    private void assertSentinel(JdbcClient jdbc, UUID sentinel) {
        assertEquals(sentinel, jdbc.sql("SELECT id FROM restart_sentinel").query(UUID.class).single());
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
