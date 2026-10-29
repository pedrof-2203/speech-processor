package dio.speech;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GROQ_API_KEY", matches = ".+")
public class OpenAIChatModelIT {
    @Autowired
    OpenAiChatModel chatModel;

    @Test
    void shouldReceiveResponseWhenChatModelIsCalled() {
        var response = chatModel.call("Gere um registro de budgeting, com descrição de gastos, valor em reais e local");
        assertThat(response).isNotEmpty();
        System.out.println(response);
    }
}
