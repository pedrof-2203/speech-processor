package dio.speech.infrastructure.http;

import dio.speech.application.usecase.transaction.*;
import dio.speech.domain.Category;
import dio.speech.infrastructure.http.request.TransactionRequest;
import dio.speech.infrastructure.http.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final GetTransactionsUseCase getTransactionsUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionByIdUseCase getTransactionByIdUseCase;
    private final GetTransactionsByCategoryUseCase getTransactionsByCategoryUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    public TransactionController(
            GetTransactionsUseCase getTransactionsUseCase,
            CreateTransactionUseCase createTransactionUseCase,
            GetTransactionByIdUseCase getTransactionByIdUseCase,
            GetTransactionsByCategoryUseCase getTransactionsByCategoryUseCase,
            UpdateTransactionUseCase updateTransactionUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase
    ) {
        this.getTransactionsUseCase = getTransactionsUseCase;
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionByIdUseCase = getTransactionByIdUseCase;
        this.getTransactionsByCategoryUseCase = getTransactionsByCategoryUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
    }

    @GetMapping
    public List<TransactionResponse> findAll(
            @RequestParam(required = false) Category category
    ) {
        var transactions = category != null
                ? getTransactionsByCategoryUseCase.execute(category)
                : getTransactionsUseCase.execute();

        return transactions.stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TransactionResponse> findById(
            @PathVariable UUID uuid
    ) {
        return getTransactionByIdUseCase.execute(uuid)
                .map(TransactionResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody TransactionRequest request
    ) {
        var transactionId = createTransactionUseCase.execute(
                request.toCreateInput()
        );
        var uri = URI.create("/transactions/" + transactionId);
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Void> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody TransactionRequest request
    ) {
        updateTransactionUseCase.execute(uuid, request.toUpdateInput());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        deleteTransactionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }
}
