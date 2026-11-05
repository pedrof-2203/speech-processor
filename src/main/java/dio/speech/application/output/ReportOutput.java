package dio.speech.application.output;

import java.math.BigDecimal;

public record ReportOutput(BigDecimal balance, BigDecimal income, BigDecimal expenses) {
}
