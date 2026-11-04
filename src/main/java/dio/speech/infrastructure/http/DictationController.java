package dio.speech.infrastructure.http;

import dio.speech.application.input.CreateTransactionInput;
import dio.speech.application.usecase.transaction.CreateTransactionUseCase;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dictate")
public class DictationController {
    private static final String SYSTEM_PROMPT = """
            Você é um assistente financeiro responsável por interpretar
            transações financeiras informadas por voz.
            
            Sua tarefa é:
            1. Extrair o valor monetário informado.
            2. Identificar a categoria mais adequada.
            3. Gerar uma mensagem curta e natural confirmando o cadastro.
            
            Regras:
            - Considere o contexto da mensagem para identificar a categoria.
            - O valor deve ser retornado como BigDecimal.
            - A categoria deve corresponder a um valor válido do enum Category.
            - A mensagem de confirmação deve estar em português.
            - A mensagem deve ter no máximo 255 caracteres.
            - Não invente valores ou informações ausentes.
            - Se não conseguir identificar o valor, não invente um.
            """;

    private final ChatClient chatClient;
    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;

    private final CreateTransactionUseCase createTransactionUseCase;

    public DictationController(
            ChatClient chatClient,
            TranscriptionModel transcriptionModel,
            TextToSpeechModel textToSpeechModel,
            CreateTransactionUseCase createTransactionUseCase
    ) {
        this.chatClient = chatClient;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
        this.createTransactionUseCase = createTransactionUseCase;
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "audio/wav"
    )
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        var transcription = transcriptionModel.transcribe(file.getResource());

        var input = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(transcription)
                .call()
                .entity(CreateTransactionInput.class);

        if (input == null) {
            return ResponseEntity.noContent().build();
        }

        createTransactionUseCase.execute(input);

        var audio = textToSpeechModel.call(input.output());

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
