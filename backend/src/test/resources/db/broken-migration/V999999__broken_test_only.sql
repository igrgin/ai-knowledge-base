-- Exercise PostgreSQL's transactional migration failure, including partial DDL rollback.
CREATE TABLE migration_must_rollback (id uuid PRIMARY KEY);
INSERT INTO deliberately_missing_table VALUES (1);
