
package dio.speech.infrastructure.http;

import dio.speech.application.GetTransactionsByCategoryUseCase;
import dio.speech.application.SaveTransactionUseCase;
import dio.speech.domain.Category;
import dio.speech.infrastructure.http.request.TransactionRequest;
import dio.speech.infrastructure.http.response.TransactionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final SaveTransactionUseCase saveTransactionUseCase;
    private final GetTransactionsByCategoryUseCase getTransactionsByCategoryUseCase;

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;

    public TransactionController(
            SaveTransactionUseCase saveTransactionUseCase,
            GetTransactionsByCategoryUseCase getTransactionsByCategoryUseCase,
            TranscriptionModel transcriptionModel,
            @Value("classpath:/prompts/system-message.st") Resource systemPrompt,
            ChatClient.Builder chatClientBuilder,
            TextToSpeechModel textToSpeechModel
    ) throws IOException {

        this.saveTransactionUseCase = saveTransactionUseCase;
        this.getTransactionsByCategoryUseCase = getTransactionsByCategoryUseCase;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;

        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(StandardCharsets.UTF_8))
                .defaultTools(saveTransactionUseCase, getTransactionsByCategoryUseCase)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@RequestBody TransactionRequest request) {
        var transaction = saveTransactionUseCase.execute(request.toInput());

        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> getByCategory(@PathVariable Category category) {
        return getTransactionsByCategoryUseCase.execute(category)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PostMapping(
            value = "/ai",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "audio/wav"
    )
    public ResponseEntity<Resource> transcribe(
            @RequestParam("file") MultipartFile file
    ) {
        var transcription = transcriptionModel.transcribe(file.getResource());

        var response = chatClient.prompt()
                .user(transcription)
                .call()
                .content();

        if (response == null || response.isBlank()) {
            return ResponseEntity.noContent().build();
        }

        var audio = textToSpeechModel.call(response);
        var resource = new ByteArrayResource(audio);

        var contentDisposition = ContentDisposition.attachment()
                .filename("audio.wav")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(resource);
    }
}
