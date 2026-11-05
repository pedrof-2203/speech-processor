package dio.speech.infrastructure.http;

import dio.speech.application.usecase.transaction.GetTransactionsUseCase;
import dio.speech.infrastructure.http.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.SQLException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerSecurityTest {

    private MockMvc mockMvc;

    @Mock
    private GetTransactionsUseCase getTransactionsUseCase;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should mask database/unexpected errors and never expose internal stack trace or SQL details to client")
    void shouldNeverExposeDatabaseInternalDetailsOrStackTrace() throws Exception {
        String sensitiveSqlMessage = "SQLSyntaxErrorException: SELECT * FROM secret_table WHERE password='xyz'";
        when(getTransactionsUseCase.execute()).thenAnswer(invocation -> {
            throw new RuntimeException("Database error: " + sensitiveSqlMessage, new SQLException("Raw database credentials exposed"));
        });

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.error.status").value(500))
                .andExpect(jsonPath("$.error.message").value(not(containsString("secret_table"))))
                .andExpect(jsonPath("$.error.message").value(not(containsString("password"))))
                .andExpect(jsonPath("$.error.message").value(not(containsString("SQLException"))))
                .andExpect(jsonPath("$.error.message").value(containsString("An unexpected internal error occurred")))
                .andExpect(jsonPath("$.error.stackTrace").doesNotExist())
                .andExpect(jsonPath("$.error.requestId").isNotEmpty());
    }
}
