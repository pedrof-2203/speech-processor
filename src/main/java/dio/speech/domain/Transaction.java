package dio.speech.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Transaction {
    private TransactionId id;
    private String description;
    private long amount;
    private Category category;
    private final String response;
    private Instant createdAt;
    private Instant updatedAt;

    public Transaction(String description, long amount, Category category, String response) {
        this.id = new TransactionId();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.response = response;
    }
}