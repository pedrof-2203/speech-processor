package dio.speech.application.usecase.transaction;

import dio.speech.application.input.CreateTransactionInput;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateTransactionUseCase {
    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "create-transaction", description = "Creates a new transaction")
    public UUID execute(CreateTransactionInput input) {
        var transaction = new Transaction(
                input.input(),
                input.output(),
                input.amount(),
                input.category()
        );
        this.repository.save(transaction);
        return transaction.getId().uuid();
    }
}
