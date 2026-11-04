package dio.speech.application.usecase.transaction;

import dio.speech.domain.TransactionId;
import dio.speech.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteTransactionUseCase {
    private final TransactionRepository repository;

    public DeleteTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "delete-transaction", description = "Deletes a financial transaction")
    public void execute(UUID uuid) {
        var transactionId = new TransactionId(uuid);
        this.repository.delete(transactionId);
    }
}
