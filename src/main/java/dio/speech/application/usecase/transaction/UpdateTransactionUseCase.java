package dio.speech.application.usecase.transaction;

import dio.speech.application.input.UpdateTransactionInput;
import dio.speech.domain.TransactionId;
import dio.speech.domain.TransactionRepository;
import dio.speech.shared.exception.ResourceNotFoundException;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateTransactionUseCase {

    private final TransactionRepository repository;

    public UpdateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "update-transaction", description = "Updates a transaction")
    public void execute(UUID uuid, UpdateTransactionInput input) {
        var transactionId = new TransactionId(uuid);

        var transaction = repository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", uuid));

        var updatedTransaction = transaction.update(
                input.amount(),
                input.category()
        );

        repository.save(updatedTransaction);
    }
}
