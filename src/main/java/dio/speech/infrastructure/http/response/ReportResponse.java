package dio.speech.infrastructure.http.response;

import dio.speech.application.output.ReportOutput;

import java.math.BigDecimal;
import java.time.Instant;

public record ReportResponse(
        BigDecimal balance,
        BigDecimal income,
        BigDecimal expenses,
        Instant timestamp
) {
    public static ReportResponse from(ReportOutput reportOutput) {
        return new ReportResponse(
                reportOutput.balance(),
                reportOutput.income(),
                reportOutput.expenses(),
                Instant.now()
        );
    }
}
