package com.aiknowledgebase.persistence;

import java.util.Arrays;

import com.aiknowledgebase.KnowledgeBaseApplication;
import com.aiknowledgebase.support.PostgresIntegrationTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = KnowledgeBaseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("itest")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ExistingVectorITests extends PostgresIntegrationTest {
    @Autowired private Flyway flyway;
    @Autowired private JdbcClient jdbc;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        Database database = freshDatabase();
        // Simulate an existing local volume before Boot/Flyway starts.
        JdbcClient.create(database.dataSource()).sql("CREATE EXTENSION vector").update();
        database.registerProperties(registry);
    }

    @Test
    void existingVectorExtensionFromTheLocalDatabaseIsAccepted() {
        assertTrue(Arrays.stream(flyway.info().applied())
                .anyMatch(migration -> "1".equals(migration.getVersion().getVersion())), "V1 must be applied");
        assertEquals(0, flyway.info().pending().length);
        assertEquals("0.8.7", jdbc.sql("SELECT extversion FROM pg_extension WHERE extname = 'vector'")
                .query(String.class).single());
    }
}
