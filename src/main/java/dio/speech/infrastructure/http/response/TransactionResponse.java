package dio.speech.infrastructure.http.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dio.speech.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

@JsonPropertyOrder({
        "id", "category",
        "input", "output",
        "amount", "type", "paymentMethod",
        "createdAt", "updatedAt"
})
public record TransactionResponse(
        String id,
        BigDecimal amount,
        String type,
        String category,
        String paymentMethod,
        String input,
        String output,
        Instant createdAt,
        Instant updatedAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId().uuid().toString(),
                transaction.getAmount(),
                transaction.getType().toString(),
                transaction.getCategory().toString(),
                transaction.getPaymentMethod() != null
                        ? transaction.getPaymentMethod().toString()
                        : null,
                transaction.getInput(),
                transaction.getOutput(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
