package dio.speech.infrastructure.persistence.repository;

import dio.speech.application.output.PageResult;
import dio.speech.domain.Category;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionId;
import dio.speech.domain.TransactionRepository;
import dio.speech.infrastructure.persistence.mapper.TransactionMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
    public PageResult<Transaction> findAllPaginated(int page, int size) {
        var pageable = PageRequest.of(page, size);

        var result = repository.findAll(pageable);

        var transactions = result.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                transactions,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public List<Transaction> findAllByPeriod(Instant from, Instant to) {
        return repository.findAllByPeriod(from, to)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void save(Transaction transaction) {
         var entity = mapper.toEntity(transaction);
         var dbTransaction = repository.save(entity);
        mapper.toDomain(dbTransaction);
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return repository.findById(id.uuid())
                .map(mapper::toDomain);
    }

    @Override
    public PageResult<Transaction> findByCategory(Category category, int page, int size) {
        var pageable = PageRequest.of(page, size);

        var result = repository.findByCategory(category, pageable);

        var transactions = result.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                transactions,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public void delete(TransactionId id) {
        repository.deleteById(id.uuid());
    }
}