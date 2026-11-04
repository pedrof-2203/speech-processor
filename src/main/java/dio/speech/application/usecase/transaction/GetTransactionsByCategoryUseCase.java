package dio.speech.application.usecase.transaction;

import dio.speech.domain.Category;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionsByCategoryUseCase {
    private final TransactionRepository repository;

    public GetTransactionsByCategoryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "get-transactions-by-category", description = "Lists financial transactions by category.")
    public List<Transaction> execute(@ToolParam(description = "A transaction's category") Category category) {
        return repository.findByCategory(category);
    }
}
