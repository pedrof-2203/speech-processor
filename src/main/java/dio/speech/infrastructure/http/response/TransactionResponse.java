package dio.speech.infrastructure.http.response;

import dio.speech.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        String id,
        String input,
        String output,
        BigDecimal amount,
        String category,
        Instant createdAt,
        Instant updatedAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId().uuid().toString(),
                transaction.getInput(),
                transaction.getOutput(),
                transaction.getAmount(),
                transaction.getCategory().toString(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
