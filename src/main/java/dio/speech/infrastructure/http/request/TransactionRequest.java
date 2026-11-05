package dio.speech.infrastructure.http.request;

import dio.speech.application.input.CreateTransactionInput;
import dio.speech.application.input.UpdateTransactionInput;
import dio.speech.domain.Category;
import dio.speech.domain.PaymentMethod;
import dio.speech.domain.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotBlank(message = "Input cannot be blank")
        String input,

        String output,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Category cannot be null")
        Category category,

        @NotNull(message = "Type cannot be null")
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
