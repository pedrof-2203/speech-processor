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
            You are a financial assistant responsible for interpreting voice-reported financial transactions.
            
            Your task is to:
            1. Extract the reported monetary amount.
            2. Identify the most appropriate category.
            3. Generate a short, natural message confirming the transaction has been recorded.
            
            Rules:
            - Consider the context of the message when identifying the category.
            - The amount must be returned as a BigDecimal.
            - The category must match a valid value from the Category enum.
            - The message must be in English and contain no more than 255 characters.
            - Do not invent missing values or information.
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
