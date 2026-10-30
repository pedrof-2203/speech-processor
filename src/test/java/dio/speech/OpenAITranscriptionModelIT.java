package dio.speech;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class OpenAITranscriptionModelIT {
    @Autowired
    OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    @ParameterizedTest
    @CsvSource({
            "01_registrar_despesa.mp3, 58 reais e 90 centavos",
            "02_registrar_receita.mp3, 3.500 reais",
            "03_consultar_saldo.mp3, disponível",
            "04_consultar_gastos.mp3, alimentação",
            "05_definir_orcamento.mp3, 800 reais",
            "06_transferencia.mp3, 250 reais"
    })
    public void shouldExtractCorrectKeywordsFromAudioFile(String fileName, String expectedKeywords) {
        var recording = new ClassPathResource("audio/" + fileName);

        var response = openAiAudioTranscriptionModel.call(recording);

        assertThat(response).contains(expectedKeywords);

        System.out.print(response);
    }
}
