package dio.speech.infrastructure.http;

import dio.speech.application.usecase.report.GetTransactionsReportUseCase;
import dio.speech.infrastructure.http.exception.GlobalExceptionHandler;
import dio.speech.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerErrorHandlingTest {

    private MockMvc mockMvc;

    @Mock
    private GetTransactionsReportUseCase getTransactionsReportUseCase;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 400 with BUSINESS_RULE_VIOLATION when 'from' is after 'to'")
    void shouldReturn400WhenFromIsAfterTo() throws Exception {
        LocalDate from = LocalDate.of(2026, 12, 31);
        LocalDate to = LocalDate.of(2026, 1, 1);

        when(getTransactionsReportUseCase.execute(from, to))
                .thenThrow(new BusinessRuleException("INVALID_DATE_RANGE", "'from' cannot be after 'to'"));

        mockMvc.perform(get("/reports")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_DATE_RANGE"))
                .andExpect(jsonPath("$.error.message").value("'from' cannot be after 'to'"))
                .andExpect(jsonPath("$.error.status").value(400))
                .andExpect(jsonPath("$.error.requestId").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 400 INVALID_PARAMETER_TYPE when date format is malformed")
    void shouldReturn400WhenDateFormatIsMalformed() throws Exception {
        mockMvc.perform(get("/reports").param("from", "invalid-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_PARAMETER_TYPE"))
                .andExpect(jsonPath("$.error.message").value(containsString("from")));
    }
}
