package dio.speech.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

public abstract class AppException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final List<?> details;

    protected AppException(String code, String message, HttpStatus status) {
        this(code, message, status, Collections.emptyList());
    }

    protected AppException(String code, String message, HttpStatus status, List<?> details) {
        super(message);
        this.code = code;
        this.status = status;
        this.details = details != null ? details : Collections.emptyList();
    }

    protected AppException(String code, String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
        this.details = Collections.emptyList();
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<?> getDetails() {
        return details;
    }
}
