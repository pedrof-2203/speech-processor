package dio.speech;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GROQ_API_KEY", matches = ".+")
public class OpenAIChatClientIT {
    @Autowired
    OpenAiChatModel openAiChatModel;

    @Test
    void shouldExecuteSumWhenPrompted() {
        var chatClient = ChatClient
                .builder(openAiChatModel)
                .defaultSystem("You are a mathematician.")
                .build();

        var response = chatClient
                .prompt("Add 10 and 20, then subtract 30 from the result. Show only the final result.")
                .call()
                .content();

        assertThat(response).contains("0");

        System.out.println(response);
    }
}
