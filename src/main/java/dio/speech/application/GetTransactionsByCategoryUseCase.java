package dio.speech.application;

import dio.speech.application.output.TransactionOutput;
import dio.speech.domain.Category;
import dio.speech.domain.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionsByCategoryUseCase {
    private final TransactionRepository repository;

    public GetTransactionsByCategoryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<TransactionOutput> execute(Category category) {
        return repository.findByCategory(category)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
