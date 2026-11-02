package dio.speech.application.input;

import dio.speech.domain.Category;

public record SaveTransactionInput(String description, long amount, Category category) {
}
