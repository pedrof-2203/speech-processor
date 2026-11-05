package dio.speech.infrastructure.persistence.repository;

import dio.speech.domain.Category;
import dio.speech.domain.Transaction;
import dio.speech.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTransactionRepository
        extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findByCategory(Category category);

    @Query("""
    SELECT t FROM TransactionEntity t
    WHERE (:from IS NULL OR t.createdAt >= :from)
    AND (:to IS NULL OR t.createdAt < :to)
    """)
    List<TransactionEntity> findAllByPeriod(
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}