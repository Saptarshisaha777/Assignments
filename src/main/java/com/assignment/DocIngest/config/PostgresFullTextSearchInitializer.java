package com.assignment.DocIngest.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostgresFullTextSearchInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Initializing PostgreSQL full-text search setup...");

            // Add tsvector column
            entityManager.createNativeQuery("""
                ALTER TABLE documents 
                ADD COLUMN IF NOT EXISTS content_vector tsvector
            """).executeUpdate();

            // Create GIN index
            entityManager.createNativeQuery("""
                CREATE INDEX IF NOT EXISTS idx_fts_content 
                ON documents USING GIN (content_vector)
            """).executeUpdate();

            // Update existing data
            entityManager.createNativeQuery("""
                UPDATE documents 
                SET content_vector = to_tsvector('english', content::text)
            """).executeUpdate();

            // Add trigger function for auto-updating content_vector
            entityManager.createNativeQuery("""
                CREATE OR REPLACE FUNCTION documents_vector_update_trigger() 
                RETURNS trigger AS $$
                BEGIN
                  NEW.content_vector := to_tsvector('english', NEW.content);
                  RETURN NEW;
                END
                $$ LANGUAGE plpgsql;
            """).executeUpdate();

            entityManager.createNativeQuery("""
    DROP TRIGGER IF EXISTS trg_content_vector_update ON documents
""").executeUpdate();

            // Create trigger on insert or update
            entityManager.createNativeQuery("""
                CREATE TRIGGER trg_content_vector_update 
                BEFORE INSERT OR UPDATE ON documents 
                FOR EACH ROW EXECUTE FUNCTION documents_vector_update_trigger()
            """).executeUpdate();

            log.info("Full-text search setup completed.");
        } catch (Exception e) {
            log.warn("Full-text search setup skipped or failed: {}", e.getMessage());
        }
    }
}
