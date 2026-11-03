package dio.speech.application.input;

import dio.speech.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

public record SaveTransactionInput(
        @ToolParam(description = "Expense description")
        String description,
        @ToolParam(description = "Expense amount in cents")
        long amount,
        @ToolParam(description = "Expense category")
        Category category
) {
}
