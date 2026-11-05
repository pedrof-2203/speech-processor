package dio.speech.infrastructure.http;

import dio.speech.application.usecase.report.GetTransactionsReportUseCase;
import dio.speech.infrastructure.http.response.ReportResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final GetTransactionsReportUseCase getTransactionsReportUseCase;

    public ReportController(GetTransactionsReportUseCase getTransactionsReportUseCase) {
        this.getTransactionsReportUseCase = getTransactionsReportUseCase;
    }

    @GetMapping
    public ReportResponse getTransactionsReport(
            @RequestParam(required = false)
            LocalDate from,
            @RequestParam(required = false)
            LocalDate to
    ) {
        return ReportResponse.from(getTransactionsReportUseCase.execute(from, to));
    }
}
