package dio.speech.infrastructure.http.request;

import dio.speech.application.input.SaveTransactionInput;
import dio.speech.domain.Category;

public record TransactionRequest(String description, Category category, long amount) {
    public SaveTransactionInput toInput() {
        return new SaveTransactionInput(description, amount, category);
    }
}
