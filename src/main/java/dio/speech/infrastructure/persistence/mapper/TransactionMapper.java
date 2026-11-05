package dio.speech.infrastructure.persistence.mapper;

import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionId;
import dio.speech.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(
                transaction.getId().uuid(),
                transaction.getInput(),
                transaction.getOutput(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getType(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                new TransactionId(entity.getId()),
                entity.getInput(),
                entity.getOutput(),
                entity.getAmount(),
                entity.getCategory(),
                entity.getType(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
