package com.aiknowledgebase.persistence;

import java.util.List;
import java.util.UUID;

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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = KnowledgeBaseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("lvl2test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PersistenceIT extends PostgresIntegrationTest {
    @Autowired private JdbcClient jdbc;
    @Autowired private Flyway flyway;
    @Autowired private PlatformTransactionManager transactionManager;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        freshDatabase().registerProperties(registry);
    }

    @Test
    void cleanMigrationAndJdbcConsumerCommitRollbackAndRelease() {
        assertEquals(List.of("flyway_schema_history"), jdbc.sql("""
                SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename
                """).query(String.class).list());
        assertEquals(1, flyway.info().applied().length);
        assertEquals(0, flyway.info().pending().length);
        assertEquals("0.8.7", jdbc.sql("SELECT extversion FROM pg_extension WHERE extname = 'vector'")
                .query(String.class).single());
        assertEquals(0.0, jdbc.sql("SELECT '[1,2,3]'::vector <-> '[1,2,3]'::vector")
                .query(Double.class).single());

        // A test consumer's schema, never part of an application migration.
        jdbc.sql("""
                CREATE TABLE contract_consumer (
                    resource_id uuid PRIMARY KEY,
                    account_id uuid NOT NULL,
                    request_id uuid NOT NULL UNIQUE
                )
                """).update();
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        UUID accountId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> transaction.executeWithoutResult(status -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            insert(accountId, resourceId, requestId);
            throw new IllegalStateException("rollback this operation");
        }));
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertTrue(TransactionSynchronizationManager.getResourceMap().isEmpty());
        assertEquals(0, jdbc.sql("SELECT count(*) FROM contract_consumer").query(Integer.class).single());

        transaction.executeWithoutResult(status -> insert(accountId, resourceId, requestId));
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertTrue(TransactionSynchronizationManager.getResourceMap().isEmpty(),
                "The JDBC connection must be released before an external call");
        assertEquals(accountId, jdbc.sql("SELECT account_id FROM contract_consumer WHERE resource_id = :id")
                .param("id", resourceId).query(UUID.class).single());
        assertEquals(requestId, jdbc.sql("SELECT request_id FROM contract_consumer WHERE resource_id = :id")
                .param("id", resourceId).query(UUID.class).single());
        assertEquals(1, jdbc.sql("SELECT count(*) FROM contract_consumer").query(Integer.class).single());
    }

    private void insert(UUID accountId, UUID resourceId, UUID requestId) {
        jdbc.sql("INSERT INTO contract_consumer (resource_id, account_id, request_id) VALUES (:resource, :account, :request)")
                .param("resource", resourceId).param("account", accountId).param("request", requestId).update();
    }
}
