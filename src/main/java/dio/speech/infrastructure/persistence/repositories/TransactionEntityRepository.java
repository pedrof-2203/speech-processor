package dio.speech.infrastructure.persistence.repositories;

import dio.speech.domain.Category;
import dio.speech.infrastructure.persistence.entities.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository
        extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);
}
