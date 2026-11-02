package dio.speech;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class OpenAISpeechModelIT {
    @Autowired
    OpenAiAudioSpeechModel openAiAudioSpeechModel;

    @Test
    public void shouldGenerateAudioWhenTextIsProvided() throws IOException {
        var response = openAiAudioSpeechModel.call("Olá, tudo bem?");

        assertThat(response).hasSizeGreaterThan(1024);

        var tempFile = Files.createTempFile("AUDIO_", ".mp3");
        Files.write(tempFile, response);
        System.out.print(tempFile.toAbsolutePath());
    }
}
