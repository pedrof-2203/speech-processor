package dio.speech.application;

import dio.speech.application.input.SaveTransactionInput;
import dio.speech.application.output.TransactionOutput;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionRepository;

public class SaveTransactionUseCase {
    private final TransactionRepository repository;

    public SaveTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public TransactionOutput execute(SaveTransactionInput input) {
        var newTransaction = repository.save(
                new Transaction(input.description(), input.amount(), input.category())
        );
        return TransactionOutput.from(newTransaction);
    }
}
