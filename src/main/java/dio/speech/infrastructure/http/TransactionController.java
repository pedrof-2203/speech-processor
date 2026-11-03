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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
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
            ChatClient.Builder chatClientBuilder, TextToSpeechModel textToSpeechModel) throws IOException {
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
        var newTransaction = saveTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(newTransaction);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> getByCategory(@PathVariable Category category) {
        return getTransactionsByCategoryUseCase
                .execute(category)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/wav")
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile multipartFile) {
        var userPrompt = transcriptionModel.transcribe(multipartFile.getResource());

        var result = chatClient.prompt().user(userPrompt).call().content();

        assert result != null;
        byte[] audio = this.textToSpeechModel.call(result);

        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("audio.wav").build().toString())
                .body(resource);

    }
}
