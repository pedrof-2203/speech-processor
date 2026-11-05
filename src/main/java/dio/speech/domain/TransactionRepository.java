package dio.speech.domain;

import dio.speech.application.output.PageResult;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    List<Transaction> findAll();

    PageResult<Transaction> findAllPaginated(int page, int size);

    List<Transaction> findAllByPeriod(Instant from, Instant to);

    void save(Transaction transaction);

    Optional<Transaction> findById(TransactionId id);

    PageResult<Transaction> findByCategory(Category category, int page, int size);

    void delete(TransactionId id);
}
