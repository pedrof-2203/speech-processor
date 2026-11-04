package dio.speech.domain;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    List<Transaction> findAll();
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(TransactionId id);
    List<Transaction> findByCategory(Category category);
    void delete(TransactionId id);
}
