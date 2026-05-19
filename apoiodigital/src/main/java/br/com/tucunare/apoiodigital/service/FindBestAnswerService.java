package br.com.tucunare.apoiodigital.service;

import br.com.tucunare.apoiodigital.dto.tutorial.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    GenerateContentConfig generateConfig(String rule, Float temp){
        return GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .systemInstruction(Content.fromParts(
                        Part.fromText(rule)
                )).temperature(temp)
                .build();
    }

    <T> T agentStructure(Object request, String rulePath, float temp, Class<T> targetClass){
        String rule = getRules(rulePath);

        GenerateContentConfig config = generateConfig(rule, temp);


        try{

            String input = objectMapper.writeValueAsString(request);

            String response = analyzeText(input, config);

            return objectMapper.readValue(response, targetClass);

        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    IAAgent0TutorialResponseDTO agent0 (IAAgent0TutorialRequestDTO request){
        String rulePath = "src/main/resources/rules/tutorial/agent0-rule.txt";
        return agentStructure(request, rulePath, 0.1f, IAAgent0TutorialResponseDTO.class);

    }

    IAAgent1TutorialResponseDTO agent1 (IAAgent1TutorialRequestDTO request){
        String rulePath = "src/main/resources/rules/tutorial/agent1-rule.txt";
        return agentStructure(request, rulePath, 0.1f, IAAgent1TutorialResponseDTO.class);

    }

    IAAgentYTutorialResponseDTO agentY (IAAgentYTutorialRequestDTO request){
        String rulePath = "src/main/resources/rules/tutorial/agenty-rule.txt";
        return agentStructure(request, rulePath, 0.1f, IAAgentYTutorialResponseDTO.class);

    }

    IAAgentXTutorialResponseDTO agentX (IAAgentXTutorialRequestDTO request){
        String rulePath = "src/main/resources/rules/tutorial/agentx-rule.txt";
        return agentStructure(request, rulePath, 0.1f, IAAgentXTutorialResponseDTO.class);


    }

    IAAgentZTutorialResponseDTO agentZ (IAAgentZTutorialRequestDTO request){
        String rulePath = "src/main/resources/rules/tutorial/agentz-rule.txt";
        return agentStructure(request, rulePath, 0.1f, IAAgentZTutorialResponseDTO.class);



    }




}
