package br.com.tucunare.apoiodigital.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Component;

@Component
public class AiApiClient {

    private final ChatClient chatClient;

    public AiApiClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String callModel (CallModelDTO dto){
        return chatClient.prompt()
                .system(dto.rule())
                .user(dto.input())
                .options(GoogleGenAiChatOptions.builder()
                        .temperature(dto.temp())
                        .build()
                )
                .call()
                .content();
    }

}
