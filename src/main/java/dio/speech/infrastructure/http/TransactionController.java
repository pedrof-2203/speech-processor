package dio.speech.infrastructure.http;

import dio.speech.application.GetTransactionsByCategoryUseCase;
import dio.speech.application.SaveTransactionUseCase;
import dio.speech.domain.Category;
import dio.speech.infrastructure.http.request.TransactionRequest;
import dio.speech.infrastructure.http.response.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final SaveTransactionUseCase saveTransactionUseCase;
    private final GetTransactionsByCategoryUseCase getTransactionsByCategoryUseCase;

    public TransactionController(
            SaveTransactionUseCase saveTransactionUseCase,
            GetTransactionsByCategoryUseCase getTransactionsByCategoryUseCase) {
        this.saveTransactionUseCase = saveTransactionUseCase;
        this.getTransactionsByCategoryUseCase = getTransactionsByCategoryUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@RequestBody TransactionRequest request) {
        var newTransaction = saveTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(newTransaction);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> getByCategory(@PathVariable Category category) {
        return getTransactionsByCategoryUseCase
                .execute(category)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

}
