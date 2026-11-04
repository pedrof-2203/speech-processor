package dio.speech.application.usecase.transaction;

import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionId;
import dio.speech.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetTransactionByIdUseCase {
    private final TransactionRepository repository;

    public GetTransactionByIdUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "get-transaction-by-id", description = "Finds a transaction by id")
    public Optional<Transaction> execute(UUID uuid) {
        var transactionId = new TransactionId(uuid);
        return this.repository.findById(transactionId);
    }
}
