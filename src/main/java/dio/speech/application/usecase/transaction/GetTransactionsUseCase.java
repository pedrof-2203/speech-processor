package dio.speech.application.usecase.transaction;

import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionsUseCase {
    private final TransactionRepository repository;

    public GetTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "get-transactions", description = "Returns all transactions")
    public List<Transaction> execute() {
        return this.repository.findAll();
    }
}
