package dio.speech.application;

import dio.speech.application.input.SaveTransactionInput;
import dio.speech.application.output.TransactionOutput;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class SaveTransactionUseCase {
    private final TransactionRepository repository;

    public SaveTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "save-transaction", description = "Persists a new financial transaction")
    public TransactionOutput execute(SaveTransactionInput input) {
        var newTransaction = repository.save(
                new Transaction(input.description(), input.amount(), input.category())
        );
        return TransactionOutput.from(newTransaction);
    }
}
