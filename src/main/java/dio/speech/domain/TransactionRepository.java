package dio.speech.domain;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> findAll();
    Transaction save(Transaction transaction);
    List<Transaction> findByCategory(Category category);
}
