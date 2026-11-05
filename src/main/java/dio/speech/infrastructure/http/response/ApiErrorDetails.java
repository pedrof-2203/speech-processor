package dio.speech.infrastructure.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorDetails(
        String code,
        String message,
        int status,
        Instant timestamp,
        String path,
        String requestId,
        List<ValidationErrorDetail> details
) {
}
