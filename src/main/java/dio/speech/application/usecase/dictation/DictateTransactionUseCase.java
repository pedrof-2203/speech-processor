package dio.speech.application.usecase.dictation;

import dio.speech.application.input.CreateTransactionInput;
import dio.speech.application.usecase.transaction.CreateTransactionUseCase;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DictateTransactionUseCase {
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
            - A mensagem deve estar em português e ter no máximo 255 caracteres.
            - Não invente valores ou informações ausentes.
            """;

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;
    private final CreateTransactionUseCase createTransactionUseCase;

    public DictateTransactionUseCase(
            TranscriptionModel transcriptionModel,
            ChatClient chatClient,
            TextToSpeechModel textToSpeechModel,
            CreateTransactionUseCase createTransactionUseCase
    ) {
        this.transcriptionModel = transcriptionModel;
        this.chatClient = chatClient;
        this.textToSpeechModel = textToSpeechModel;
        this.createTransactionUseCase = createTransactionUseCase;
    }

    public Optional<byte[]> execute(Resource audio) {
        var transcription = transcriptionModel.transcribe(audio);

        var transaction = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(transcription)
                .call()
                .entity(CreateTransactionInput.class);

        if (transaction == null) {
            return Optional.empty();
        }

        createTransactionUseCase.execute(transaction);

        if (transaction.output() == null || transaction.output().isBlank()) {
            return Optional.empty();
        }

        return Optional.of(
                textToSpeechModel.call(transaction.output())
        );
    }
}
