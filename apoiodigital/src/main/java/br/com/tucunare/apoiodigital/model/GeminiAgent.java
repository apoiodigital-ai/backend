package br.com.tucunare.apoiodigital.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GeminiAgent {

    private final ChatClient chatClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String getRules(String filepath) {
        try {
            return Files.readString(Path.of(filepath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T agentStructure(Object request, String rulePath, double temp, Class<T> targetClass) {
        String rule = getRules(rulePath);

        try {
            String input = objectMapper.writeValueAsString(request);
            String response = chatClient.prompt()
                    .system(rule)
                    .user(input)
                    .options(GoogleGenAiChatOptions.builder()
                            .temperature(temp)
                            .build()
                    )
                    .call()
                    .content();

            return objectMapper.readValue(response, targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
