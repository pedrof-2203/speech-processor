package dio.speech.application.usecase.transaction;

import dio.speech.application.output.PageResult;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class GetTransactionsUseCase {
    private final TransactionRepository repository;

    public GetTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "get-transactions", description = "Returns all transactions")
    public PageResult<Transaction> execute(int page, int size) {
        return repository.findAllPaginated(page, size);
    }
}
