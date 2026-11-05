package dio.speech.shared.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends AppException {
    private static final String DEFAULT_CODE = "EXTERNAL_SERVICE_ERROR";

    public ExternalServiceException(String message) {
        super(DEFAULT_CODE, message, HttpStatus.BAD_GATEWAY);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(DEFAULT_CODE, message, HttpStatus.BAD_GATEWAY, cause);
    }
}
