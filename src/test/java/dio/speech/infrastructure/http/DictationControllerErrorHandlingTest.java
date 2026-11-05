package dio.speech.infrastructure.http;

import dio.speech.application.usecase.dictation.DictateTransactionUseCase;
import dio.speech.infrastructure.http.exception.GlobalExceptionHandler;
import dio.speech.shared.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DictationControllerErrorHandlingTest {

    private MockMvc mockMvc;

    @Mock
    private DictateTransactionUseCase dictateTransactionUseCase;

    @InjectMocks
    private DictationController dictationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dictationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 400 INVALID_AUDIO_FILE when audio file is empty")
    void shouldReturn400WhenAudioFileIsEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.wav",
                "audio/wav",
                new byte[0]
        );

        mockMvc.perform(multipart("/dictate").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_AUDIO_FILE"))
                .andExpect(jsonPath("$.error.message").value("Audio file is required and cannot be empty"))
                .andExpect(jsonPath("$.error.status").value(400))
                .andExpect(jsonPath("$.error.requestId").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 502 EXTERNAL_SERVICE_ERROR when AI service fails during dictation")
    void shouldReturn502WhenAiServiceFails() throws Exception {
        MockMultipartFile validFile = new MockMultipartFile(
                "file",
                "sample.wav",
                "audio/wav",
                "test-audio-content".getBytes()
        );

        when(dictateTransactionUseCase.execute(any()))
                .thenThrow(new ExternalServiceException("Failed to process transaction using AI model: Connection timeout"));

        mockMvc.perform(multipart("/dictate").file(validFile))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("EXTERNAL_SERVICE_ERROR"))
                .andExpect(jsonPath("$.error.status").value(502))
                .andExpect(jsonPath("$.error.requestId").isNotEmpty());
    }
}
