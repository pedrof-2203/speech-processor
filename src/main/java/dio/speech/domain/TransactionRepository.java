package dio.speech.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    List<Transaction> findAll();
    List<Transaction> findAllByPeriod(Instant from, Instant to);
    void save(Transaction transaction);
    Optional<Transaction> findById(TransactionId id);
    List<Transaction> findByCategory(Category category);
    void delete(TransactionId id);
}
