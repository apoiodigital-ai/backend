package br.com.tucunare.apoiodigital.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FindBestAnswerService {


    @Value("${APIKEY}")
    private String apiKey;

    @Value("gemini-2.5-flash-lite")
    private String modelName;

    private Client client;

    @PostConstruct
    public void init() {
        // Initialize the client with your key
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();

    }

    public String analyzeText(String input, GenerateContentConfig config) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    modelName,
                    input,
                    config
            );
            System.out.println("RESPONSE TEXT: " + response.text());


            return response.text();
        } catch (Exception e) {
            // Handle rate limits (429) or connection issues
            return "Error calling Gemini API: " + e.getMessage();
        }
    }

    public String getRules(String filepath){
        try{
            Path caminho = Path.of(filepath);
            String rule = Files.readString(caminho);
//            System.out.println(rule);
            return rule;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    GenerateContentConfig generateConfig(String additionalRules, Float temp){
        return GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .systemInstruction(Content.fromParts(
                        Part.fromText(additionalRules)
                )).temperature(temp)
                .build();
    }




}
