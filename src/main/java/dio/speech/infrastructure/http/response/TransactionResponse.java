package dio.speech.infrastructure.http.response;

import dio.speech.application.output.TransactionOutput;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionResponse(String id, String category, String description, double value) {
    public static TransactionResponse from(TransactionOutput transactionOutput) {
        return new TransactionResponse(
                transactionOutput.id(),
                transactionOutput.category(),
                transactionOutput.description(),
                BigDecimal.valueOf(transactionOutput.value()).setScale(2, RoundingMode.HALF_UP).doubleValue()
        );
    }
}
