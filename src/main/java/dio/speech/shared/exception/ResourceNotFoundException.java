package dio.speech.shared.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {
    private static final String DEFAULT_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(DEFAULT_CODE, message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(DEFAULT_CODE, String.format("%s with identifier '%s' was not found", resourceName, identifier), HttpStatus.NOT_FOUND);
    }
}
