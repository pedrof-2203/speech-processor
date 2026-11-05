package dio.speech.application.usecase.report;

import dio.speech.application.output.ReportOutput;
import dio.speech.domain.Transaction;
import dio.speech.domain.TransactionRepository;
import dio.speech.domain.Type;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class GetTransactionsReportUseCase {
    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    private final TransactionRepository repository;

    public GetTransactionsReportUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public ReportOutput execute(LocalDate from, LocalDate to) {
        Instant start = from != null
                ? from.atStartOfDay(ZONE).toInstant()
                : null;

        Instant end = to != null
                ? to.plusDays(1).atStartOfDay(ZONE).toInstant()
                : null;

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "'from' cannot be after 'to'"
            );
        }

        var dbTransactions = from == null && to == null
                ? repository.findAll()
                : repository.findAllByPeriod(start, end);

        BigDecimal incomeAmount = dbTransactions.stream()
                .filter(transaction -> transaction.getType() == Type.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expensesAmount = dbTransactions.stream()
                .filter(transaction -> transaction.getType() == Type.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = incomeAmount.subtract(expensesAmount);

        return new ReportOutput(balance, incomeAmount, expensesAmount);
    }
}
