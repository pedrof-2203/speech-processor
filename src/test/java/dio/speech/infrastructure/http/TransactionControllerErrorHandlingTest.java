package dio.speech.infrastructure.http;

import dio.speech.application.usecase.transaction.*;
import dio.speech.domain.Category;
import dio.speech.domain.PaymentMethod;
import dio.speech.domain.Type;
import dio.speech.infrastructure.http.exception.GlobalExceptionHandler;
import dio.speech.infrastructure.http.request.UpdateTransactionRequest;
import dio.speech.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerErrorHandlingTest {

    private MockMvc mockMvc;

    @Mock
    private GetTransactionsUseCase getTransactionsUseCase;

    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

    @Mock
    private GetTransactionByIdUseCase getTransactionByIdUseCase;

    @Mock
    private GetTransactionsByCategoryUseCase getTransactionsByCategoryUseCase;

    @Mock
    private UpdateTransactionUseCase updateTransactionUseCase;

    @Mock
    private DeleteTransactionUseCase deleteTransactionUseCase;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 400 VALIDATION_FAILED when creating transaction with empty body")
    void shouldReturn400WhenCreatingWithEmptyBody() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.error.status").value(400))
                .andExpect(jsonPath("$.error.requestId").isNotEmpty())
                .andExpect(jsonPath("$.error.details", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    @DisplayName("Should return 400 VALIDATION_FAILED when amount is negative or zero")
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        String jsonPayload = """
                {
                    "input": "Supermarket",
                    "output": "Recorded",
                    "amount": -15.00,
                    "category": "GROCERIES",
                    "type": "EXPENSE",
                    "paymentMethod": "CASH"
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.error.details[0].field").value("amount"))
                .andExpect(jsonPath("$.error.details[0].message").value("Amount must be greater than zero"));
    }

    @Test
    @DisplayName("Should return 400 MALFORMED_REQUEST_BODY when JSON payload is broken")
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"amount\": invalid-json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST_BODY"));
    }

    @Test
    @DisplayName("Should return 404 RESOURCE_NOT_FOUND with standardized error when transaction does not exist on findById")
    void shouldReturn404WhenTransactionNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(getTransactionByIdUseCase.execute(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/transactions/{uuid}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.error.status").value(404))
                .andExpect(jsonPath("$.error.message").value(containsString(nonExistentId.toString())))
                .andExpect(jsonPath("$.error.requestId").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 400 INVALID_PARAMETER_TYPE when path variable is not a valid UUID")
    void shouldReturn400WhenPathVariableIsNotUUID() throws Exception {
        mockMvc.perform(get("/transactions/not-a-valid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.error.message").value(containsString("uuid")));
    }

    @Test
    @DisplayName("Should return 400 INVALID_PARAMETER_TYPE when category query parameter is invalid enum value")
    void shouldReturn400WhenQueryParamCategoryIsInvalid() throws Exception {
        mockMvc.perform(get("/transactions").param("category", "NON_EXISTENT_CATEGORY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.error.message").value(containsString("category")));
    }

    @Test
    @DisplayName("Should return 404 RESOURCE_NOT_FOUND when updating non-existent transaction")
    void shouldReturn404WhenUpdatingNonExistentTransaction() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String jsonPayload = """
                {
                    "amount": 50.00,
                    "category": "GROCERIES"
                }
                """;

        doThrow(new ResourceNotFoundException("Transaction", nonExistentId))
                .when(updateTransactionUseCase).execute(eq(nonExistentId), any());

        mockMvc.perform(put("/transactions/{uuid}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value(containsString(nonExistentId.toString())));
    }

    @Test
    @DisplayName("Should return 400 VALIDATION_FAILED when updating transaction with negative amount")
    void shouldReturn400WhenUpdatingWithNegativeAmount() throws Exception {
        UUID id = UUID.randomUUID();
        String jsonPayload = """
                {
                    "amount": -25.00,
                    "category": "GROCERIES"
                }
                """;

        mockMvc.perform(put("/transactions/{uuid}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.error.details[0].field").value("amount"));
    }

    @Test
    @DisplayName("Should return 405 METHOD_NOT_ALLOWED when invoking unsupported HTTP method")
    void shouldReturn405WhenUsingPatchMethod() throws Exception {
        mockMvc.perform(patch("/transactions"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("METHOD_NOT_ALLOWED"));
    }
}
