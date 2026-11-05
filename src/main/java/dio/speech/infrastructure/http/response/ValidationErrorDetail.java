package dio.speech.infrastructure.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationErrorDetail(
        String field,
        Object rejectedValue,
        String message
) {
}
