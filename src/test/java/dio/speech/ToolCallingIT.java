package dio.speech;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GROQ_API_KEY", matches = ".+")
public class ToolCallingIT {
    @Autowired
    OpenAiChatModel openAiChatModel;

    static class MathTools {
        @Tool(description = "Sums integers A and B.")
        public int sum(int a, int b) {
            return a + b;
        }

        @Tool(description = "Subtracts integers A and B.")
        public int diff(int a, int b) {
            return a - b;
        }
    }

    @Test
    void shouldExecuteSumWhenPrompted() {
        var chatClient = ChatClient
                .builder(openAiChatModel)
                .defaultSystem("You are a mathematician.")
                .defaultTools(new MathTools())
                .build();

        var response = chatClient
                .prompt("Say hello.")
                .call()
                .content();

        assertThat(response).contains("0");

        System.out.print(response);
    }
}
