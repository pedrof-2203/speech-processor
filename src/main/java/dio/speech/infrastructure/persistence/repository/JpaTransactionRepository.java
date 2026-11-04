package dio.speech.infrastructure.persistence.repository;

import dio.speech.domain.Category;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionId;
import dio.speech.domain.TransactionRepository;
import dio.speech.infrastructure.persistence.mapper.TransactionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final SpringDataTransactionRepository repository;
    private final TransactionMapper mapper;

    public JpaTransactionRepository(SpringDataTransactionRepository repository, TransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Transaction> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Transaction save(Transaction transaction) {
         var entity = mapper.toEntity(transaction);
         var dbTransaction = repository.save(entity);
         return mapper.toDomain(dbTransaction);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return repository.findById(id.uuid())
                .map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findByCategory(Category category) {
        return repository.findByCategory(category)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(TransactionId id) {
        repository.deleteById(id.uuid());
    }
}