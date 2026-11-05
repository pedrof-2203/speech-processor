package dio.speech.infrastructure.http.request;

import dio.speech.application.input.UpdateTransactionInput;
import dio.speech.domain.Category;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateTransactionRequest(
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        Category category
) {
    public UpdateTransactionInput toInput() {
        return new UpdateTransactionInput(amount, category);
    }
}
