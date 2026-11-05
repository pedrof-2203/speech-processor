package dio.speech.shared.exception;

import org.springframework.http.HttpStatus;

public class InvalidAudioException extends AppException {
    private static final String DEFAULT_CODE = "INVALID_AUDIO_FILE";

    public InvalidAudioException(String message) {
        super(DEFAULT_CODE, message, HttpStatus.BAD_REQUEST);
    }
}
