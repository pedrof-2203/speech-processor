package dio.speech.infrastructure.http.request;

import dio.speech.application.input.CreateTransactionInput;
import dio.speech.application.input.UpdateTransactionInput;
import dio.speech.domain.Category;
import dio.speech.domain.PaymentMethod;
import dio.speech.domain.Type;

import java.math.BigDecimal;

public record TransactionRequest(
        String input,
        String output,
        BigDecimal amount,
        Category category,
        Type type,
        PaymentMethod paymentMethod
) {
    public CreateTransactionInput toCreateInput() {
        return new CreateTransactionInput(
                input,
                output,
                amount,
                category,
                type,
                paymentMethod
        );
    }

    public UpdateTransactionInput toUpdateInput() {
        return new UpdateTransactionInput(
                amount,
                category
        );
    }
}