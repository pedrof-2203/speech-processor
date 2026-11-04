package dio.speech.application.input;

import dio.speech.domain.Category;

import java.math.BigDecimal;

public record UpdateTransactionInput(
        BigDecimal amount,
        Category category
) {
}