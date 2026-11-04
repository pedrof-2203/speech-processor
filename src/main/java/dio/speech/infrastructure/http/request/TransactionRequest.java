package dio.speech.infrastructure.http.request;

import dio.speech.application.input.CreateTransactionInput;
import dio.speech.application.input.UpdateTransactionInput;
import dio.speech.domain.Category;

import java.math.BigDecimal;

public record TransactionRequest(
        String input,
        String output,
        BigDecimal amount,
        Category category
) {
    public CreateTransactionInput toCreateInput() {
        return new CreateTransactionInput(
                input,
                output,
                amount,
                category
        );
    }

    public UpdateTransactionInput toUpdateInput() {
        return new UpdateTransactionInput(
                amount,
                category
        );
    }
}