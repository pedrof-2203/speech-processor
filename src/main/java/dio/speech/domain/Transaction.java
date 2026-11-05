package dio.speech.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Getter
public class Transaction {

    private final TransactionId id;
    private final String input;
    private final String output;
    private final BigDecimal amount;
    private final Category category;
    private final Type type;
    private final PaymentMethod paymentMethod;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Transaction(
            String input,
            String output,
            BigDecimal amount,
            Category category,
            Type type,
            PaymentMethod paymentMethod
    ) {
        this(
                new TransactionId(),
                input,
                output,
                amount,
                category,
                type,
                paymentMethod,
                Instant.now(),
                null
        );
    }

    public Transaction(
            TransactionId id,
            String input,
            String output,
            BigDecimal amount,
            Category category,
            Type type,
            PaymentMethod paymentMethod,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.input = requireNotBlank(input, "Input cannot be blank");
        this.output = output;
        this.amount = validateAmount(amount);
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.paymentMethod = type == Type.INCOME ? null : paymentMethod;
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date cannot be null");
        this.updatedAt = updatedAt;
    }

    public Transaction update(BigDecimal amount, Category category) {
        return new Transaction(
                this.id,
                this.input,
                this.output,
                amount != null ? amount : this.amount,
                category != null ? category : this.category,
                this.type,
                this.paymentMethod,
                this.createdAt,
                Instant.now()
        );
    }

    private static String requireNotBlank(String value, String message) {
        Objects.requireNonNull(value, message);

        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    private static BigDecimal validateAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        return amount;
    }
}