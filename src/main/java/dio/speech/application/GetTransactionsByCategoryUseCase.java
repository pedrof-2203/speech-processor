package dio.speech.application;

import dio.speech.application.output.TransactionOutput;
import dio.speech.domain.Category;
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
    public List<TransactionOutput> execute(@ToolParam(description = "A transaction's category") Category category) {
        return repository.findByCategory(category)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
