package dio.speech.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class BusinessRuleException extends AppException {
    private static final String DEFAULT_CODE = "BUSINESS_RULE_VIOLATION";

    public BusinessRuleException(String message) {
        super(DEFAULT_CODE, message, HttpStatus.BAD_REQUEST);
    }

    public BusinessRuleException(String code, String message) {
        super(code, message, HttpStatus.BAD_REQUEST);
    }

    public BusinessRuleException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }

    public BusinessRuleException(String code, String message, HttpStatus status, List<?> details) {
        super(code, message, status, details);
    }
}
