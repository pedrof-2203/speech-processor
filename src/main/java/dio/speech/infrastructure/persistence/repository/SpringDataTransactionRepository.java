package dio.speech.infrastructure.persistence.repository;

import dio.speech.domain.Category;
import dio.speech.domain.Transaction;
import dio.speech.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTransactionRepository
        extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findByCategory(Category category);
}