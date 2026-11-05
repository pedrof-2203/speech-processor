package dio.speech.infrastructure.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        boolean success,
        ApiErrorDetails error
) {
    public static ApiErrorResponse of(
            String code,
            String message,
            int status,
            String path,
            String requestId,
            List<ValidationErrorDetail> details
    ) {
        return new ApiErrorResponse(
                false,
                new ApiErrorDetails(
                        code,
                        message,
                        status,
                        Instant.now(),
                        path,
                        requestId,
                        (details != null && !details.isEmpty()) ? details : null
                )
        );
    }

    public static ApiErrorResponse of(
            String code,
            String message,
            int status,
            String path,
            String requestId
    ) {
        return of(code, message, status, path, requestId, null);
    }
}
