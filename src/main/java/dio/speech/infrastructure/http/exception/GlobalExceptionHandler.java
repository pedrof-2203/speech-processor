package dio.speech.infrastructure.http.exception;

import dio.speech.infrastructure.http.filter.RequestIdFilter;
import dio.speech.infrastructure.http.response.ApiErrorResponse;
import dio.speech.infrastructure.http.response.ValidationErrorDetail;
import dio.speech.shared.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        String requestId = resolveRequestId();
        log.warn("Operational exception [requestId={}, code={}, status={}]: {}",
                requestId, ex.getCode(), ex.getStatus(), ex.getMessage());

        List<ValidationErrorDetail> details = null;
        if (ex.getDetails() != null && !ex.getDetails().isEmpty()) {
            details = new ArrayList<>();
            for (Object item : ex.getDetails()) {
                if (item instanceof ValidationErrorDetail validationErrorDetail) {
                    details.add(validationErrorDetail);
                }
            }
        }

        ApiErrorResponse body = ApiErrorResponse.of(
                ex.getCode(),
                ex.getMessage(),
                ex.getStatus().value(),
                request.getRequestURI(),
                requestId,
                details
        );

        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        List<ValidationErrorDetail> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationErrorDetail(
                        fieldError.getField(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        log.warn("Validation failed [requestId={}, path={}]: {} field errors",
                requestId, request.getRequestURI(), validationErrors.size());

        ApiErrorResponse body = ApiErrorResponse.of(
                "VALIDATION_FAILED",
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                requestId,
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        List<ValidationErrorDetail> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .toList();

        log.warn("Constraint violation [requestId={}, path={}]: {}",
                requestId, request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.of(
                "VALIDATION_FAILED",
                "Constraint violation occurred",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                requestId,
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' with value '%s' is invalid. Expected type: %s",
                ex.getName(), ex.getValue(), requiredType);

        log.warn("Type mismatch [requestId={}, path={}]: {}", requestId, request.getRequestURI(), message);

        ApiErrorResponse body = ApiErrorResponse.of(
                "INVALID_PARAMETER_TYPE",
                message,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        log.warn("Malformed JSON payload [requestId={}, path={}]: {}",
                requestId, request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.of(
                "MALFORMED_REQUEST_BODY",
                "Malformed JSON request body or invalid data format",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        String message = String.format("Required request parameter '%s' is missing", ex.getParameterName());

        log.warn("Missing parameter [requestId={}, path={}]: {}", requestId, request.getRequestURI(), message);

        ApiErrorResponse body = ApiErrorResponse.of(
                "MISSING_PARAMETER",
                message,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingPart(
            MissingServletRequestPartException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        String message = String.format("Required request part '%s' is missing", ex.getRequestPartName());

        log.warn("Missing request part [requestId={}, path={}]: {}", requestId, request.getRequestURI(), message);

        ApiErrorResponse body = ApiErrorResponse.of(
                "MISSING_REQUEST_PART",
                message,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        log.warn("Upload size exceeded [requestId={}, path={}]: {}", requestId, request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.of(
                "PAYLOAD_TOO_LARGE",
                "Uploaded file exceeds the maximum allowed size",
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        String message = String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod());

        log.warn("Method not allowed [requestId={}, path={}]: {}", requestId, request.getRequestURI(), message);

        ApiErrorResponse body = ApiErrorResponse.of(
                "METHOD_NOT_ALLOWED",
                message,
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        String message = String.format("Media type '%s' is not supported", ex.getContentType());

        log.warn("Unsupported media type [requestId={}, path={}]: {}", requestId, request.getRequestURI(), message);

        ApiErrorResponse body = ApiErrorResponse.of(
                "UNSUPPORTED_MEDIA_TYPE",
                message,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        log.warn("Resource not found [requestId={}, path={}]: {}", requestId, request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.of(
                "RESOURCE_NOT_FOUND",
                String.format("The requested path '%s' was not found", request.getRequestURI()),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();
        log.warn("Illegal argument [requestId={}, path={}]: {}", requestId, request.getRequestURI(), ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.of(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request
    ) {
        String requestId = resolveRequestId();

        // Critical security requirement: Never leak stack traces, database details or secrets in response.
        // Full details are recorded only in server-side logs.
        log.error("Unexpected server error [requestId={}, path={}]: {}",
                requestId, request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse body = ApiErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "An unexpected internal error occurred. Please contact support referencing request ID: " + requestId,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                requestId
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String resolveRequestId() {
        String requestId = MDC.get(RequestIdFilter.MDC_KEY);
        return (requestId != null && !requestId.isBlank()) ? requestId : UUID.randomUUID().toString();
    }
}
