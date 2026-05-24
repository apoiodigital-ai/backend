package br.com.tucunare.apoiodigital.agent.providers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class GeminiProvider implements LLMProvider {
    private final ChatClient chatClient;

    public GeminiProvider(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public String generateText(String rule, String prompt, double temp) {
        return chatClient.prompt()
                .system(rule)
                .user(prompt)
                .options(GoogleGenAiChatOptions.builder()
                        .temperature(temp)
                        .build()
                )
                .call()
                .content();
    }
}
